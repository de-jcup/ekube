package de.jcup.ekube.explorer;

import org.eclipse.core.runtime.IAdaptable;

class TreeObject implements IAdaptable {
	private String name;
	private TreeParent parent;

	public TreeObject(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setParent(TreeParent parent) {
		this.parent = parent;
	}

	public TreeParent getParent() {
		return parent;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public <T> T getAdapter(Class<T> key) {
		return null;
	}
}