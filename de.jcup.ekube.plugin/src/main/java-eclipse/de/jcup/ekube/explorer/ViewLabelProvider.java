package de.jcup.ekube.explorer;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;

class ViewLabelProvider extends LabelProvider {

	/**
	 * 
	 */
	private final KubernetesExplorer workbenchHolder;

	/**
	 * @param kubernetesExplorer
	 */
	ViewLabelProvider(KubernetesExplorer kubernetesExplorer) {
		workbenchHolder = kubernetesExplorer;
	}

	public String getText(Object obj) {
		return obj.toString();
	}

	public Image getImage(Object obj) {
		String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
		if (obj instanceof TreeParent)
			imageKey = ISharedImages.IMG_OBJ_FOLDER;
		return workbenchHolder.workbench.getSharedImages().getImage(imageKey);
	}
}