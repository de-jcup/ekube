package de.jcup.ekube.core.model;

public class EKubeActionIdentifer<R> {

	public static final EKubeActionIdentifer<Void> REFRESH = new EKubeActionIdentifer<>(Void.class);
	
	public static final EKubeActionIdentifer<String> GRAB_STRING_INFO = new EKubeActionIdentifer<>(String.class);
	
	private EKubeActionIdentifer(Class<R> resultClass){
		
	}
	
	
}
