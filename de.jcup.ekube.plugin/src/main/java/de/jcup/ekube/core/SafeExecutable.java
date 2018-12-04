package de.jcup.ekube.core;

import de.jcup.ekube.core.model.EKubeElement;

public interface SafeExecutable<E extends EKubeElement,C,D,R>{

	public default R execute(EKubeContext context, C client, E element){
		return execute(context, client,element,null);
	}
	
	public R execute(EKubeContext context, C client, E element, D data);
	
}
