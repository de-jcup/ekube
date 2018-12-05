package de.jcup.ekube.core.fabric8io.exec;

import de.jcup.ekube.core.EKubeContext;
import io.fabric8.kubernetes.client.KubernetesClient;

/**
 * Composite class for all fabric8io support instances. Just create one and have
 * all accessible...
 * 
 * @author Albert Tregnaghi
 *
 */
public class Fabric8ioSupports implements Fabric8ioSupportContext {

    private NetworkSupport networkSupport;
    private PodSupport podSupport;
    private VolumeSupport volumeSupport;
    private ServiceSupport serviceSupport;
    private ConfigMapSupport configMapSupport;
    private NodesSupport nodesSupport;
    private DefaultSupport defaultSupport;
    private SecretsSupport secretsSupport;

    private EKubeContext context;
    private KubernetesClient client;
    private DeploymentsSupport deploymentsSupport;
    private NamespaceSupport namespaceSupport;

    public Fabric8ioSupports(EKubeContext context, KubernetesClient client) {
        this.context = context;
        this.client = client;

        networkSupport = new NetworkSupport(this);
        podSupport = new PodSupport(this);
        volumeSupport = new VolumeSupport(this);
        serviceSupport = new ServiceSupport(this);
        configMapSupport = new ConfigMapSupport(this);
        secretsSupport = new SecretsSupport(this);
        nodesSupport = new NodesSupport(this);
        deploymentsSupport = new DeploymentsSupport(this);
        defaultSupport = new DefaultSupport(this);
        namespaceSupport = new NamespaceSupport(this);
    }

    @Override
    public NamespaceSupport namespaces() {
        return namespaceSupport;
    }

    public DeploymentsSupport deployments() {
        return deploymentsSupport;
    }

    public DefaultSupport defaults() {
        return defaultSupport;
    }

    public NodesSupport nodes() {
        return nodesSupport;
    }

    public NetworkSupport networks() {
        return networkSupport;
    }

    public PodSupport pods() {
        return podSupport;
    }

    public VolumeSupport volumes() {
        return volumeSupport;
    }

    public ServiceSupport services() {
        return serviceSupport;
    }

    public ConfigMapSupport configMaps() {
        return configMapSupport;
    }

    @Override
    public EKubeContext getContext() {
        return context;
    }

    public SecretsSupport secrets() {
        return secretsSupport;
    }

    @Override
    public KubernetesClient getClient() {
        return client;
    }

}
