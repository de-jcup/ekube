package de.jcup.ekube.core.model;

public class EKubeModelToStringDumpConverter {

	public String convert(EKubeModel model){
		CurrentContextContainer context = model.getCurrentContext();
		StringBuilder sb = new StringBuilder();
		output(context,0,sb);
		
		return sb.toString();
	}
	
	private void output(EKubeElement element, int indent, StringBuilder sb){
		for (int i=0;i<indent;i++){
			sb.append(" ");
		}
		sb.append(element.toString());
		sb.append("\n");
		
		if (element instanceof EKubeContainer){
			EKubeContainer container = (EKubeContainer) element;
			for (EKubeElement child: container.getChildren()){
				output(child, indent+1,sb);
			}
		}
	}
	
}
