package de.jcup.ekube.core.model;

public class PodsContainer extends AbstractEKubeContainer implements SyntheticKubeElement{

	public PodsContainer(){
		super(null);// no uid available - because synthetic element which is not existing in kubernetes
		label="Pods";
	}
	
	public void add(PodContainer pod){
		addChild(pod);
	}
}
