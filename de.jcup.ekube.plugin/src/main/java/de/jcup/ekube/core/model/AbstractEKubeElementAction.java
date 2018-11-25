package de.jcup.ekube.core.model;

public abstract class AbstractEKubeElementAction<E extends EKubeElement, T> implements EKubeElementAction {

	protected E kubeElement;
	protected T technicalObject;
	private EKubeActionIdentifer actionIdentifier;

	public AbstractEKubeElementAction(EKubeActionIdentifer actionIdentifier, E ekubeElement, T technicalObject) {
		this.actionIdentifier = actionIdentifier;
		this.kubeElement = ekubeElement;
		this.technicalObject = technicalObject;
	}

	public final EKubeActionIdentifer getIdentifier() {
		return actionIdentifier;
	}

}
