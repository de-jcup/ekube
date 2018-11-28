package de.jcup.ekube;

import java.io.File;
import java.util.List;

import de.jcup.ekube.core.AbstractEKubeConfiguration;
import de.jcup.ekube.preferences.EKubePreferences;

public class EclipseEKubeConfiguration extends AbstractEKubeConfiguration{

	@Override
	public File getKubeConfigFile() {
		return EKubePreferences.getInstance().getKubeConfigFile();
	}

	@Override
	public List<String> getFilteredNamespaces() {
		return EKubePreferences.getInstance().getFilteredNamespacesAsList();
	}
	
	@Override
	public boolean isNamespaceFilteringEnabled() {
		return EKubePreferences.getInstance().getFilterNamespacesEnabled();
	}

}
