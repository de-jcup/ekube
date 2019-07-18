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
import de.jcup.ekube.core.fabric8io.condition.ConditionInfo;
import de.jcup.ekube.core.fabric8io.condition.ConditionInfoUtil;
import de.jcup.ekube.core.fabric8io.condition.ConditionInfoUtil.ConditionInfoStatus;
import de.jcup.ekube.core.fabric8io.condition.DeploymentConditionInfoBuilder;
import de.jcup.ekube.core.fabric8io.elementaction.Fabric8ioGenericExecutionAction;
import de.jcup.ekube.core.model.DeploymentConditionElement;
import de.jcup.ekube.core.model.DeploymentContainer;
import de.jcup.ekube.core.model.DeploymentsContainer;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import de.jcup.ekube.core.model.NamespaceContainer;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentCondition;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.fabric8.kubernetes.api.model.apps.DeploymentStatus;
import io.fabric8.kubernetes.api.model.apps.DoneableDeployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.ScalableResource;

public class DeploymentsSupport extends AbstractSupport {

    public DeploymentsSupport(Fabric8ioSupportContext context) {
        super(context);
    }

    private AddDeploymentsExcecutable addDeploymentsExcecutable = new AddDeploymentsExcecutable();
    private UpdateDeploymentsExecutable updateStatus = new UpdateDeploymentsExecutable();
    private DeploymentConditionInfoBuilder conditionBuilder = new DeploymentConditionInfoBuilder();
    public void addDeploymentFromNamespace(NamespaceContainer namespaceContainer) {
        this.addDeploymentFromNamespace(getContext(), getClient(), namespaceContainer);
    }

    public void addDeploymentFromNamespace(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer) {
        context.getExecutor().execute(context, addDeploymentsExcecutable, namespaceContainer, client);
    }

    public void updateStatus(EKubeContext context, KubernetesClient client, Deployment deployment, DeploymentContainer nodeContainer) {
        context.getExecutor().execute(context, updateStatus, nodeContainer, client, new ExecutionParameters().set(Deployment.class, deployment));
    }

    private class UpdateDeploymentsExecutable implements Fabric8ioSafeExecutable<DeploymentContainer, Void> {
        @Override
        public Void execute(EKubeContext context, KubernetesClient client, DeploymentContainer nodeContainer, ExecutionParameters parameters) {
            StringBuilder sb = new StringBuilder();
            DeploymentStatus status = parameters.get(Deployment.class).getStatus();
            sb.append("Replicas:" + status.getReplicas());
            if (status.getAvailableReplicas() != null) {
                sb.append(",avail:" + status.getAvailableReplicas());
            }
            if (status.getUnavailableReplicas() != null) {
                sb.append(",unavail:" + status.getUnavailableReplicas());
            }
            nodeContainer.setStatus(sb.toString());
            return null;
        }

    }

    private class AddDeploymentsExcecutable implements Fabric8ioSafeExecutableNoData<NamespaceContainer> {

        @Override
        public Void execute(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer, ExecutionParameters parameters) {
            String namespace = namespaceContainer.getName();
            MixedOperation<Deployment, DeploymentList, DoneableDeployment, ScalableResource<Deployment, DoneableDeployment>> deployments = client
                    .apps().deployments();
            DeploymentList deploymentList = deployments.inNamespace(namespace).list();

            DeploymentsContainer fetchDeploymentsContainer = namespaceContainer.fetchDeploymentsContainer();

            /* set this itself as action for rebuild */
            Fabric8ioGenericExecutionAction<NamespaceContainer, Void> x = new Fabric8ioGenericExecutionAction<>(addDeploymentsExcecutable,
                    EKubeActionIdentifer.REFRESH_CHILDREN, context, client, namespaceContainer);

            fetchDeploymentsContainer.register(x);

            fetchDeploymentsContainer.startOrphanCheck(parameters);
            for (Deployment deployment : deploymentList.getItems()) {
                DeploymentContainer newElement = new DeploymentContainer(deployment.getMetadata().getUid(), deployment);
                if (!parameters.isHandling(newElement)) {
                    continue;
                }
                DeploymentContainer deploymentContainer = fetchDeploymentsContainer.AddOrReuseExisting(newElement);
                deploymentContainer.setName(deployment.getMetadata().getName());
                updateStatus(context, client, deployment, deploymentContainer);

                /* add node conditions */
                List<DeploymentCondition> conditions = deployment.getStatus().getConditions();
                ConditionInfoStatus status = ConditionInfoUtil.createStatus();
                for (DeploymentCondition condition : conditions) {
                    DeploymentConditionElement element = new DeploymentConditionElement(deployment.getMetadata().getUid() + "#" + condition.getType(),
                            condition);
                    element.setName(condition.getType());
                    ConditionInfo buildInfo = conditionBuilder.buildInfo(condition);
                    element.setInfo(buildInfo);
                    status.handle(buildInfo);
                    
                    deploymentContainer.add(element);
                }
                status.handleErrors(deploymentContainer);
               
            }
            fetchDeploymentsContainer.removeOrphans();
            return null;
        }

    }

}
