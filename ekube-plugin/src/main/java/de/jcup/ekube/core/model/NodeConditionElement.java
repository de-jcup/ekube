package de.jcup.ekube.core.model;

import de.jcup.ekube.core.fabric8io.condition.ConditionInfo;

public class NodeConditionElement extends AbstractEKubeElement implements EKubeStatusElement {

    public NodeConditionElement(String uid, Object technicalObject) {
        super(uid, technicalObject);
    }

    private ConditionInfo info;

    public void setInfo(ConditionInfo info) {
        this.info = info;
    }

    public ConditionInfo getInfo() {
        return info;
    }

    public String getStatus() {
        if (info == null) {
            return "<none>";
        }
        return info.getText();
    }
}