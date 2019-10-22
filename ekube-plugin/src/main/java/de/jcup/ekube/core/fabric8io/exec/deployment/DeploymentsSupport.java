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
 package de.jcup.ekube.core.fabric8io.exec.deployment;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.ExecutionParameters;
import de.jcup.ekube.core.fabric8io.condition.DeploymentConditionInfoBuilder;
import de.jcup.ekube.core.fabric8io.exec.AbstractSupport;
import de.jcup.ekube.core.fabric8io.exec.Fabric8ioSupportContext;
import de.jcup.ekube.core.model.DeploymentContainer;
import de.jcup.ekube.core.model.NamespaceContainer;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;

public class DeploymentsSupport extends AbstractSupport {

    public DeploymentsSupport(Fabric8ioSupportContext context) {
        super(context);
    }

    AddDeploymentsExcecutable addDeploymentsExcecutable = new AddDeploymentsExcecutable(this);
    private UpdateDeploymentsExecutable updateStatus = new UpdateDeploymentsExecutable();
    DeploymentConditionInfoBuilder conditionBuilder = new DeploymentConditionInfoBuilder();
    public void addDeploymentFromNamespace(NamespaceContainer namespaceContainer) {
        this.addDeploymentFromNamespace(getContext(), getClient(), namespaceContainer);
    }

    public void addDeploymentFromNamespace(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer) {
        context.getExecutor().execute(context, addDeploymentsExcecutable, namespaceContainer, client);
    }

    public void updateStatus(EKubeContext context, KubernetesClient client, Deployment deployment, DeploymentContainer nodeContainer) {
        context.getExecutor().execute(context, updateStatus, nodeContainer, client, new ExecutionParameters().set(Deployment.class, deployment));
    }

}
