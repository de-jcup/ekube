package de.jcup.ekube.core;

public class EKubeException extends Exception{

	private static final long serialVersionUID = 1L;

	public EKubeException(String message) {
		super(message);
	}

	public EKubeException(String message, Throwable cause) {
		super(message,cause);
	}

}
