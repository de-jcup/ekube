package de.jcup.ekube.core.model;

public class ConfigMapsContainer extends AbstractEKubeContainer{

	public ConfigMapsContainer(){
		label="Config maps";
	}
	
	public void add(ConfigMapElement configMap){
		addChild(configMap);
	}
}
