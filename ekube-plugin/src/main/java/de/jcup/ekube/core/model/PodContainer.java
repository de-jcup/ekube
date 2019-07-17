package de.jcup.ekube.core.model;

import io.fabric8.kubernetes.api.model.PodCondition;

public class PodContainer extends AbstractEKubeContainer implements EKubeStatusElement {

    public PodContainer(String uid, Object technicalObject) {
        super(uid, technicalObject);
    }

    private String status;

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
    
    public void clearConditions(){
        removeAllChildren(PodConditionElement.class);
    }

    public void add(PodConditionElement element) {
        addChild(element);
    }
}
