package de.jcup.ekube.explorer;

import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import de.jcup.ekube.core.access.Cluster;
import de.jcup.ekube.core.access.EKubeObject;

class KubernetesExplorerViewLabelProvider extends LabelProvider implements IStyledLabelProvider, IColorProvider {

	public String getText(Object obj) {
		return obj.toString();
	}

	public Image getImage(Object obj) {
		String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
		if (obj instanceof TreeParent) {
			imageKey = ISharedImages.IMG_OBJ_FOLDER;
		}
		IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench == null) {
			return null;
		}
		ISharedImages sharedImages = workbench.getSharedImages();
		if (sharedImages == null) {
			return null;
		}
		return sharedImages.getImage(imageKey);
	}

	@Override
	public Color getForeground(Object element) {
		return null;
	}

	@Override
	public Color getBackground(Object element) {
		return null;
	}
	private Styler clusterNotCurrentContextStyler = new Styler() {

		@Override
		public void applyStyles(TextStyle textStyle) {
			textStyle.strikeout=true;
		}
	};
	
	private Styler clusterIsCurrentContextStyler = new Styler() {

		@Override
		public void applyStyles(TextStyle textStyle) {
			textStyle.strikeout=false;
		}
	};
	@Override
	public StyledString getStyledText(Object e) {
		StyledString styled = new StyledString();
		if (e == null) {
			styled.append("null");
			return styled;
		}
		
		if (! (e instanceof TreeObject)) {
			styled.append("<unknwon:"+e.getClass());
			return styled;
		}
		TreeObject to = (TreeObject) e;
		EKubeObject element = to.getKubeObject();
		if (element ==null){
			styled.append("<null kube object>");
			return styled;
		}
		if (element instanceof Cluster){
			Cluster cluster = (Cluster) element;
			if (cluster.isInCurrentContext()){
				styled.append(cluster.getName(), clusterIsCurrentContextStyler);
			}else{
				styled.append(cluster.getName(), clusterNotCurrentContextStyler);
			}
		}else{
			styled.append(element.getName());
		}
		return styled;
	}
}