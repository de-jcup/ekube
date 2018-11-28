package de.jcup.ekube.core.model;

public interface EKubeElementAction<R> {

	public EKubeActionIdentifer<R> getIdentifier();

	public R execute();
}
