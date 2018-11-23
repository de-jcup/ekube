package de.jcup.ekube.core.access.fallback;

import java.util.Collections;
import java.util.List;

import de.jcup.ekube.core.access.Cluster;
import de.jcup.ekube.core.access.Kubernetes;

public class FallbackKubernetes implements Kubernetes{

	@Override
	public boolean isConnected() {
		return false;
	}

	@Override
	public void reload() {
	}

	@Override
	public List<Cluster> getClusters() {
		return Collections.emptyList();
	}

	@Override
	public String getName() {
		return "Not available";
	}

}
