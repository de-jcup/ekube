package de.jcup.ekube.core.access.fallback;

import de.jcup.ekube.core.access.Pod;

public class FallbackPod implements Pod {

	@Override
	public String getName() {
		return "unknown";
	}

}
