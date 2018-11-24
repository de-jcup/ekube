package de.jcup.ekube.core.model;

public class PodContainer extends AbstractEKubeContainer implements EKubeStatusElement {

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
}
