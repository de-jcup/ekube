package de.jcup.ekube.core.fabric8io.condition;

public class UnknownConditionInfo extends AbstractConditionInfo {

    @Override
    public boolean isOkay() {
        return false;
    }

    @Override
    public boolean isUnknown() {
        return true;
    }

}
