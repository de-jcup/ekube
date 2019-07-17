package de.jcup.ekube.core.fabric8io.condition;

public interface ConditionInfo {

    public boolean isOkay();
    
    public boolean isUnknown();
    
    public String getText();
    
    public String getType();
}
