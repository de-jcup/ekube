package de.jcup.ekube.core;

import java.io.File;
import java.util.List;

public interface EKubeConfiguration {

	File getKubeConfigFile();

	void updateEntries(List<EKubeContextConfigurationEntry> entries);

	/**
	 * Set current kubernetes context
	 * @param currentContext
	 */
	void setKubernetesContext(String currentContext);

	List<EKubeContextConfigurationEntry> getConfigurationContextList();

	/**
	 * @return current kubernetes context
	 */
	String getKubernetesContext();

	List<String> getFilteredNamespaces();

	/**
	 * Finds context configuration entry or <code>null</code>
	 * @param newCurrentContextName
	 * @return config or <code>null</code> when not found
	 */
	EKubeContextConfigurationEntry findContextConfigurationEntry(String newCurrentContextName);

}