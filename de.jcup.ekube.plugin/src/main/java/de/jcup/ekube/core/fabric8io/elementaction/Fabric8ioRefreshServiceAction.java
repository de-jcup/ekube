package de.jcup.ekube.core.fabric8io.elementaction;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import de.jcup.ekube.core.model.ServiceContainer;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.client.KubernetesClient;

public class Fabric8ioRefreshServiceAction extends AbstractFabric8ioElementAction<ServiceContainer, Service> {

	public Fabric8ioRefreshServiceAction(EKubeContext context, KubernetesClient client,
			EKubeActionIdentifer actionIdentifier, ServiceContainer ekubeElement, Service technicalObject) {
		super(context,client, EKubeActionIdentifer.REFRESH, ekubeElement, technicalObject);
	}

	@Override
	public void execute() {
		support.services().updateStatus(context,client,technicalObject, kubeElement);
	}

}
