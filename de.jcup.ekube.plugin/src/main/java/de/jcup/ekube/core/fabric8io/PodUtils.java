package de.jcup.ekube.core.fabric8io;

import com.google.protobuf.Descriptors.Descriptor;

import de.jcup.ekube.core.model.NamespaceContainer;
import de.jcup.ekube.core.model.PodContainer;
import de.jcup.ekube.core.model.PodsContainer;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.PodStatus;
import io.fabric8.kubernetes.client.KubernetesClient;

public class PodUtils {

	public static void addPodsFromNamespace(KubernetesClient client, NamespaceContainer namespaceContainer) {
		String namespaceName = namespaceContainer.getName();
		PodList podList = client.pods().inNamespace(namespaceName).list();
		PodsContainer podsContainer = namespaceContainer.fetchPodsContainer();
		for (Pod pod: podList.getItems()){
			PodContainer podContainer = new PodContainer();
			updateStatus(pod, podContainer);
			podContainer.setLabel(pod.getMetadata().getName());
			podsContainer.add(podContainer);
			
		}
	}
	
	public static void updateStatus(Pod pod, PodContainer podContainer){
		PodStatus status = pod.getStatus();
		podContainer.setStatus(status.getPhase());
	}
}
