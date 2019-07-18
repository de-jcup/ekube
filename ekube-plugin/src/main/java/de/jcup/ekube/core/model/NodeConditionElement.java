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
