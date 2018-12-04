package de.jcup.ekube.core.fabric8io.exec;

import java.util.List;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.fabric8io.elementaction.Fabric8ioGenericExecutionAction;
import de.jcup.ekube.core.fabric8io.elementaction.Fabric8ioRefreshPodStatusAction;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import de.jcup.ekube.core.model.NamespaceContainer;
import de.jcup.ekube.core.model.PodConditionElement;
import de.jcup.ekube.core.model.PodContainer;
import de.jcup.ekube.core.model.PodsContainer;
import io.fabric8.kubernetes.api.model.PodCondition;
import io.fabric8.kubernetes.api.model.ContainerStatus;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.PodStatus;
import io.fabric8.kubernetes.client.KubernetesClient;

public class PodSupport extends AbstractSupport{
	
	public PodSupport(Fabric8ioSupportContext context) {
		super(context);
	}
	
	private AddPodsFromNamespaceExecutable addPodsFromNamespaceExecutable = new AddPodsFromNamespaceExecutable();
	private UpdatePodsExecutable updateStatus = new UpdatePodsExecutable();

	public void addPodsFromNamespace(NamespaceContainer namespaceContainer) {
		this.addPodsFromNamespace(getContext(), getClient(), namespaceContainer);
	}
	
	public void addPodsFromNamespace(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer) {
		context.getExecutor().execute(context, addPodsFromNamespaceExecutable, namespaceContainer, client);
	}
	
	public void updateStatus(EKubeContext context, KubernetesClient client, Pod pod, PodContainer podContainer){
		context.getExecutor().execute(context, updateStatus, podContainer,client,pod);
	}
	
	private class UpdatePodsExecutable implements Fabric8ioSafeExecutable<PodContainer, Pod,Void>{

		@Override
		public Void execute(EKubeContext context, KubernetesClient client, PodContainer podContainer, Pod pod) {
			PodStatus status = pod.getStatus();
			List<ContainerStatus> containerStatuses = status.getContainerStatuses();
			int ready=0;
			int count=0;
			for (ContainerStatus scs : containerStatuses){
				count++;
				if (Boolean.TRUE.equals(scs.getReady())){
					ready++;
				}
			}
			podContainer.setStatus("Ready: "+ready+"/"+count);
			return null;
		}
		
	}
	private class AddPodsFromNamespaceExecutable implements Fabric8ioSafeExecutableNoData<NamespaceContainer>{

		@Override
		public Void execute(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer, Object ignore) {
			String namespaceName = namespaceContainer.getName();
			PodList podList = client.pods().inNamespace(namespaceName).list();
			PodsContainer podsContainer = namespaceContainer.fetchPodsContainer();
			
			/* set this itself as action for rebuild */
			Fabric8ioGenericExecutionAction<NamespaceContainer, Object, Void> x = new Fabric8ioGenericExecutionAction<>(addPodsFromNamespaceExecutable, EKubeActionIdentifer.REFRESH_CHILDREN, context, client, namespaceContainer, ignore);
			podsContainer.register(x);
			
			podsContainer.startOrphanCheck();
			for (Pod pod: podList.getItems()){
				PodContainer podContainer = new PodContainer(pod.getMetadata().getUid());
				if (podsContainer.isAlreadyFoundAndSoNoOrphan(podContainer)){
					continue;
				}
				updateStatus(context,client,pod,podContainer);
				
				podContainer.setName(pod.getMetadata().getName());
				podsContainer.add(podContainer);
				
				Fabric8ioRefreshPodStatusAction action = new Fabric8ioRefreshPodStatusAction(context,client,podContainer,pod);
				podContainer.register(action);
				
				
				List<PodCondition> conditions = pod.getStatus().getConditions();
				for (PodCondition condition: conditions){
					PodConditionElement element = new PodConditionElement(pod.getMetadata().getUid()+"#"+condition.getType());
					element.setName(condition.getType());
					
					StringBuilder sb = new StringBuilder();
					sb.append(condition.getStatus());
					sb.append(" ");
					sb.append(condition.getMessage());
					sb.append(" ");
					sb.append(condition.getLastProbeTime());
					element.setStatus(sb.toString());
					
					podContainer.add(element);
				}
			
			}
			podsContainer.removeOrphans();
			return null;
		}
		
	}
	
}
