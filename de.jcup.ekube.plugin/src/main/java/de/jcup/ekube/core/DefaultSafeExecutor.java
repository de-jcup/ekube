package de.jcup.ekube.core;

import java.util.ArrayList;
import java.util.List;

import de.jcup.ekube.core.model.AbstractEKubeElement;
import de.jcup.ekube.core.model.EKubeElement;

public class DefaultSafeExecutor implements SafeExecutor {

    private List<SafeExecutionListener> listeners = new ArrayList<>();

    @Override
    public <E extends EKubeElement, C, R> R execute(EKubeContext context, SafeExecutable<E, C, R> executable, E element, C client,
            ExecutionParameters parameters) {
        try {
            R result = executable.execute(context, client, element, parameters);
            afterExecution(context, executable, client, element, parameters);
            return result;
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
                context.getErrorHandler().logError("Kube element not wellknown, cannot set error and lock to :" + element, e);
            }
            return null;
        }
    }

    private <E extends EKubeElement, C, D, R> void afterExecution(EKubeContext context, SafeExecutable<E, C, R> executable, C client, E element,
            ExecutionParameters params) {
        for (SafeExecutionListener listener : listeners) {
            listener.afterExecute(context, executable, element, client, params);
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
