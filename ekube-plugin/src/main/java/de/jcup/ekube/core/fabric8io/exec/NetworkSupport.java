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
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import de.jcup.ekube.core.model.NamespaceContainer;
import de.jcup.ekube.core.model.NetworkPolicyElement;
import de.jcup.ekube.core.model.NetworksContainer;
import io.fabric8.kubernetes.api.model.networking.NetworkPolicy;
import io.fabric8.kubernetes.api.model.networking.NetworkPolicyList;
import io.fabric8.kubernetes.client.KubernetesClient;

public class NetworkSupport extends AbstractSupport {

    public NetworkSupport(Fabric8ioSupportContext context) {
        super(context);
    }

    private AddNetworksFromNamespaceExecutable addNetworksFromNamespaceExecutable = new AddNetworksFromNamespaceExecutable();

    public void addNetworkPolicies(NamespaceContainer namespaceContainer) {
        this.addNetworkPolicies(getContext(), getClient(), namespaceContainer);
    }

    public void addNetworkPolicies(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer) {
        context.getExecutor().execute(context, addNetworksFromNamespaceExecutable, namespaceContainer, client);
    }

    private class AddNetworksFromNamespaceExecutable implements Fabric8ioSafeExecutableNoData<NamespaceContainer> {

        @Override
        public Void execute(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer, ExecutionParameters parameters) {
            String namespaceName = namespaceContainer.getName();
            NetworkPolicyList networkPoliciesList = client.network().networkPolicies().inNamespace(namespaceName).list();
            List<NetworkPolicy> items = networkPoliciesList.getItems();
            NetworksContainer fetchNetworksContainer = namespaceContainer.fetchNetworksContainer();

            /* set this itself as action for rebuild */
            Fabric8ioGenericExecutionAction<NamespaceContainer, Void> addNetworksFromNamespaceAction = new Fabric8ioGenericExecutionAction<>(addNetworksFromNamespaceExecutable,
                    EKubeActionIdentifer.REFRESH_CHILDREN, context, client, namespaceContainer);
            fetchNetworksContainer.register(addNetworksFromNamespaceAction);

            fetchNetworksContainer.startOrphanCheck(parameters);
            for (NetworkPolicy networkPolicy : items) {
                NetworkPolicyElement newElement = new NetworkPolicyElement(networkPolicy.getMetadata().getUid(), networkPolicy);
                if (!parameters.isHandling(newElement)) {
                    continue;
                }
                NetworkPolicyElement networkPolicyElement = fetchNetworksContainer.addOrReuseExisting(newElement);
                networkPolicyElement.setName(networkPolicy.getMetadata().getName());
                
                /* this is not added by own action, which does automatically add default so add default by our own:*/
                supportContext.defaults().appendDefaults(context, client, networkPolicyElement);
                
            }
            fetchNetworksContainer.removeOrphans();
            return null;
        }

    }

}
