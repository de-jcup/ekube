package de.jcup.ekube.core.model;

public class NamespaceContainer extends AbstractEKubeContainer {
	private String name;

	public NamespaceContainer(){
		children.add(new ServicesContainer());
		children.add(new PodsContainer());
	}
	
	public PodsContainer fetchPodsContainer(){
		/* always available, no check necessary */
		return fetchAllChildrenOfType(PodsContainer.class).iterator().next();
	}
	public ServicesContainer fetchServicesContainer(){
		/* always available, no check necessary */
		return fetchAllChildrenOfType(ServicesContainer.class).iterator().next();
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
