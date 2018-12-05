package de.jcup.ekube.core.fabric8io.condition;

import io.fabric8.kubernetes.api.model.apps.DeploymentCondition;

public class DeploymentConditionInfoBuilder {

    public ConditionInfo buildInfo(DeploymentCondition condition) {
        AbstractConditionInfo info = null;
        switch (ConditionInfoUtil.asNullSafeLowerCased(condition.getType())) {
        case "available":
            info = new MustBeTrueConditionInfo();
            break;
        default:
            info = new UnknownConditionInfo();
        }
        info.setType(condition.getType());
        info.setStatus(condition.getStatus());
        info.setLastUpdateTimeStamp(condition.getLastUpdateTime());
        info.setMessage(condition.getMessage());
        return info;
    }
    
 
}
