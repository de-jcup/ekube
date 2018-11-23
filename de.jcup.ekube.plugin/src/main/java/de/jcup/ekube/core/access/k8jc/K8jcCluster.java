package de.jcup.ekube.core.access.k8jc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.jcup.ekube.core.access.Cluster;
import de.jcup.ekube.core.access.Namespace;
import de.jcup.ekube.core.access.fallback.FallbackNamespace;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.models.V1Namespace;
import io.kubernetes.client.models.V1NamespaceList;

public class K8jcCluster implements Cluster {

	private K8jcKubernetes kubernetes;
	
	String name;
	String server;
	private List<Namespace> namespaces;

	boolean isInCurrentContext;

	public K8jcCluster(K8jcKubernetes kubernetes) {
		this.kubernetes = kubernetes;
	}
	
	@Override
	public void setAsCurrentContext() {
		this.kubernetes.setCurrentContext(name);
	}

	@Override
	public String toString() {
		return "Context:"+name+", server:"+server;
	}
	
	@Override
	public String getServer() {
		return server;
	}

	@Override
	public String getName() {
		return name;
	}
	
	public boolean isInCurrentContext() {
		return isInCurrentContext;
	}

	@Override
	public List<Namespace> getNamespaces() {
		if (! isInCurrentContext){
			return Collections.emptyList();
		}
		if (namespaces==null){
			namespaces= createNamespaces();
		}
		return namespaces;
	}

	protected List<Namespace> createNamespaces() {
		List<Namespace> list=new ArrayList<>();
		try {
			V1NamespaceList v1List = kubernetes.api.listNamespace("false", null, null, null, null, null,null,null, null);
			List<V1Namespace> items = v1List.getItems();
			for (V1Namespace item: items){
				K8jcNamespace namespace = new K8jcNamespace(kubernetes);
				namespace.name= item.getMetadata().getName();
				list.add(namespace);
			}
			
		} catch (ApiException e) {
			kubernetes.errorhandler.logError("Cannot fetch namespaces of cluster "+name, e);
			/* FIXME ATR, 22.11.2018: handle errors correctly and not this way..., fallback okay, but error message in text... */
			list.add(new FallbackNamespace("failed:"+e.getMessage()));
		}
		return list;
	}

	@Override
	public void reload() {
		namespaces=null;
	}
}
