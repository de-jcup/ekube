package de.jcup.ekube.core.access;

public interface Context extends EKubeObject{

	public Cluster getCluster();
	
	public User getUser();
	
	
}
