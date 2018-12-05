package de.jcup.ekube.core.model;

public class NodeContainer extends AbstractEKubeContainer implements EKubeStatusElement {

    public NodeContainer(String uid, Object technicalObject) {
        super(uid, technicalObject);
    }

    private String status;

    public void add(NodeConditionElement element) {
        addChild(element);
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
