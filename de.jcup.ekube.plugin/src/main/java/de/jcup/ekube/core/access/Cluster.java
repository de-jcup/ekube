package de.jcup.ekube.core.access;

import java.util.List;

public interface Cluster extends EKubeObject{

	public String getServer();
	
	public List<Namespace> getNamespaces();
	
	public void reload();
	
	public boolean isInCurrentContext();
	
	public void setAsCurrentContext();
}
