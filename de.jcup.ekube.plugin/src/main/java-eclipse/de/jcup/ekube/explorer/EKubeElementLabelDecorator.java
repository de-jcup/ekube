package de.jcup.ekube.explorer;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;

import de.jcup.eclipse.commons.ui.EclipseUtil;
import de.jcup.ekube.Activator;
import de.jcup.ekube.core.model.EKubeElement;

public class EKubeElementLabelDecorator extends LabelProvider implements ILightweightLabelDecorator {

    private static ImageDescriptor DESC_LOCK = EclipseUtil.createImageDescriptor("/icons/ovr/lock_ovr.png", Activator.PLUGIN_ID);
    private static ImageDescriptor DESC_ERROR = EclipseUtil.createImageDescriptor("/icons/ovr/error_ovr.png", Activator.PLUGIN_ID);

    @Override
    public void decorate(Object element, IDecoration decoration) {
        if (!(element instanceof EKubeElement)) {
            return;
        }

        EKubeElement ekubeElement = (EKubeElement) element;
        if (ekubeElement.isLocked()) {
            decoration.addOverlay(DESC_LOCK, IDecoration.BOTTOM_RIGHT);
        }
        if (ekubeElement.hasError()){
            decoration.addOverlay(DESC_ERROR, IDecoration.BOTTOM_LEFT);
        }
    }

}
