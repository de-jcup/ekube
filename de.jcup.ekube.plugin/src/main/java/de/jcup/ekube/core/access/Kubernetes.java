package de.jcup.ekube.core.access;

import java.util.List;

public interface Kubernetes  extends EKubeObject{
	
	public boolean isConnected();
	/**
	 * Reloads data
	 */
	public void reload();
	
	public List<Cluster> getClusters();
}
