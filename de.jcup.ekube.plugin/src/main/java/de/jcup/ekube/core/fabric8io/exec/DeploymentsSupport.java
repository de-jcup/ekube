package de.jcup.ekube.core.fabric8io.exec;

import java.util.List;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.model.DeploymentConditionElement;
import de.jcup.ekube.core.model.DeploymentContainer;
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
		this.addDeploymentFromNamespace(getContext(),getClient(),namespaceContainer);
	}
	
	public void addDeploymentFromNamespace(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer) {
		context.getExecutor().execute(context, addDeploymentsExcecutable, namespaceContainer, client);
	}
	
	public void updateStatus(EKubeContext context, KubernetesClient client, Deployment node, DeploymentContainer nodeContainer){
		context.getExecutor().execute(context, updateStatus, nodeContainer,client,node);
	}
	
	private class UpdateDeploymentsExecutable implements Fabric8ioSafeExecutable<DeploymentContainer, Deployment>{
		@Override
		public void execute(EKubeContext context, KubernetesClient client, DeploymentContainer nodeContainer, Deployment node) {
			StringBuilder sb = new StringBuilder();
			DeploymentStatus status = node.getStatus();
			sb.append("Replicas:"+status.getReplicas());
			if (status.getAvailableReplicas()!=null){
				sb.append(",avail:"+status.getAvailableReplicas());
			}
			if (status.getUnavailableReplicas()!=null){
				sb.append(",unavail:"+status.getUnavailableReplicas());
			}	
			nodeContainer.setStatus(sb.toString());
		}
		
	}
	
	private class AddDeploymentsExcecutable implements Fabric8ioSafeExecutableNoData<NamespaceContainer>{

		@Override
		public void execute(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer, Void ignore) {
			String namespace = namespaceContainer.getName();
			MixedOperation<Deployment, DeploymentList, DoneableDeployment, ScalableResource<Deployment, DoneableDeployment>> deployments = client.apps().deployments();
			DeploymentList deploymentList = deployments.inNamespace(namespace).list();
			for (Deployment deployment: deploymentList.getItems()){
				DeploymentContainer deploymentContainer = new DeploymentContainer();
				deploymentContainer.setName(deployment.getMetadata().getName());
				namespaceContainer.fetchDeploymentsContainer().add(deploymentContainer);
				updateStatus(context,client,deployment,deploymentContainer);
				
				/* add node conditions */
				List<DeploymentCondition> conditions = deployment.getStatus().getConditions();
				for (DeploymentCondition condition: conditions){
					DeploymentConditionElement element = new DeploymentConditionElement();
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
		}
		
	}

	
}
