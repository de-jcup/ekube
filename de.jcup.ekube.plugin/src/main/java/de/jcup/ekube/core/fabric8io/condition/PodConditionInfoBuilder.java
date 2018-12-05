package de.jcup.ekube.core.fabric8io.condition;

import io.fabric8.kubernetes.api.model.PodCondition;

public class PodConditionInfoBuilder {

    public ConditionInfo buildInfo(PodCondition condition) {
        AbstractConditionInfo info = null;
        switch (ConditionInfoUtil.asNullSafeLowerCased(condition.getType())) {
        case "initialized":
        case "ready":
        case "podscheduled":
            info = new MustBeTrueConditionInfo();
            break;
        default:
            info = new UnknownConditionInfo();
        }
        info.setType(condition.getType());
        info.setStatus(condition.getStatus());
        info.setLastUpdateTimeStamp(condition.getLastProbeTime());
        info.setMessage(condition.getMessage());
        return info;
    }
}
