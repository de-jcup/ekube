package de.jcup.ekube.core.model;

public class PodsContainer extends AbstractEKubeContainer{

	public PodsContainer(){
		label="Pods";
	}
	
	public void add(PodContainer pod){
		addChild(pod);
	}
}
