package de.jcup.ekube.core.model;

import java.util.EnumMap;
import java.util.Map;

public abstract class AbstractEKubeElement implements EKubeElement {

	protected String label;
	private Map<EKubeActionIdentifer, EKubeElementAction> actions = new EnumMap<>(EKubeActionIdentifer.class);
	EKubeContainer parent;

	public AbstractEKubeElement() {
	}

	@Override
	public EKubeContainer getParent() {
		return parent;
	}

	public void setLabel(String text) {
		this.label = text;
	}

	@Override
	public String getLabel() {
		return label != null ? label : getClass().getSimpleName() + hashCode();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ":" + getLabel();
	}

	@Override
	public void execute(EKubeActionIdentifer actionIdentifier) {
		EKubeElementAction action = actions.get(actionIdentifier);
		if (action == null) {
			return;
		}
		action.execute();
	}

	/**
	 * Registers an action for its identifier. Attention: an action with same
	 * identifier already registered will be removed! If you want to have
	 * multiple actions executed for same identifier please create a composite
	 * action cotaining the others...
	 * 
	 * @param action
	 */
	public void register(EKubeElementAction action) {
		actions.put(action.getIdentifier(), action);
	}

}
