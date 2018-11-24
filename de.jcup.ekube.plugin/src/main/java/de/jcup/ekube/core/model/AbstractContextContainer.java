package de.jcup.ekube.core.model;

public abstract class AbstractContextContainer extends AbstractEKubeContainer {

	private String name;
	private String user;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	private String cluster;

	public void setUser(String user) {
		this.user = user;
	}

	public String getUser() {
		return user;
	}

	public AbstractContextContainer() {
		super();
	}

	public void setCluster(String cluster) {
		this.cluster = cluster;
	}

	public String getCluster() {
		return cluster;
	}

}