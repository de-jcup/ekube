package de.jcup.ekube.core.model;

import java.util.List;

public class CurrentContextContainer extends AbstractContextContainer {
	
	public void add(NamespaceContainer namespaceContainer){
		addChild(namespaceContainer);
	}

	public List<NamespaceContainer> getNamespaces() {
		return fetchAllChildrenOfType(NamespaceContainer.class);
	}
	

}
