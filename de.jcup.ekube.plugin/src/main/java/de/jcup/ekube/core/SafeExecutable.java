package de.jcup.ekube.core;

import de.jcup.ekube.core.model.EKubeElement;

public interface SafeExecutable<E extends EKubeElement,C,D>{

	public default void execute(EKubeContext context, C client, E element){
		execute(context, client,element,null);
	}
	
	public void execute(EKubeContext context, C client, E element, D data);
	
}
