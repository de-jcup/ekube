package de.jcup.ekube.core.model;

public class PersistentVolumeClaimElement extends AbstractEKubeElement implements EKubeStatusElement {

	private String status;

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}
}
