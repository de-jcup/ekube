package de.jcup.ekube.core;

import de.jcup.ekube.core.model.EKubeElement;

public interface SafeExecutor {

    default <E extends EKubeElement, C, R> R execute(EKubeContext context, SafeExecutable<E, C, R> executable, E element, C client) {
        return execute(context, executable, element, client, new ExecutionParameters());
    }

    <E extends EKubeElement, C, R> R execute(EKubeContext context, SafeExecutable<E, C, R> executable, E element, C client,
            ExecutionParameters parameters);

    public void add(SafeExecutionListener listener);

    public void remove(SafeExecutionListener listener);
}