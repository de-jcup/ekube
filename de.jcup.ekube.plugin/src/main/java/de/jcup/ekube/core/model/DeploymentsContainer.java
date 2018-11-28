package de.jcup.ekube.core.model;

public class DeploymentsContainer extends AbstractEKubeContainer{

	public DeploymentsContainer(){
		label="Deployments";
	}
	
	public void add(DeploymentContainer deployment){
		addChild(deployment);
	}
}
