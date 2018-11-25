package de.jcup.ekube.core.fabric8io.action;

import java.util.List;

import de.jcup.ekube.core.fabric8io.ServiceUtils;
import de.jcup.ekube.core.model.AbstractEKubeElementAction;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import de.jcup.ekube.core.model.ServiceContainer;
import io.fabric8.kubernetes.api.model.LoadBalancerIngress;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceStatus;

public class Fabric8ioRefreshServiceAction extends AbstractEKubeElementAction<ServiceContainer, Service> {

	public Fabric8ioRefreshServiceAction(ServiceContainer kubeElement, Service technicalObject) {
		super(EKubeActionIdentifer.REFRESH, kubeElement, technicalObject);
	}

	@Override
	public void execute() {
		ServiceUtils.updateStatus(technicalObject, kubeElement);
	}

}
