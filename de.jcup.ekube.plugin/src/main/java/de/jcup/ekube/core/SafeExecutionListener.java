package de.jcup.ekube.core;

import de.jcup.ekube.core.model.EKubeElement;

public interface SafeExecutionListener {

    <E extends EKubeElement, C, D, R> void afterExecute(EKubeContext context, SafeExecutable<E, C, R> executable, E element, C client,
            ExecutionParameters parameters);
}
