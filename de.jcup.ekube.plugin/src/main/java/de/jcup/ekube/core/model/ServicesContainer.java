package de.jcup.ekube.core.model;

public class ServicesContainer extends AbstractEKubeContainer{

	public ServicesContainer(){
		label="Services";
	}
	
	public void add(ServiceContainer serviceContainer){
		children.add(serviceContainer);
	}
}
