package de.jcup.ekube.explorer;

import java.util.ArrayList;

import de.jcup.ekube.core.access.EKubeObject;

class TreeParent extends TreeObject {
	private ArrayList<TreeObject> children;

	public TreeParent(EKubeObject name) {
		super(name);
		children = new ArrayList<>();
	}

	public void addChild(TreeObject child) {
		children.add(child);
		child.setParent(this);
	}

	public void removeChild(TreeObject child) {
		children.remove(child);
		child.setParent(null);
	}

	public TreeObject[] getChildren() {
		return (TreeObject[]) children.toArray(new TreeObject[children.size()]);
	}

	public boolean hasChildren() {
		return children.size() > 0;
	}
}