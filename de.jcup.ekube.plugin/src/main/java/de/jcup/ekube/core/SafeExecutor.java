package de.jcup.ekube.core;

import de.jcup.ekube.core.model.EKubeElement;

public interface SafeExecutor {
	
	default <E extends EKubeElement, C, D > void execute(EKubeContext context, SafeExecutable<E,C,D> executable, E element,C client){
		execute(context, executable, element, client, null);
	}

	<E extends EKubeElement, C,D > void execute(EKubeContext context, SafeExecutable<E, C,D> executable, E element,	C client, D data);

	
	public void add(SafeExecutionListener listener);
	
	public void remove(SafeExecutionListener listener);
}