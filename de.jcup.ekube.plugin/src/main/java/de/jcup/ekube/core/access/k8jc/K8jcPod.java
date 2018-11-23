package de.jcup.ekube.core.access.k8jc;

import de.jcup.ekube.core.access.Pod;

public class K8jcPod implements Pod{

	String name;
	private K8jcKubernetes kubernetes;

	public K8jcPod(K8jcKubernetes kubernetes) {
		this.kubernetes=kubernetes;
	}

	@Override
	public String getName() {
		return name;
	}

}
