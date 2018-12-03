package de.jcup.ekube.core.model;

public class EKubeActionIdentifer<R> {

	public static final EKubeActionIdentifer<Void> REFRESH_STATUS = new EKubeActionIdentifer<>("Refresh status", Void.class);
	public static final EKubeActionIdentifer<Void> REFRESH_CHILDREN= new EKubeActionIdentifer<>("Refresh children", Void.class);
	
	public static final EKubeActionIdentifer<String> GRAB_STRING_INFO = new EKubeActionIdentifer<>("Open as Yaml",String.class).markDirectlyExecutable();

	public static final EKubeActionIdentifer<Void> REFRESH = new EKubeActionIdentifer<>("Refresh", Void.class).markDirectlyExecutable().markRefreshNecessary();

	private String label;

	private boolean refreshNecessary;

	private boolean directExecutable;
	
	private EKubeActionIdentifer(String label, Class<R> resultClass){
		this.label=label;
	}

	public String getLabel() {
		return label;
	}

	private EKubeActionIdentifer<R> markRefreshNecessary() {
		this.refreshNecessary = true;
		return this;
	}
	
	public boolean isRefreshNecessary() {
		return refreshNecessary;
	}
	
	private EKubeActionIdentifer<R> markDirectlyExecutable(){
		this.directExecutable=true;
		return this;
	}
	
	public boolean isVisibleForUser(){
		return directExecutable;
	}
	
	
}
