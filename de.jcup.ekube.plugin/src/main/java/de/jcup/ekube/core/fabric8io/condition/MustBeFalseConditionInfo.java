package de.jcup.ekube.core.fabric8io.condition;

public class MustBeFalseConditionInfo extends AbstractConditionInfo {

    @Override
    public boolean isOkay() {
        return !getState();
    }

    @Override
    public boolean isUnknown() {
        return false;
    }

}
