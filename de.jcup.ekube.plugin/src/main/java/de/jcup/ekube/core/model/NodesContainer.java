package de.jcup.ekube.core.model;

public class NodesContainer extends AbstractEKubeContainer implements SyntheticKubeElement{

	public NodesContainer(){
		super(null);// no uid available - because synthetic element which is not existing in kubernetes
		label="Nodes";
	}
	
	public void add(NodeContainer node){
		addChild(node);
	}
}
