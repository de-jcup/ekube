package de.jcup.ekube.explorer;

import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import de.jcup.eclipse.commons.ui.EclipseUtil;
import de.jcup.ekube.Activator;
import de.jcup.ekube.core.model.CurrentContextContainer;
import de.jcup.ekube.core.model.EKubeContainer;
import de.jcup.ekube.core.model.EKubeElement;
import de.jcup.ekube.core.model.EKubeStatusElement;
import de.jcup.ekube.core.model.NamespaceContainer;
import de.jcup.ekube.core.model.PodContainer;
import de.jcup.ekube.core.model.PodsContainer;
import de.jcup.ekube.core.model.ServiceContainer;
import de.jcup.ekube.core.model.ServicesContainer;

class KubernetesExplorerViewLabelProvider extends LabelProvider implements IStyledLabelProvider, IColorProvider {

	public Image getImage(Object obj) {
		String element=null;
		if (obj instanceof CurrentContextContainer){
			element="ctxhelp_context_obj.png";
		}else if (obj instanceof NamespaceContainer){
			element="memory_view.png";
		}else if (obj instanceof ServicesContainer){
			element="osprc_obj.png";
		}else if (obj instanceof ServiceContainer){
			element="thread_obj.png";
		}else if (obj instanceof PodsContainer){
			element="debugt_obj.png";
		}else if (obj instanceof PodContainer){
			element="methpub_obj.png";
		}
		if (element!=null){
			return EclipseUtil.getImage("/icons/model/"+element, Activator.getDefault());
		}
		return getFallbackImage(obj);
	}

	protected Image getFallbackImage(Object obj) {
		String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
		if (obj instanceof EKubeContainer) {
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
	private Styler contextStyler = new Styler() {

		@Override
		public void applyStyles(TextStyle textStyle) {
			textStyle.foreground=Activator.getDefault().getColorManager().getColor(new RGB(250,200,0));
		}
	};
	
	@Override
	public StyledString getStyledText(Object e) {
		if (! (e instanceof EKubeElement)){
			return null;
		}
		EKubeElement element = (EKubeElement) e;
		StyledString styledString = new StyledString(element.getLabel());
		if (element instanceof EKubeStatusElement){
			EKubeStatusElement cluster = (EKubeStatusElement) element;
			styledString.append("  ["+cluster.getStatus()+"]", StyledString.COUNTER_STYLER);//contextStyler);
		}
		if (element instanceof CurrentContextContainer){
			CurrentContextContainer container = (CurrentContextContainer) element;
			styledString.append("  [cluster:"+container.getCluster()+", user="+container.getUser() + "]", contextStyler);
		}
		return styledString;
	}
}