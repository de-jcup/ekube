package de.jcup.ekube.core.model;

public class DeploymentContainer extends AbstractEKubeContainer implements EKubeStatusElement {

	private String status;

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getStatus() {
		return status;
	}

	public void add(DeploymentConditionElement element) {
		addChild(element);
	}
}