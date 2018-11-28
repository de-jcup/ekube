package de.jcup.ekube.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public abstract class AbstractEKubeConfiguration implements EKubeConfiguration{
	private String currentContext;
	private List<EKubeContextConfigurationEntry> configurationContextList = new ArrayList<>();

	@Override
	public void updateEntries(List<EKubeContextConfigurationEntry> configurationContextList) {
		this.configurationContextList.clear();
		this.configurationContextList.addAll(configurationContextList);
	}

	@Override
	public void setKubernetesContext(String currentContext) {
		this.currentContext = currentContext;
	}
	
	@Override
	public String getKubernetesContext() {
		return currentContext;
	}

	@Override
	public List<EKubeContextConfigurationEntry> getConfigurationContextList() {
		return Collections.unmodifiableList(configurationContextList);
	}

	@Override
	public EKubeContextConfigurationEntry findContextConfigurationEntry(String newCurrentContextName) {
		for (EKubeContextConfigurationEntry ecc: configurationContextList){
			if (StringUtils.equals(ecc.getName(), newCurrentContextName)){
				return ecc;
			}
		}
		return null;
	}
	
	@Override
	public boolean isNamespaceFiltered(String namespaceName) {
		if (!isNamespaceFilteringEnabled()){
			return false;
		}
		return getFilteredNamespaces().contains(namespaceName);
	}

}