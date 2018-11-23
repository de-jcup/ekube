package de.jcup.ekube.core.access.fallback;

import java.util.Collections;
import java.util.List;

import de.jcup.ekube.core.access.Namespace;
import de.jcup.ekube.core.access.Pod;

public class FallbackNamespace implements Namespace{
	
	private String name;

	public FallbackNamespace(String fallbackName){
		this.name=fallbackName;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<Pod> getPods() {
		return Collections.emptyList();
	}

}
