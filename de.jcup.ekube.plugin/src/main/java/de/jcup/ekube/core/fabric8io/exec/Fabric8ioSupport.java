package de.jcup.ekube.core.fabric8io.exec;

/**
 * Composite class for all fabric8io support instances. Just create one and have all accessible...
 * @author Albert Tregnaghi
 *
 */
public class Fabric8ioSupport {

	private NetworkSupport networkSupport = new NetworkSupport();
	private PodSupport podSupport = new PodSupport();
	private VolumeSupport volumeSupport = new VolumeSupport();
	private ServiceSupport serviceSupport = new ServiceSupport();
	private ConfigMapSupport configMapSupport = new ConfigMapSupport();
	
	
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
}
