package de.jcup.ekube.core.model;

public class ServiceContainer extends AbstractEKubeContainer implements EKubeStatusElement {

    public ServiceContainer(String uid, Object technicalObject) {
        super(uid, technicalObject);
    }

    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
