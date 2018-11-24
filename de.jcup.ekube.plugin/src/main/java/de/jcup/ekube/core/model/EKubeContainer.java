package de.jcup.ekube.core.model;

import java.util.List;

public interface EKubeContainer extends EKubeElement{

	/**
	 * @return unmodifiable list of children
	 */
	public List<EKubeElement> getChildren();
	
	public boolean hasChildren();
}
