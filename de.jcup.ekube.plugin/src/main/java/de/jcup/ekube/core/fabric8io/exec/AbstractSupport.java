package de.jcup.ekube.core.fabric8io.exec;

import de.jcup.ekube.core.EKubeContext;
import io.fabric8.kubernetes.client.KubernetesClient;

public class AbstractSupport {
	
	Fabric8ioSupportContext supportContext;

	public AbstractSupport(Fabric8ioSupportContext context){
		this.supportContext=context;
	}

	protected EKubeContext getContext(){
		return supportContext.getContext();
	}
	
	protected KubernetesClient getClient(){
		return supportContext.getClient();
	}
	
}
