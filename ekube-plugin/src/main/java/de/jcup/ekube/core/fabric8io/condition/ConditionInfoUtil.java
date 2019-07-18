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

import de.jcup.ekube.core.model.AbstractEKubeElement;

public class ConditionInfoUtil {

    public static String asNullSafeLowerCased(String text) {
        if (text == null) {
            return "";
        }
        return text.trim().toLowerCase();

    }

    public static ConditionInfoStatus createStatus() {
        return new ConditionInfoStatus();
    }

    public static class ConditionInfoStatus {
        private boolean atLeastOneFailed;
        private boolean atLeastOneUnknown;
        private StringBuilder sb = new StringBuilder();

        public void handle(ConditionInfo info) {
            if (info.isUnknown()) {
                atLeastOneUnknown = true;
            } else {
                if (info.isOkay()) {
                    return;
                }
                atLeastOneFailed = true;
                sb.append("Failed condition:");
                sb.append(info.getType());
                sb.append("\n");
            }
        }

        public boolean hasAtLeastOneUnknown() {
            return atLeastOneUnknown;
        }

        public boolean hasAtLeastOneFailed() {
            return atLeastOneFailed;
        }

        public String getErrorMessage() {
            return sb.toString();
        }

        public void handleErrors(AbstractEKubeElement element) {
            if (hasAtLeastOneFailed()){
                element.setErrorMessage(getErrorMessage());
            }
        }
    }

}
