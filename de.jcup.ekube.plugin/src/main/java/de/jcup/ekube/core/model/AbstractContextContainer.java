package de.jcup.ekube.core.model;

public abstract class AbstractContextContainer extends AbstractEKubeContainer {

	private String name;
	private String user;
	private String cluster;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}


	public void setUser(String user) {
		this.user = user;
	}

	public String getUser() {
		return user;
	}

	public void setCluster(String cluster) {
		this.cluster = cluster;
	}

	public String getCluster() {
		return cluster;
	}

}