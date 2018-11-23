package de.jcup.ekube.explorer;

import org.eclipse.jface.viewers.ITreeContentProvider;

import de.jcup.ekube.core.access.Cluster;
import de.jcup.ekube.core.access.EKubeObject;
import de.jcup.ekube.core.access.Kubernetes;
import de.jcup.ekube.core.access.KubernetesRegistry;
import de.jcup.ekube.core.access.Namespace;
import de.jcup.ekube.core.access.Pod;

class KubernetesExplorerContentProvider implements ITreeContentProvider {
	private final KubernetesExplorer kubernetesExplorer;

	KubernetesExplorerContentProvider(KubernetesExplorer kubernetesExplorer) {
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
	public void initialize() {
		Kubernetes k = KubernetesRegistry.get();
		TreeParent root = new TreeParent(k);
		for (Cluster c: k.getClusters()){
			appendClusterContent(c, root);
			
		}
		invisibleRoot = new TreeParent(null);
		invisibleRoot.addChild(root);
	}

	private void appendClusterContent(Cluster c, TreeParent root) {
		TreeParent clusterParent = addNode(c,root);
		
		for (Namespace n: c.getNamespaces()){
			appendNamespaceContent(n,clusterParent);
		}
	}

	private TreeParent addNode(EKubeObject object, TreeParent parent){
		TreeParent newParent = new TreeParent(object);
		parent.addChild(newParent);
		return newParent;
	}
	
	private TreeObject addLeaf(EKubeObject object, TreeParent parent){
		TreeObject newObject = new TreeObject(object);
		parent.addChild(newObject);
		return newObject;
	}

	
	private void appendNamespaceContent(Namespace n, TreeParent clusterParent) {
		TreeParent namespaceParent = addNode(n, clusterParent);
		for (Pod p: n.getPods()){
			appendPodContent(p,namespaceParent);
		}
		
	}

	private void appendPodContent(Pod p, TreeParent namespaceParent) {
		addLeaf(p, namespaceParent);
	}
}