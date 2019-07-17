package de.jcup.ekube.core.model;

import de.jcup.ekube.core.EKubeContext;

public abstract class AbstractEKubeElementAction<E extends EKubeElement, R> implements EKubeElementAction<R> {

    protected E kubeElement;
    private EKubeActionIdentifer<R> actionIdentifier;
    protected EKubeContext context;

    public AbstractEKubeElementAction(EKubeContext context, EKubeActionIdentifer<R> actionIdentifier, E ekubeElement) {
        this.context = context;
        this.actionIdentifier = actionIdentifier;
        this.kubeElement = ekubeElement;
    }

    protected EKubeContext getContext() {
        return context;
    }

    public final EKubeActionIdentifer<R> getIdentifier() {
        return actionIdentifier;
    }

}
