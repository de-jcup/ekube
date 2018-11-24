package de.jcup.ekube.core.model;

public abstract class AbstractEKubeElement implements EKubeElement {

	protected String label;

	public void setLabel(String text) {
		this.label = text;
	}

	@Override
	public String getLabel() {
		return label != null ? label : getClass().getSimpleName() + hashCode();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName()+":"+getLabel();
	}
}
