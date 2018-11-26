package de.jcup.ekube.core.model;

public class NetworksContainer extends AbstractEKubeContainer{

	public NetworksContainer(){
		label="Network";
	}
	
	public void add(NetworkPolicyElement networkPolicyElement){
		addChild(networkPolicyElement);
	}
}
