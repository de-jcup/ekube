package de.jcup.ekube.core.model;

public class ClusterContainer extends AbstractEKubeContainer{
	
	public ClusterContainer(){
		addChild(new PodsContainer());
	}
}