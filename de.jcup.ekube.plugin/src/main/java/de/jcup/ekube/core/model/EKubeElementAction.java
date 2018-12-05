package de.jcup.ekube.core.model;

import de.jcup.ekube.core.ExecutionParameters;

public interface EKubeElementAction<R> {

    public EKubeActionIdentifer<R> getIdentifier();

    public R execute(ExecutionParameters params);
}
