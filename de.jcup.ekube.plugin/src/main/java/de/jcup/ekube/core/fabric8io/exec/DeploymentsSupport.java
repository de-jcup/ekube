package de.jcup.ekube.core.fabric8io.exec;

import java.util.List;

import de.jcup.ekube.core.EKubeContext;
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

	public void addDeploymentFromNamespace(NamespaceContainer namespaceContainer) {
		this.addDeploymentFromNamespace(getContext(), getClient(), namespaceContainer);
	}

	public void addDeploymentFromNamespace(EKubeContext context, KubernetesClient client,
			NamespaceContainer namespaceContainer) {
		context.getExecutor().execute(context, addDeploymentsExcecutable, namespaceContainer, client);
	}

	public void updateStatus(EKubeContext context, KubernetesClient client, Deployment node,
			DeploymentContainer nodeContainer) {
		context.getExecutor().execute(context, updateStatus, nodeContainer, client, node);
	}

	private class UpdateDeploymentsExecutable implements Fabric8ioSafeExecutable<DeploymentContainer, Deployment,Void> {
		@Override
		public Void execute(EKubeContext context, KubernetesClient client, DeploymentContainer nodeContainer,
				Deployment node) {
			StringBuilder sb = new StringBuilder();
			DeploymentStatus status = node.getStatus();
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
		public Void execute(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer,
				Object ignore) {
			String namespace = namespaceContainer.getName();
			MixedOperation<Deployment, DeploymentList, DoneableDeployment, ScalableResource<Deployment, DoneableDeployment>> deployments = client
					.apps().deployments();
			DeploymentList deploymentList = deployments.inNamespace(namespace).list();

			DeploymentsContainer fetchDeploymentsContainer = namespaceContainer.fetchDeploymentsContainer();
			
			/* set this itself as action for rebuild */
			Fabric8ioGenericExecutionAction<NamespaceContainer, Object, Void> x = new Fabric8ioGenericExecutionAction<>(addDeploymentsExcecutable, EKubeActionIdentifer.REFRESH_CHILDREN, context, client, namespaceContainer, ignore);
			fetchDeploymentsContainer.register(x);
			
			
			fetchDeploymentsContainer.startOrphanCheck();
			for (Deployment deployment : deploymentList.getItems()) {
				DeploymentContainer deploymentContainer = new DeploymentContainer(deployment.getMetadata().getUid());
				if (namespaceContainer.isAlreadyFoundAndSoNoOrphan(deploymentContainer)) {
					continue;
				}
				deploymentContainer.setName(deployment.getMetadata().getName());
				fetchDeploymentsContainer.add(deploymentContainer);
				updateStatus(context, client, deployment, deploymentContainer);

				/* add node conditions */
				List<DeploymentCondition> conditions = deployment.getStatus().getConditions();
				for (DeploymentCondition condition : conditions) {
					DeploymentConditionElement element = new DeploymentConditionElement(
							deployment.getMetadata().getUid() + "#" + condition.getType());
					element.setName(condition.getType());

					StringBuilder sb = new StringBuilder();
					sb.append(condition.getStatus());
					sb.append(" ");
					sb.append(condition.getMessage());
					sb.append(" ");
					sb.append(condition.getLastUpdateTime());
					element.setStatus(sb.toString());

					deploymentContainer.add(element);
				}

			}
			fetchDeploymentsContainer.removeOrphans();
			return null;
		}

	}

}
