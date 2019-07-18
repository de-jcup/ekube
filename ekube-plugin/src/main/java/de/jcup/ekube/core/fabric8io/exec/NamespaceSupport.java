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

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.ExecutionParameters;
import de.jcup.ekube.core.fabric8io.elementaction.Fabric8ioGenericExecutionAction;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import de.jcup.ekube.core.model.NamespaceContainer;
import io.fabric8.kubernetes.client.KubernetesClient;

public class NamespaceSupport extends AbstractSupport {

    private AddAllChildrenExcecutable addAllChildrenExcecutable = new AddAllChildrenExcecutable();

    public NamespaceSupport(Fabric8ioSupportContext context) {
        super(context);
    }

    public void fill(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer) {
        context.getExecutor().execute(context, addAllChildrenExcecutable, namespaceContainer, client);
    }

    private class AddAllChildrenExcecutable implements Fabric8ioSafeExecutableNoData<NamespaceContainer> {

        @Override
        public Void execute(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer, ExecutionParameters parameters) {
            Fabric8ioGenericExecutionAction<NamespaceContainer, Void> x = new Fabric8ioGenericExecutionAction<>(addAllChildrenExcecutable,
                    EKubeActionIdentifer.REFRESH_CHILDREN, context, client, namespaceContainer);
            namespaceContainer.register(x);

            supportContext.deployments().addDeploymentFromNamespace(namespaceContainer);
            supportContext.pods().addPodsFromNamespace(namespaceContainer);
            supportContext.services().addServicesFromNamespace(namespaceContainer);
            supportContext.volumes().addVolumeClaimsFromNamespace(namespaceContainer);
            supportContext.networks().addNetworkPolicies(namespaceContainer);
            supportContext.configMaps().addConfigMapsFromNamespace(namespaceContainer);
            supportContext.secrets().addSecretsFromNamespace(namespaceContainer);

            return null;
        }

    }
}
