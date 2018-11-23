package de.jcup.ekube.core.access.k8jc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.jcup.ekube.core.access.Cluster;
import de.jcup.ekube.core.access.ErrorHandler;
import de.jcup.ekube.core.access.Kubernetes;
import de.jcup.ekube.core.access.fallback.FallbackCluster;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.KubeConfig;

public class K8jcKubernetes implements Kubernetes{

	private boolean connected;
	ApiClient client;
	KubeConfig config;
	CoreV1Api api;
	ErrorHandler errorhandler;
	String name;

	private List<Cluster> clusters;
	
	public K8jcKubernetes(ErrorHandler errorHandler, KubeConfig config) throws IOException {
		reconfigure(config);
		this.errorhandler=errorHandler;
	}

	protected void reconfigure(KubeConfig config) throws IOException {
		this.config=config;
		client = Config.fromConfig(config);
		Configuration.setDefaultApiClient(client);
		api = new CoreV1Api();
	}

	@Override
	public boolean isConnected() {
		return connected;
	}
	
	@Override
	public List<Cluster> getClusters() {
		if (clusters==null){
			this.clusters= createClusterList();
		}
		return clusters;
	}

	protected List<Cluster> createClusterList() {
		List<Cluster> result =new ArrayList<>();
		String current = config.getCurrentContext();
		
		ArrayList<Object> clusters = config.getClusters();
		for (Object obj: clusters){
			if (obj instanceof Map){
				K8jcCluster cluster = new K8jcCluster(this);
				Map<?,?> map = (Map<?, ?>) obj;
				cluster.name=(String)map.get("name");
				Map<?,?> clusterData= (Map<?, ?>) map.get("cluster");
				cluster.server= (String) clusterData.get("server");
				cluster.isInCurrentContext=(""+cluster.name).equals(""+current);
				
				/* we do not fetch other data - because there is also secret stuff inside*/
				result.add(cluster);
			}else{
				result.add(new FallbackCluster(obj));
			}
		}
		return result;
	}

	@Override
	public void reload() {
		clusters=null;
	}
	
	@Override
	public String getName() {
		return name;
	}

	public void setCurrentContext(String name) {
		try {
			config.setContext(name);
			reconfigure(config);
		} catch (IOException e) {
			errorhandler.logError("was not able to set new current context:"+name, e);
		}
	}

}
