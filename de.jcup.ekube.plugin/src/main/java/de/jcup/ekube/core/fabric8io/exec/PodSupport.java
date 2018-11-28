package de.jcup.ekube.core.fabric8io.exec;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.model.NamespaceContainer;
import de.jcup.ekube.core.model.PodContainer;
import de.jcup.ekube.core.model.PodsContainer;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.PodStatus;
import io.fabric8.kubernetes.client.KubernetesClient;

public class PodSupport {
	
	private AddPodsFromNamespaceExecutable addPodsFromNamespaceExecutable = new AddPodsFromNamespaceExecutable();
	private UpdatePodsExecutable updateStatus = new UpdatePodsExecutable();

	public void addPodsFromNamespace(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer) {
		context.getExecutor().execute(context, addPodsFromNamespaceExecutable, namespaceContainer, client);
	}
	
	public void updateStatus(EKubeContext context, KubernetesClient client, Pod pod, PodContainer podContainer){
		context.getExecutor().execute(context, updateStatus, podContainer,client,pod);
	}
	
	private class UpdatePodsExecutable implements Fabric8ioSafeExecutable<PodContainer, Pod>{

		@Override
		public void execute(EKubeContext context, KubernetesClient client, PodContainer podContainer, Pod pod) {
			PodStatus status = pod.getStatus();
			podContainer.setStatus(status.getPhase());
		}
		
	}
	private class AddPodsFromNamespaceExecutable implements Fabric8ioSafeExecutableNoData<NamespaceContainer>{

		@Override
		public void execute(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer, Void ignore) {
			String namespaceName = namespaceContainer.getName();
			PodList podList = client.pods().inNamespace(namespaceName).list();
			PodsContainer podsContainer = namespaceContainer.fetchPodsContainer();
			for (Pod pod: podList.getItems()){
				PodContainer podContainer = new PodContainer();
				
				updateStatus(context,client,pod,podContainer);
				
				podContainer.setName(pod.getMetadata().getName());
				podsContainer.add(podContainer);
			}
		}
		
	}
}
