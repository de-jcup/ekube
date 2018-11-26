package de.jcup.ekube.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DefaultEKubeConfiguration extends AbstractEKubeConfiguration implements EKubeConfiguration {

	private List<String> filteredNamespaces = new ArrayList<>();

	private File kubeConfigFile;
	
	public DefaultEKubeConfiguration(){
		setDefaults();
	}

	private void setDefaults() {
		kubeConfigFile = new File(System.getProperty("user.home") + "/.kube/config");
		filteredNamespaces.add("kube-system");
	}

	@Override
	public File getKubeConfigFile(){
		return kubeConfigFile;
	}
	
	@Override
	public List<String> getFilteredNamespaces(){
		return filteredNamespaces;
	}
	
}
