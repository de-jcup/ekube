package de.jcup.ekube.core.fabric8io.exec.deployment;

import java.util.List;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.ExecutionParameters;
import de.jcup.ekube.core.fabric8io.condition.ConditionInfo;
import de.jcup.ekube.core.fabric8io.condition.ConditionInfoUtil;
import de.jcup.ekube.core.fabric8io.condition.ConditionInfoUtil.ConditionInfoStatus;
import de.jcup.ekube.core.fabric8io.elementaction.Fabric8ioGenericExecutionAction;
import de.jcup.ekube.core.fabric8io.exec.Fabric8ioSafeExecutableNoData;
import de.jcup.ekube.core.model.DeploymentConditionElement;
import de.jcup.ekube.core.model.DeploymentContainer;
import de.jcup.ekube.core.model.DeploymentsContainer;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import de.jcup.ekube.core.model.NamespaceContainer;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentCondition;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.fabric8.kubernetes.api.model.apps.DoneableDeployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.ScalableResource;

class AddDeploymentsExcecutable implements Fabric8ioSafeExecutableNoData<NamespaceContainer> {

    /**
     * 
     */
    private final DeploymentsSupport deploymentsSupport;

    /**
     * @param deploymentsSupport
     */
    AddDeploymentsExcecutable(DeploymentsSupport deploymentsSupport) {
        this.deploymentsSupport = deploymentsSupport;
    }

    @Override
    public Void execute(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer, ExecutionParameters parameters) {
        String namespace = namespaceContainer.getName();
        MixedOperation<Deployment, DeploymentList, DoneableDeployment, ScalableResource<Deployment, DoneableDeployment>> deployments = client
                .apps().deployments();
        DeploymentList deploymentList = deployments.inNamespace(namespace).list();

        DeploymentsContainer fetchDeploymentsContainer = namespaceContainer.fetchDeploymentsContainer();

        /* set this itself as action for rebuild */
        Fabric8ioGenericExecutionAction<NamespaceContainer, Void> x = new Fabric8ioGenericExecutionAction<>(this.deploymentsSupport.addDeploymentsExcecutable,
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
            this.deploymentsSupport.updateStatus(context, client, deployment, deploymentContainer);

            /* add node conditions */
            List<DeploymentCondition> conditions = deployment.getStatus().getConditions();
            ConditionInfoStatus status = ConditionInfoUtil.createStatus();
            for (DeploymentCondition condition : conditions) {
                DeploymentConditionElement element = new DeploymentConditionElement(deployment.getMetadata().getUid() + "#" + condition.getType(),
                        condition);
                element.setName(condition.getType());
                ConditionInfo buildInfo = this.deploymentsSupport.conditionBuilder.buildInfo(condition);
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