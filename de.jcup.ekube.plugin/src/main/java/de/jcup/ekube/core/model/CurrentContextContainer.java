package de.jcup.ekube.core.model;

import java.util.List;

public class CurrentContextContainer extends AbstractContextContainer {
	
	public CurrentContextContainer(){
		addChild(new NodesContainer());
	}
	
	public void add(NamespaceContainer namespaceContainer){
		addChild(namespaceContainer);
	}

	public List<NamespaceContainer> getNamespaces() {
		return fetchAllChildrenOfType(NamespaceContainer.class);
	}
	
	public NodesContainer getNodesContainer(){
		return fetchAllChildrenOfType(NodesContainer.class).iterator().next();
	}


}
