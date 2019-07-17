package de.jcup.ekube.core.fabric8io.condition;

public abstract class AbstractConditionInfo implements ConditionInfo {

    private boolean state;
    private String message;
    private String type;
    private String status;
    private String lastUpdateTimeStamp;

    public AbstractConditionInfo() {
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStatus(String status) {
        this.status = status;
        this.state=Boolean.parseBoolean(ConditionInfoUtil.asNullSafeLowerCased(status));
    }

    public void setLastUpdateTimeStamp(String lastUpdateTimeStamp) {
        this.lastUpdateTimeStamp = lastUpdateTimeStamp;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getLastUpdateTimeStamp() {
        return lastUpdateTimeStamp;
    }

    public String getType() {
        return type;
    }

    @Override
    public String getText() {
        StringBuilder sb = new StringBuilder();
        
        if (message!=null){
            sb.append(message);
            sb.append(" ");
        }
        if (lastUpdateTimeStamp!=null){
            sb.append(lastUpdateTimeStamp);
            sb.append(" ");
        }
        if (status!=null && isUnknown()){
            sb.append("(Status:");
            sb.append(status);
            sb.append(") ");
        }
        return sb.toString();
    }

    protected boolean getState() {
        return state;
    }

    public void setType(String type) {
       this.type=type;
    }
    
}
