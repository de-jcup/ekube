package de.jcup.ekube.core.fabric8io.elementaction;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import de.jcup.ekube.core.model.PodContainer;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;

public class Fabric8ioRefreshPodStatusAction extends AbstractFabric8ioElementAction<PodContainer, Pod,Void> {

	public Fabric8ioRefreshPodStatusAction(EKubeContext context, KubernetesClient client, PodContainer kubeElement, Pod technicalObject) {
		super(context,client, EKubeActionIdentifer.REFRESH_STATUS, kubeElement, technicalObject);
	}

	@Override
	public Void execute() {
		support.pods().updateStatus(context,client,technicalObject, kubeElement);
		return null;
	}

}
