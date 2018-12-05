package de.jcup.ekube.core.fabric8io.condition;

import io.fabric8.kubernetes.api.model.NodeCondition;

public class NodeConditionInfoBuilder {

    public ConditionInfo buildInfo(NodeCondition condition) {
        AbstractConditionInfo info = null;
        switch (ConditionInfoUtil.asNullSafeLowerCased(condition.getType())) {
        case "ready":
            info = new MustBeTrueConditionInfo();
            break;
        case "outofdisk":
        case "memorypressure":
        case "diskpressure":
            info = new MustBeFalseConditionInfo();
            break;
        default:
            info = new UnknownConditionInfo();
        }
        info.setType(condition.getType());
        info.setStatus(condition.getStatus());
        info.setLastUpdateTimeStamp(condition.getLastHeartbeatTime());
        info.setMessage(condition.getMessage());
        return info;
    }
}
