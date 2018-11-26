package de.jcup.ekube.core.model;

public interface EKubeElement {

	/**
	 * @return label, or when no label defined, the name. No name defined returns a fallback
	 */
	public String getLabel();
	
	public String getName();
	
	public void execute(EKubeActionIdentifer action);

	public EKubeContainer getParent();
}
