package de.jcup.ekube.core.model;

import java.util.EnumMap;
import java.util.Map;

public abstract class AbstractEKubeElement implements EKubeElement {

	protected String label;
	protected String name;
	
	private Map<EKubeActionIdentifer, EKubeElementAction> actions = new EnumMap<>(EKubeActionIdentifer.class);
	EKubeContainer parent;
	private boolean locked;
	private String errorMessage;

	public AbstractEKubeElement() {
	}

	@Override
	public EKubeContainer getParent() {
		return parent;
	}
	
	@Override
	public boolean isLocked() {
		return locked;
	}
	
	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	/**
	 * This sets directly the label. Normally you do NOT have to use this method when your label shall be same as name.
	 * @param text
	 */
	public void setLabel(String text) {
		this.label = text;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	@Override
	public String getLabel() {
		if (label!=null){
			return label;
		}
		if (name!=null){
			return name;
		}
		return getClass().getSimpleName() + hashCode();
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

	public void setErrorMessage(String errorMessage) {
		this.errorMessage=errorMessage;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}

}
