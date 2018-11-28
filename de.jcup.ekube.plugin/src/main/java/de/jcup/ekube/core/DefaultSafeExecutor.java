package de.jcup.ekube.core;

import de.jcup.ekube.core.model.AbstractEKubeElement;
import de.jcup.ekube.core.model.EKubeElement;

public class DefaultSafeExecutor implements SafeExecutor {

	@Override
	public <E extends EKubeElement,C,D> void execute(EKubeContext context, SafeExecutable<E,C,D> executable, E element, C client, D data){
		try{
			executable.execute(context, client, element, data);
		}catch(Exception e){
			/* NPE will be logged */
			if (e instanceof NullPointerException){
				context.getErrorHandler().logError("NPE occurred", e);
			}
			if (element instanceof AbstractEKubeElement){
				AbstractEKubeElement ae = (AbstractEKubeElement) element;
				ae.setErrorMessage(e.getMessage());
				ae.setLocked(true);
			}else{
				context.getErrorHandler().logError("Kube element not wellknown, cannot set error and lock to :"+element, e);
			}
		}
	}
}
