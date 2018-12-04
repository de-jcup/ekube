package de.jcup.ekube.core.model;

public class PodConditionElement extends AbstractEKubeElement implements EKubeStatusElement {

	private String status;

	public PodConditionElement(String uid) {
		super(uid);
	}


	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}
}
