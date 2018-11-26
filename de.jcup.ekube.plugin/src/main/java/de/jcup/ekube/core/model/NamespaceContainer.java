package de.jcup.ekube.core.model;

public class NamespaceContainer extends AbstractEKubeContainer {

	public NamespaceContainer(){
		addChild(new ServicesContainer());
		addChild(new PodsContainer());
		addChild(new VolumesContainer());
		addChild(new NetworksContainer());
		addChild(new ConfigMapsContainer());
	}
	
	public NetworksContainer fetchNetworksContainer(){
		/* always available, no check necessary */
		return fetchAllChildrenOfType(NetworksContainer.class).iterator().next();
	}
	public VolumesContainer fetchPerstitentVolumeClaimsContainer(){
		/* always available, no check necessary */
		return fetchAllChildrenOfType(VolumesContainer.class).iterator().next();
	}
	public ConfigMapsContainer fetchConfigMapsContainer(){
		/* always available, no check necessary */
		return fetchAllChildrenOfType(ConfigMapsContainer.class).iterator().next();
	}
	public PodsContainer fetchPodsContainer(){
		/* always available, no check necessary */
		return fetchAllChildrenOfType(PodsContainer.class).iterator().next();
	}
	public ServicesContainer fetchServicesContainer(){
		/* always available, no check necessary */
		return fetchAllChildrenOfType(ServicesContainer.class).iterator().next();
	}
	
}
