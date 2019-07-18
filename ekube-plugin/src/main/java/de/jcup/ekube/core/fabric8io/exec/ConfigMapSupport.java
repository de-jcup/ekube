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
 package de.jcup.ekube.core.fabric8io.exec;

import java.util.List;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.ExecutionParameters;
import de.jcup.ekube.core.fabric8io.elementaction.Fabric8ioGenericExecutionAction;
import de.jcup.ekube.core.model.ConfigMapElement;
import de.jcup.ekube.core.model.ConfigMapsContainer;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import de.jcup.ekube.core.model.NamespaceContainer;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapList;
import io.fabric8.kubernetes.client.KubernetesClient;

public class ConfigMapSupport extends AbstractSupport {

    public ConfigMapSupport(Fabric8ioSupportContext context) {
        super(context);
    }

    private UpdateConfigMapsExecutable updateConfigMapsExecutable = new UpdateConfigMapsExecutable();
    private AddConfigMapsFromNamespaceExecutable addConfigMapsFromNamespaceExecutable = new AddConfigMapsFromNamespaceExecutable();

    public void addConfigMapsFromNamespace(NamespaceContainer namespaceContainer) {
        this.addConfigMapsFromNamespace(getContext(), getClient(), namespaceContainer);
    }

    public void addConfigMapsFromNamespace(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer) {
        context.getExecutor().execute(context, addConfigMapsFromNamespaceExecutable, namespaceContainer, client);
    }

    public void updateStatus(EKubeContext context, KubernetesClient client, ConfigMap technicalObject, ConfigMapElement kubeElement) {
        context.getExecutor().execute(context, updateConfigMapsExecutable, kubeElement, client,
                new ExecutionParameters().set(ConfigMap.class, technicalObject));
    }

    private class UpdateConfigMapsExecutable implements Fabric8ioSafeExecutableNoData<ConfigMapElement> {

        @Override
        public Void execute(EKubeContext context, KubernetesClient client, ConfigMapElement kubeElement, ExecutionParameters parameters) {

            ConfigMap configMap = parameters.get(ConfigMap.class);

            // https://kubernetes.io/docs/concepts/services-networking/service/
            kubeElement.setData(configMap.getData());
            kubeElement.setStatus("elements:" + kubeElement.getData().size());

            /* set this itself as action for refresh */
            Fabric8ioGenericExecutionAction<ConfigMapElement, Void> x = new Fabric8ioGenericExecutionAction<>(updateConfigMapsExecutable,
                    EKubeActionIdentifer.REFRESH_STATUS, context, client, kubeElement);
            kubeElement.register(x);

            return null;
        }
    }

    private class AddConfigMapsFromNamespaceExecutable implements Fabric8ioSafeExecutableNoData<NamespaceContainer> {

        @Override
        public Void execute(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer, ExecutionParameters parameters) {
            String namespaceName = namespaceContainer.getName();
            ConfigMapList serviceList = client.configMaps().inNamespace(namespaceName).list();
            List<ConfigMap> items = serviceList.getItems();

            ConfigMapsContainer configMapsContainer = namespaceContainer.fetchConfigMapsContainer();

            /* set this itself as action for rebuild */
            Fabric8ioGenericExecutionAction<NamespaceContainer, Void> x = new Fabric8ioGenericExecutionAction<>(this,
                    EKubeActionIdentifer.REFRESH_CHILDREN, context, client, namespaceContainer);
            configMapsContainer.register(x);

            configMapsContainer.startOrphanCheck(parameters);
            for (ConfigMap configMap : items) {
                ConfigMapElement newElement = new ConfigMapElement(configMap.getMetadata().getUid(), configMap);
                if (! parameters.isHandling(newElement)){
                    continue;
                }
                ConfigMapElement configMapElement = configMapsContainer
                        .AddOrReuseExisting(newElement);
                configMapElement.setName(configMap.getMetadata().getName());
                updateStatus(context, client, configMap, configMapElement);

            }
            configMapsContainer.removeOrphans();
            return null;
        }

    }

}
