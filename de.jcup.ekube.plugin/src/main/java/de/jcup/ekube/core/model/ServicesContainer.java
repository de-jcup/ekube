package de.jcup.ekube.core.model;

public class ServicesContainer extends AbstractEKubeContainer{

	public ServicesContainer(){
		label="Services";
	}
	
	public void add(ServiceContainer serviceContainer){
		addChild(serviceContainer);
	}
}
