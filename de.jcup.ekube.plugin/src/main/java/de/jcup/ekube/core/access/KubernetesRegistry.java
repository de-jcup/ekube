package de.jcup.ekube.core.access;

import de.jcup.ekube.core.access.fallback.FallbackKubernetes;

public class KubernetesRegistry {
	private static Kubernetes current=null;

	public static final void set(Kubernetes kubernetes) {
		KubernetesRegistry.current = kubernetes;
	}
	
	public static final Kubernetes get(){
		if (current==null){
			current = new FallbackKubernetes();
		}
		return current;
	}
}


