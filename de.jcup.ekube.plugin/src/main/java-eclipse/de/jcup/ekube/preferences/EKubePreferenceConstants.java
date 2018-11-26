package de.jcup.ekube.preferences;

/**
 * Constant definitions for plug-in preferences
 */
public enum EKubePreferenceConstants implements PreferenceIdentifiable{

	KUBE_CONFIGFILE_PATH("kubeConfigFilePath"),

	FILTER_NAMESPACES_ENABLED("filterNamespacesEnabled"),
	
	FILTERED_NAMESPACES("filteredNameSpaces"),
	
	;

	private String id;

	private EKubePreferenceConstants(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
}
