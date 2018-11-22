package de.jcup.ekube.explorer;

import org.eclipse.jface.viewers.ITreeContentProvider;

class ViewContentProvider implements ITreeContentProvider {
	/**
	 * 
	 */
	private final KubernetesExplorer kubernetesExplorer;

	/**
	 * @param kubernetesExplorer
	 */
	ViewContentProvider(KubernetesExplorer kubernetesExplorer) {
		this.kubernetesExplorer = kubernetesExplorer;
	}

	private TreeParent invisibleRoot;

	public Object[] getElements(Object parent) {
		if (parent.equals(this.kubernetesExplorer.getViewSite())) {
			if (invisibleRoot == null)
				initialize();
			return getChildren(invisibleRoot);
		}
		return getChildren(parent);
	}

	public Object getParent(Object child) {
		if (child instanceof TreeObject) {
			return ((TreeObject) child).getParent();
		}
		return null;
	}

	public Object[] getChildren(Object parent) {
		if (parent instanceof TreeParent) {
			return ((TreeParent) parent).getChildren();
		}
		return new Object[0];
	}

	public boolean hasChildren(Object parent) {
		if (parent instanceof TreeParent)
			return ((TreeParent) parent).hasChildren();
		return false;
	}

	/*
	 * We will set up a dummy model to initialize tree heararchy. In a real code,
	 * you will connect to a real model and expose its hierarchy.
	 */
	private void initialize() {
		TreeParent root = new TreeParent("Tenants");
		TreeParent p1 = new TreeParent("Parent 1");
		TreeParent p2 = new TreeParent("Parent 2");

		TreeObject to1 = new TreeObject("Leaf 1");
		TreeObject to2 = new TreeObject("Leaf 2");
		TreeObject to3 = new TreeObject("Leaf 3");
		p1.addChild(to1);
		p1.addChild(to2);
		p1.addChild(to3);

		TreeObject to4 = new TreeObject("Leaf 4");
		p2.addChild(to4);

		root.addChild(p1);
		root.addChild(p2);

		invisibleRoot = new TreeParent("");
		invisibleRoot.addChild(root);
	}
}