package de.jcup.ekube.core.fabric8io.action;

import de.jcup.ekube.core.fabric8io.PodUtils;
import de.jcup.ekube.core.model.AbstractEKubeElementAction;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import de.jcup.ekube.core.model.PodContainer;
import io.fabric8.kubernetes.api.model.Pod;

public class Fabric8ioRefreshPodAction extends AbstractEKubeElementAction<PodContainer, Pod> {

	public Fabric8ioRefreshPodAction(PodContainer kubeElement, Pod technicalObject) {
		super(EKubeActionIdentifer.REFRESH, kubeElement, technicalObject);
	}

	@Override
	public void execute() {
		PodUtils.updateStatus(technicalObject, kubeElement);
	}

}
