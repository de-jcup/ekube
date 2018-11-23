package de.jcup.ekube.core.access.fallback;

import java.util.Collections;
import java.util.List;

import de.jcup.ekube.core.access.Cluster;
import de.jcup.ekube.core.access.Namespace;

public class FallbackCluster implements Cluster{

	private Object unrecognized;
	private boolean currentContext;

	public FallbackCluster(Object obj) {
		this.unrecognized=obj;
	}

	public Object getUnrecognized() {
		return unrecognized;
	}
	
	@Override
	public String getName() {
		return "unknown";
	}

	@Override
	public String getServer() {
		return "unknown";
	}

	@Override
	public List<Namespace> getNamespaces() {
		return Collections.emptyList();
	}

	@Override
	public void reload() {
		
	}

	@Override
	public boolean isInCurrentContext() {
		return currentContext;
	}

	@Override
	public void setAsCurrentContext() {
		this.currentContext=true;
	}

}
