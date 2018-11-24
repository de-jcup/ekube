package de.jcup.ekube.core.model;

import de.jcup.ekube.core.ErrorHandler;

/**
 * Model containing relevant data usable for ui and information gathering. Will
 * be created by dedicated - maybe specific (k8jc/fabric8client) plementations
 * of builders/factories.
 * 
 * @author Albert Tregnaghi
 *
 */
public class EKubeModel{

	private ErrorHandler handler;
	private CurrentContextContainer currentContextContainer;
	private String currentContextName;
	
	public EKubeModel(ErrorHandler handler) {
		this.handler = handler;
	}

	public String getCurrentContextName() {
		return currentContextName;
	}
	
	public void setCurrentContext(CurrentContextContainer currentContextContainer){
		this.currentContextContainer=currentContextContainer;
	}
	
	public CurrentContextContainer getCurrentContext() {
		if (currentContextContainer==null){
			handler.logError("current context not set. To provent NPE, returning fake", null);
			currentContextContainer = new CurrentContextContainer();
			currentContextContainer.setLabel("<none>");
			return currentContextContainer;
		}
		return this.currentContextContainer;
	}
	
	public void setCurrentContext(String context){
		this.currentContextName=context;
	}

}
