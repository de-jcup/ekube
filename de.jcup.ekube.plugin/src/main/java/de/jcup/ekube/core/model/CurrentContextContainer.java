package de.jcup.ekube.core.model;

import java.util.List;

public class CurrentContextContainer extends AbstractContextContainer implements SyntheticKubeElement {
	
	public CurrentContextContainer(){
		super(null);// no uid available - because synthetic element which is not existing in kubernetes
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
