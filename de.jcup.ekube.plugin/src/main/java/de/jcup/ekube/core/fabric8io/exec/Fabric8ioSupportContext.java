package de.jcup.ekube.core.fabric8io.exec;

import io.fabric8.kubernetes.client.KubernetesClient;

import de.jcup.ekube.core.EKubeContext;

public interface Fabric8ioSupportContext {

	public EKubeContext getContext();

	public KubernetesClient getClient();

	public DeploymentsSupport deployments();
	
	public DefaultSupport defaults();

	public NodesSupport nodes();

	public NetworkSupport networks();

	public PodSupport pods();

	public VolumeSupport volumes();

	public ServiceSupport services();

	public ConfigMapSupport configMaps();
	
	public NamespaceSupport namespaces();

	public SecretsSupport secrets();
}
