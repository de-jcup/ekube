/*
 * Copyright 2019 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
package de.jcup.ekube.core.fabric8io;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.jcup.ekube.core.EKubeConfiguration;
import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.EKubeContextConfigurationEntry;
import de.jcup.ekube.core.ExecutionParameters;
import de.jcup.ekube.core.SafeExecutable;
import de.jcup.ekube.core.SafeExecutionListener;
import de.jcup.ekube.core.SafeExecutor;
import de.jcup.ekube.core.fabric8io.exec.Fabric8ioSupports;
import de.jcup.ekube.core.model.AbstractEKubeElement;
import de.jcup.ekube.core.model.CurrentContextContainer;
import de.jcup.ekube.core.model.EKubeElement;
import de.jcup.ekube.core.model.EKubeModel;
import de.jcup.ekube.core.model.EKubeModelBuilder;
import de.jcup.ekube.core.model.IngressContainer;
import de.jcup.ekube.core.model.IngressesContainer;
import de.jcup.ekube.core.model.NamespaceContainer;
import de.jcup.ekube.preferences.EKubePreferences;
import io.fabric8.kubernetes.api.model.DoneableNamespace;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceList;
import io.fabric8.kubernetes.api.model.extensions.DoneableIngress;
import io.fabric8.kubernetes.api.model.extensions.Ingress;
import io.fabric8.kubernetes.api.model.extensions.IngressList;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;

public class Fabric8ioEKubeModelBuilder implements EKubeModelBuilder {

    @Override
    public EKubeModel build(EKubeContext context) {
        int totalWork = 0;

        EKubeModel model = new EKubeModel();
        EKubeConfiguration configuration = context.getConfiguration();
        File cubeConfigFile = configuration.getKubeConfigFile();
        System.setProperty(io.fabric8.kubernetes.client.Config.KUBERNETES_KUBECONFIG_FILE, cubeConfigFile.getAbsolutePath());

        String wantedKubernetesContext = configuration.getKubernetesContext();
        EKubeContextConfigurationEntry entry = configuration.findContextConfigurationEntry(wantedKubernetesContext);
        if (entry == null) {
            /* not possible - return emtpy model */
            return model;
        }
        CurrentContextContainer currentContext = setupCurrentContextContainer(model, entry);

        Config config = Config.autoConfigure(wantedKubernetesContext);

        KubernetesClient client = new DefaultKubernetesClient(config);
        Fabric8ioSupports supports = new Fabric8ioSupports(context, client);

        BuildTimeSafeExecutionListener listener = new BuildTimeSafeExecutionListener(supports);

        SafeExecutor executor = context.getExecutor();
        executor.add(listener);

        /*
         * build child content by using current context... if need more we must change
         * the context and rebuild!
         */
        addNamespacesToCurrentContext(context, model, client, supports);

        List<NamespaceContainer> namespaceContainers = currentContext.getNamespaces();
        int size = namespaceContainers.size() + 1;
        context.getProgressHandler().beginTask("Building tree", size);

        context.getProgressHandler().beginSubTask("Inspecting nodes");
        supports.nodes().addnodesFromNamespace(context, client, currentContext.getNodesContainer());
        supports.volumes().addVolumes(context, client, currentContext);
        context.getProgressHandler().worked(totalWork++);

        for (NamespaceContainer namespaceContainer : namespaceContainers) {
            context.getProgressHandler().beginSubTask("Inspecting namespace: " + namespaceContainer.getLabel());

            supports.namespaces().fill(context, client, namespaceContainer);

            context.getProgressHandler().worked(totalWork++);
            // EndpointsList endpointList = client.endpoints().list();
            // List<Endpoints> items = endpointList.getItems();
            // for (Endpoints endpoints: items){
            // endpoints.getMetadata();
            // }
        }

        return model;

    }

    protected CurrentContextContainer setupCurrentContextContainer(EKubeModel model, EKubeContextConfigurationEntry entry) {
        /* setup current context container */
        CurrentContextContainer currentContextContainer = model.getCurrentContext();

        /* use settings from configuration - will be used for connection */
        currentContextContainer.setName(entry.getName());
        currentContextContainer.setCluster(entry.getCluster());
        currentContextContainer.setUser(entry.getUser());
        currentContextContainer.setNamespace(entry.getNamespace());
        return currentContextContainer;
    }

    protected void addNamespacesToCurrentContext(EKubeContext context, EKubeModel model, KubernetesClient client, Fabric8ioSupports supports) {
        CurrentContextContainer currentContextContainer = model.getCurrentContext();
        NonNamespaceOperation<Namespace, NamespaceList, DoneableNamespace, Resource<Namespace, DoneableNamespace>> namespaces = client.namespaces();

        List<Namespace> namespaceItems = new ArrayList<Namespace>();
        String configuredNamespaceName = client.getNamespace();
        
        if (configuredNamespaceName != null) {
            /* when kubernetes config file contains already a namespace for selected context, we can use this one */ 
            try {
                Resource<Namespace, DoneableNamespace> namespaceResource = namespaces.withName(configuredNamespaceName);
                Namespace namespace = namespaceResource.get();
                namespaceItems.add(namespace);
            } catch (Exception e) {
                context.getErrorHandler().logError("Was not able to fetch configured namespace:" + configuredNamespaceName+". Will try to list all instead", e);
            }
        }
        
        if (namespaceItems.isEmpty()) {
            /* this happens when wrong namespeace configured, or no namespace at all - in this case we add all namespaces*/
            NamespaceList namespacesList = namespaces.list();
            namespaceItems.addAll(namespacesList.getItems());
        }

        for (Namespace namespace : namespaceItems) {
            String namespaceName = namespace.getMetadata().getName();
            if (isNamespaceFiltered(context, currentContextContainer, namespaceName)) {
                continue;
            }

            NamespaceContainer namespaceContainer = new NamespaceContainer(namespace.getMetadata().getUid(), namespace);
            namespaceContainer.setName(namespaceName);

            addDefaultActionsToNamespaceAndPredefinedChildren(context, client, supports, namespace, namespaceContainer);
            currentContextContainer.add(namespaceContainer);
        }
        
        if (EKubePreferences.getInstance().areExperimentalFeaturesEnabled()) {
            enableIngressExperimentalFeature(context, client, supports, currentContextContainer);
        }
        
        
        
    }

    private void enableIngressExperimentalFeature(EKubeContext context, KubernetesClient client, Fabric8ioSupports supports, CurrentContextContainer currentContextContainer) {
        try {
            MixedOperation<Ingress, IngressList, DoneableIngress, Resource<Ingress, DoneableIngress>> ingresses = client.extensions().ingresses();
            IngressList ingressList = ingresses.list();
            IngressesContainer ingressesContainer = new IngressesContainer();
            for (Ingress ingress: ingressList.getItems()) {
                IngressContainer ingressC = new IngressContainer(ingress.getMetadata().getUid(), ingress);
                ingressesContainer.addOrReuseExisting(ingressC);
                supports.defaults().appendDefaults(context, client, ingressC);
                ingressC.setStatus(ingress.getStatus().toString());
            }
            currentContextContainer.add(ingressesContainer);
            
            
        }catch(Exception e) {
            context.getErrorHandler().logError("Was not able to fetch ingress list", e);
        }
    }

    /*
     * add default actions to namespace and also to the (predefined) children -
     * necessary, because no actions will be added otherwise
     */
    protected List<EKubeElement> addDefaultActionsToNamespaceAndPredefinedChildren(EKubeContext context, KubernetesClient client, Fabric8ioSupports supports, Namespace namespace,
            NamespaceContainer namespaceContainer) {
        List<EKubeElement> defaultsMissing = new ArrayList<>();
        defaultsMissing.add(namespaceContainer);
        defaultsMissing.addAll(namespaceContainer.getChildren());
        for (EKubeElement element : defaultsMissing) {
            supports.defaults().appendDefaults(context, client, element);
        }
        return defaultsMissing;
    }

    protected boolean isNamespaceFiltered(EKubeContext context, CurrentContextContainer currentContextContainer, String namespaceName) {
        return context.getConfiguration().isNamespaceFiltered(namespaceName);
    }

    /**
     * Special executor which does add default actions after execution - will always
     * rea-add default actions on execution time. Interesting for new added parts on
     * refresh actions
     * 
     * @author Albert Tregnaghi
     *
     */
    private class BuildTimeSafeExecutionListener implements SafeExecutionListener {
        private Fabric8ioSupports supports;

        public BuildTimeSafeExecutionListener(Fabric8ioSupports supports) {
            this.supports = supports;
        }

        @Override
        public <E extends EKubeElement, C, D, R> void afterExecute(EKubeContext context, SafeExecutable<E, C, R> executable, E element, C client, ExecutionParameters parameters) {
            supports.defaults().appendDefaults(supports.getContext(), supports.getClient(), (AbstractEKubeElement) element);

        }
    }
}
