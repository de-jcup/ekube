package de.jcup.ekube.core.model;

public class PersistentVolumeElement extends AbstractEKubeElement implements EKubeStatusElement {

    public PersistentVolumeElement(String uid, Object technicalObject) {
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
