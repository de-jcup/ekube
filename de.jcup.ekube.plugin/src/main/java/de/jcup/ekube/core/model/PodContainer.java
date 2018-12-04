package de.jcup.ekube.core.model;

public class PodContainer extends AbstractEKubeContainer implements EKubeStatusElement {

	public PodContainer(String uid) {
		super(uid);
	}

	private String status;

	public void add(DockerElement docker) {
		addChild(docker);
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void add(PodConditionElement element) {
		addChild(element);
	}
}
