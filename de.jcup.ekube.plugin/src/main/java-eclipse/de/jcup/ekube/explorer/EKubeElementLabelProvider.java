package de.jcup.ekube.explorer;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.IToolTipProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import de.jcup.eclipse.commons.ui.EclipseUtil;
import de.jcup.ekube.Activator;
import de.jcup.ekube.core.model.ConfigMapElement;
import de.jcup.ekube.core.model.ConfigMapsContainer;
import de.jcup.ekube.core.model.CurrentContextContainer;
import de.jcup.ekube.core.model.DeploymentConditionElement;
import de.jcup.ekube.core.model.DeploymentContainer;
import de.jcup.ekube.core.model.DeploymentsContainer;
import de.jcup.ekube.core.model.EKubeContainer;
import de.jcup.ekube.core.model.EKubeElement;
import de.jcup.ekube.core.model.EKubeStatusElement;
import de.jcup.ekube.core.model.NamespaceContainer;
import de.jcup.ekube.core.model.NetworkPolicyElement;
import de.jcup.ekube.core.model.NetworksContainer;
import de.jcup.ekube.core.model.NodeConditionElement;
import de.jcup.ekube.core.model.NodeContainer;
import de.jcup.ekube.core.model.NodesContainer;
import de.jcup.ekube.core.model.PersistentVolumeClaimElement;
import de.jcup.ekube.core.model.PodContainer;
import de.jcup.ekube.core.model.PodsContainer;
import de.jcup.ekube.core.model.ServiceContainer;
import de.jcup.ekube.core.model.ServicesContainer;
import de.jcup.ekube.core.model.VolumesContainer;

class EKubeElementLabelProvider extends CellLabelProvider implements IStyledLabelProvider,IToolTipProvider {

	public EKubeElementLabelProvider() {
	}

	private Styler contextStyler = new Styler() {

		@Override
		public void applyStyles(TextStyle textStyle) {
			textStyle.foreground = Activator.getDefault().getColorManager().getColor(new RGB(250, 200, 0));
		}
	};

	@Override
	public StyledString getStyledText(Object e) {
		if (!(e instanceof EKubeElement)) {
			return null;
		}
		EKubeElement element = (EKubeElement) e;
		StyledString styledString = new StyledString();
		if (element instanceof CurrentContextContainer) {
			styledString.append("context:");
		}
		styledString.append(element.getLabel());
		if (element instanceof CurrentContextContainer) {
			CurrentContextContainer container = (CurrentContextContainer) element;
			styledString.append("  [cluster:" + container.getCluster() + ", user=" + container.getUser() + "]",
					contextStyler);
			return styledString;
		}
		if (element instanceof EKubeStatusElement) {
			EKubeStatusElement cluster = (EKubeStatusElement) element;
			styledString.append("  [" + cluster.getStatus() + "]", StyledString.COUNTER_STYLER);// contextStyler);
		}
		return styledString;
	}

	public Image getImage(Object obj) {
		String element = null;
		if (obj instanceof CurrentContextContainer) {
			element = "context.png";
		} else if (obj instanceof NamespaceContainer) {
			element = "namespace.png";
		} else if (obj instanceof ServicesContainer) {
			element = "services.png";
		} else if (obj instanceof ServiceContainer) {
			element = "service.png";
		} else if (obj instanceof PodsContainer) {
			element = "pods.png";
		} else if (obj instanceof PodContainer) {
			element = "pod.png";
		} else if (obj instanceof VolumesContainer) {
			element = "volumes.png";
		} else if (obj instanceof PersistentVolumeClaimElement) {
			element = "persistentvolumeclaim.png";
		} else if (obj instanceof ConfigMapsContainer) {
			element = "configmaps.png";
		} else if (obj instanceof ConfigMapElement) {
			element = "configmap.png";
		} else if (obj instanceof NetworksContainer) {
			element = "network.png";
		} else if (obj instanceof NetworkPolicyElement) {
			element = "network-policy.png";
		} else if (obj instanceof NodesContainer) {
			element = "nodes.png";
		} else if (obj instanceof NodeContainer) {
			element = "node.png";
		} else if (obj instanceof NodeConditionElement) {
			element = "node-condition.png";
		} else if (obj instanceof DeploymentConditionElement){
			element = "node-condition.png";
		} else if (obj instanceof DeploymentContainer){
			element = "deployment.gif";
		} else if (obj instanceof DeploymentsContainer){
			element = "deployments.gif";
		}
		if (element != null) {
			Image image = EclipseUtil.getImage("/icons/model/" + element, Activator.getDefault());
			return image;
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
	public void update(ViewerCell cell) {
		cell.setBackground(Activator.getDefault().getColorManager().getColor(new RGB(255,0,0)));
	}
	
	@Override
	public String getToolTipText(Object element) {
		if (! (element instanceof EKubeElement)){
			return null;
		}
		EKubeElement ee = (EKubeElement) element;
		return ee.getErrorMessage();
	}
}