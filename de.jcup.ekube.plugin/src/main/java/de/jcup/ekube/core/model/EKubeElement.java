package de.jcup.ekube.core.model;

public interface EKubeElement {

	public String getLabel();
	
	public void execute(EKubeActionIdentifer action);

	public EKubeContainer getParent();
}
