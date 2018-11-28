package de.jcup.ekube.core.model;

/**
 * Model containing relevant data usable for ui and information gathering. Will
 * be created by dedicated - maybe specific (k8jc/fabric8client) plementations
 * of builders/factories.
 * 
 * @author Albert Tregnaghi
 *
 */
public class EKubeModel {

	private CurrentContextContainer currentContextContainer;
	
	public EKubeModel() {
		this.currentContextContainer = new CurrentContextContainer();
		this.currentContextContainer.setName("<no context selected>"); // just initial name+label
	}
	
	public CurrentContextContainer getCurrentContext() {
		return currentContextContainer;
	}

}
