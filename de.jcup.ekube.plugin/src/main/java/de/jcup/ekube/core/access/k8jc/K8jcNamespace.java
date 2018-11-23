package de.jcup.ekube.core.access.k8jc;

import java.util.ArrayList;
import java.util.List;

import de.jcup.ekube.core.access.Namespace;
import de.jcup.ekube.core.access.Pod;
import de.jcup.ekube.core.access.fallback.FallbackPod;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.apis.CoreApi;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1PodList;

public class K8jcNamespace implements Namespace{

	String name;
	private K8jcKubernetes kubernetes;
	
	private List<Pod> pods;
	
	public K8jcNamespace(){
		
	}
	
	public K8jcNamespace(K8jcKubernetes kubernetes) {
		this.kubernetes=kubernetes;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<Pod> getPods() {
		if (pods==null){
			pods = createPods();
		}
		return pods;
	}

	private List<Pod> createPods() {
		List<Pod> list = new ArrayList<>();
		try {
			V1PodList podList = kubernetes.api.listNamespacedPod(name,"false",null, null, null, null, null, null, null,null);
			List<V1Pod> items = podList.getItems();
			for (V1Pod pod: items){
				K8jcPod kpod = new K8jcPod(kubernetes);
				kpod.name = pod.getMetadata().getName();
				list.add(kpod);
				
			}
		} catch (ApiException e) {
			kubernetes.errorhandler.logError("Not able to get pods for namespace:"+name, e);
			list.add(new FallbackPod());
		}
		return list;
	}

}
