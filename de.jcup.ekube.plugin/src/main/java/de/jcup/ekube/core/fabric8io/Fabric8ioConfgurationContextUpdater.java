package de.jcup.ekube.core.fabric8io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.jcup.ekube.core.EKubeConfiguration;
import de.jcup.ekube.core.EKubeConfigurationContext;
import de.jcup.ekube.core.ErrorHandler;
import io.fabric8.kubernetes.api.model.Config;
import io.fabric8.kubernetes.api.model.Context;
import io.fabric8.kubernetes.api.model.NamedContext;
import io.fabric8.kubernetes.client.internal.KubeConfigUtils;

public class Fabric8ioConfgurationContextUpdater {
	
	private ErrorHandler errorHandler;

	public Fabric8ioConfgurationContextUpdater(ErrorHandler errorHandler){
		this.errorHandler=errorHandler;
	}

	/**
	 * Updates list of available contexts, does also setup current context if not already set in configuration
	 * @param configuration
	 */
	public void update(EKubeConfiguration configuration){
		List<EKubeConfigurationContext> list = new ArrayList<>();
		try {
			Config config = KubeConfigUtils.parseConfig(configuration.getKubeConfigFile());
			String currentContext = config.getCurrentContext();
			if (configuration.getCurrentContext()==null){
				configuration.setCurrentContext(currentContext);
			}
			
			List<NamedContext> namedContexts = config.getContexts();
			
			for (NamedContext namedContext: namedContexts){
				Context context  =namedContext.getContext();
				
				/* we build context container - does only contain some properties thats all - no children*/
				EKubeConfigurationContext configurationContext = new EKubeConfigurationContext();
				String contextName = namedContext.getName();
				configurationContext.setName(contextName);
				configurationContext.setUser(context.getUser());
				configurationContext.setCluster(context.getCluster());
				
				list.add(configurationContext);
			}
			
		} catch (IOException e) {
			errorHandler.logError("Was not able to fetch context definitions", e);
		}
		configuration.updateContextInfo(list);
	}
}
