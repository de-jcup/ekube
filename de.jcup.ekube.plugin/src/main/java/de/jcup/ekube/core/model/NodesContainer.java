package de.jcup.ekube.core.model;

public class NodesContainer extends AbstractEKubeContainer{

	public NodesContainer(){
		label="Nodes";
	}
	
	public void add(NodeContainer node){
		addChild(node);
	}
}
