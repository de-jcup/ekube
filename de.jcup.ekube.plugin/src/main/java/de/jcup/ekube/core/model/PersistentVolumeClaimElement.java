package de.jcup.ekube.core.model;

public class PersistentVolumeClaimElement extends AbstractEKubeElement implements EKubeStatusElement {

    public PersistentVolumeClaimElement(String uid, Object technicalObject) {
        super(uid, technicalObject);
    }

    private String status;

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
