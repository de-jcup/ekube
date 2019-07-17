package de.jcup.ekube.core.fabric8io.plantuml;

public class CodeBuilder {

    private StringBuilder sb = new StringBuilder();
    
    public void addLine(String line){
       addLine(0,line);
    }
    
    public void addLine(int indent, String line){
        for (int i=0;i<indent;i++){
            sb.append("   ");
        }
        sb.append(line).append("\n");
    }
    public void addEmptyLine(){
        sb.append("\n");
    }

    public String getCode() {
        return sb.toString();
    }
}
