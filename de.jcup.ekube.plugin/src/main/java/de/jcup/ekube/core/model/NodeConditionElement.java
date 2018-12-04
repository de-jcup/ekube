package de.jcup.ekube.core.model;

public class NodeConditionElement extends AbstractEKubeElement implements EKubeStatusElement {

	public NodeConditionElement(String uid) {
		super(uid);
	}

	private String status;

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}
}
