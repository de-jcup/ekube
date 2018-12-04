package de.jcup.ekube.core.model;

public class ConfigMapsContainer extends AbstractEKubeContainer implements SyntheticKubeElement{

	public ConfigMapsContainer(){
		super(null);// no uid available - because synthetic element which is not existing in kubernetes
		label="Config maps";
	}
	
	public void add(ConfigMapElement configMap){
		addChild(configMap);
	}

	

	
}
