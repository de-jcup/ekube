package de.jcup.ekube.core;

import de.jcup.ekube.core.model.EKubeElement;

public interface SafeExecutor {

    default <E extends EKubeElement, C, R> R execute(EKubeContext context, SafeExecutable<E, C, R> executable, E element, C client) {
        return execute(context, executable, element, client, new ExecutionParameters());
    }

    /**
     * Executes executable. 
     * @param context
     * @param executable
     * @param element
     * @param client
     * @param parameters
     * @return result or <code>null</code>
     */
    <E extends EKubeElement, C, R> R execute(EKubeContext context, SafeExecutable<E, C, R> executable, E element, C client,
            ExecutionParameters parameters);

    public void add(SafeExecutionListener listener);

    public void remove(SafeExecutionListener listener);
}