/*
 * Copyright 2019 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
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
