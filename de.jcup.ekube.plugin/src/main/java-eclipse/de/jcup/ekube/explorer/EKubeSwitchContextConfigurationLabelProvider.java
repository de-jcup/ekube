package de.jcup.ekube.explorer;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import de.jcup.eclipse.commons.ui.EclipseUtil;
import de.jcup.ekube.Activator;
import de.jcup.ekube.core.KubernetesContextInfo;

public class EKubeSwitchContextConfigurationLabelProvider extends LabelProvider {

    public String getText(Object element) {
        if (element instanceof KubernetesContextInfo) {
            StringBuilder sb = new StringBuilder();
            KubernetesContextInfo container = (KubernetesContextInfo) element;
            sb.append(container.getName());
            sb.append("   [");
            sb.append("cluster: ");
            sb.append(container.getCluster());
            sb.append(",");
            sb.append("user: ");
            sb.append(container.getUser());
            if (container.getNamespace()!=null){
                sb.append(",");
                sb.append("ns: ");
                sb.append(container.getNamespace());
            }
            sb.append("]");
            return sb.toString();
        }
        return null;
    
    }
    @Override
    public Image getImage(Object element) {
        if (element instanceof KubernetesContextInfo){
            return EclipseUtil.getImage("/icons/model/context.png", Activator.getDefault());
        }
        return null;
    }
   
}
