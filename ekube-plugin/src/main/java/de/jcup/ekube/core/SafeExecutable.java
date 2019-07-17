package de.jcup.ekube.core;

import de.jcup.ekube.core.model.EKubeElement;

public interface SafeExecutable<E extends EKubeElement, C, R> {

    public R execute(EKubeContext context, C client, E element, ExecutionParameters parameters);

}
