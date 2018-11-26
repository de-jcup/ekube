package de.jcup.ekube.explorer;

import org.eclipse.jface.viewers.LabelProvider;

import de.jcup.ekube.core.EKubeContextConfigurationEntry;

public class EKubeConfigurationLabelProvider extends LabelProvider{

	public String getText(Object element) {
		if (element instanceof EKubeContextConfigurationEntry){
			EKubeContextConfigurationEntry context = (EKubeContextConfigurationEntry) element;
			return context.getName();
		}
		return null;
	}
}
