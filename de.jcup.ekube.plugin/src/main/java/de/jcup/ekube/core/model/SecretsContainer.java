package de.jcup.ekube.core.model;

public class SecretsContainer extends AbstractEKubeContainer{

	public SecretsContainer(){
		label="Secrets";
	}
	
	public void add(SecretElement secret){
		addChild(secret);
	}
}
