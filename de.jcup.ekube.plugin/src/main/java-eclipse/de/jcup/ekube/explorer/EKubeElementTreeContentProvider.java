package de.jcup.ekube.explorer;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.jcup.ekube.core.model.EKubeContainer;
import de.jcup.ekube.core.model.EKubeElement;
import de.jcup.ekube.core.model.EKubeModel;

class EKubeElementTreeContentProvider implements ITreeContentProvider {
	private final KubernetesExplorer kubernetesExplorer;
	private EKubeModel model;

	EKubeElementTreeContentProvider(KubernetesExplorer kubernetesExplorer) {
		this.kubernetesExplorer = kubernetesExplorer;
	}

	
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput){
		if (newInput instanceof EKubeModel){
			this.model=(EKubeModel) newInput;
			return;
		}
	}
	

	public Object[] getElements(Object parent) {
		if (parent.equals(this.kubernetesExplorer.getViewSite())) {
			if (model == null){
				model=new EKubeModel();
			}
			return new Object[]{model.getCurrentContext(),model.getNodesContainer()};
		}
		return getChildren(parent);
	}

	public Object getParent(Object child) {
		if (child instanceof EKubeElement) {
			EKubeElement element = (EKubeElement) child;
			return element.getParent();
		}
		return null;
	}

	public Object[] getChildren(Object parent) {
		if (parent instanceof EKubeContainer) {
			EKubeContainer container = (EKubeContainer) parent;
			if (container.isLocked()){
				return new Object[0];
			}
			return container.getChildren().toArray();
		}
		return new Object[0];
	}

	public boolean hasChildren(Object parent) {
		if (parent instanceof EKubeContainer) {
			EKubeContainer container = (EKubeContainer) parent;
			if (container.isLocked()){
				return false;
			}
			return container.hasChildren();
		}
		return false;
	}

}