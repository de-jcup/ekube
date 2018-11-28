package de.jcup.ekube.core.model;

import de.jcup.ekube.core.EKubeContext;

public abstract class AbstractEKubeElementAction<E extends EKubeElement, T> implements EKubeElementAction {

	protected E kubeElement;
	protected T technicalObject;
	private EKubeActionIdentifer actionIdentifier;
	protected EKubeContext context;

	public AbstractEKubeElementAction(EKubeContext context, EKubeActionIdentifer actionIdentifier, E ekubeElement, T technicalObject) {
		this.context=context;
		this.actionIdentifier = actionIdentifier;
		this.kubeElement = ekubeElement;
		this.technicalObject = technicalObject;
	}

	protected EKubeContext getContext() {
		return context;
	}
	
	public final EKubeActionIdentifer getIdentifier() {
		return actionIdentifier;
	}

}
