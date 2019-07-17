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
