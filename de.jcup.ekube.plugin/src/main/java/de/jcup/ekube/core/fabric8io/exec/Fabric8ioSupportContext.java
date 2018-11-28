package de.jcup.ekube.core.fabric8io.exec;

import io.fabric8.kubernetes.client.KubernetesClient;

import de.jcup.ekube.core.EKubeContext;

public interface Fabric8ioSupportContext {

	public EKubeContext getContext();
	
	public KubernetesClient getClient();
	
	public DefaultSupport defaults();
}
