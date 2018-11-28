package de.jcup.ekube.core;

import java.util.ArrayList;
import java.util.List;

import de.jcup.ekube.core.model.AbstractEKubeElement;
import de.jcup.ekube.core.model.EKubeElement;

public class DefaultSafeExecutor implements SafeExecutor {

	private List<SafeExecutionListener> listeners = new ArrayList<>();

	@Override
	public <E extends EKubeElement, C, D> void execute(EKubeContext context, SafeExecutable<E, C, D> executable,
			E element, C client, D data) {
		try {
			executable.execute(context, client, element, data);
			afterExecution(context, executable, client, element, data);
		} catch (Exception e) {
			/* NPE will be logged */
			if (e instanceof NullPointerException) {
				context.getErrorHandler().logError("NPE occurred", e);
			}
			if (element instanceof AbstractEKubeElement) {
				AbstractEKubeElement ae = (AbstractEKubeElement) element;
				ae.setErrorMessage(e.getMessage());
				ae.setLocked(true);
			} else {
				context.getErrorHandler()
						.logError("Kube element not wellknown, cannot set error and lock to :" + element, e);
			}
		}
	}

	private <E extends EKubeElement, C, D> void afterExecution(EKubeContext context, SafeExecutable<E, C, D> executable,
			C client, E element, D data) {
		for (SafeExecutionListener listener : listeners) {
			listener.afterExecute(context, executable, element, client, data);
		}
	}

	@Override
	public void add(SafeExecutionListener listener) {
		listeners.add(listener);
	}

	@Override
	public void remove(SafeExecutionListener listener) {
		listeners.remove(listener);
	}
}
