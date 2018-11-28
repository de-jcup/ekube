package de.jcup.ekube.core.model;

public interface EKubeElement {

	/**
	 * @return label, or when no label defined, the name. No name defined returns a fallback
	 */
	public String getLabel();
	
	public String getName();
	
	public void execute(EKubeActionIdentifer action);

	public EKubeContainer getParent();
	
	/**
	 * @return <code>true</code> when this element is not accessible by current user
	 */
	public boolean isLocked();
	
	/**
	 * @return error message or <code>null</code>
	 */
	public String getErrorMessage();
}
