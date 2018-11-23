package de.jcup.ekube.core.access;

public interface Connector {

	public Kubernetes connect(ErrorHandler errorHandler);
}
