package de.jcup.ekube.explorer;

import org.eclipse.core.runtime.IAdaptable;

import de.jcup.ekube.core.access.EKubeObject;

class TreeObject implements IAdaptable {
	private EKubeObject kubeObject;
	private TreeParent parent;

	public TreeObject(EKubeObject object) {
		this.kubeObject = object;
	}

	public String getName() {
		return kubeObject.getName();
	}
	
	public EKubeObject getKubeObject() {
		return kubeObject;
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