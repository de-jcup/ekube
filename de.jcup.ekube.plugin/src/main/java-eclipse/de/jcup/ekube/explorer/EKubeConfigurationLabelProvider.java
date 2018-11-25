package de.jcup.ekube.explorer;

import org.eclipse.jface.viewers.LabelProvider;

import de.jcup.ekube.core.EKubeConfigurationContext;

public class EKubeConfigurationLabelProvider extends LabelProvider{

	public String getText(Object element) {
		if (element instanceof EKubeConfigurationContext){
			EKubeConfigurationContext context = (EKubeConfigurationContext) element;
			return context.getName();
		}
		return null;
	}
}
