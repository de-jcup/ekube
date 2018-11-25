package de.jcup.ekube.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class EKubeConfiguration {

	private List<EKubeConfigurationContext> configurationContextList = new ArrayList<>();
	private String currentContext;

	public File getKubeConfigFile(){
		/* FIXME ATR, 24.11.2018: make this configurable... */
		File cubeConfigFile = new File(System.getProperty("user.home") + "/.kube/config");
		return cubeConfigFile;
	}
	
	public void updateContextInfo(List<EKubeConfigurationContext> configurationContextList){
		this.configurationContextList.clear();
		this.configurationContextList.addAll(configurationContextList);
	}
	
	public void setCurrentContext(String currentContext) {
		this.currentContext = currentContext;
	}
	
	public List<EKubeConfigurationContext> getConfigurationContextList() {
		return Collections.unmodifiableList(configurationContextList);
	}
	
	public String getCurrentContext() {
		return currentContext;
	}

	/**
	 * Finds context configuration or <code>null</code>
	 * @param newCurrentContextName
	 * @return config or <code>null</code> when not found
	 */
	public EKubeConfigurationContext findContextConfiguration(String newCurrentContextName) {
		for (EKubeConfigurationContext ecc: configurationContextList){
			if (StringUtils.equals(ecc.getName(), newCurrentContextName)){
				return ecc;
			}
		}
		return null;
	}
	
}
