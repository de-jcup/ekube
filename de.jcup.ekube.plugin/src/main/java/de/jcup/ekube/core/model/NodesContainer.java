package de.jcup.ekube.core.model;

import java.util.List;

public class NodesContainer extends AbstractEKubeContainer implements SyntheticKubeElement {

    public NodesContainer() {
        super(null, null);// no uid available - because synthetic element which
                          // is not existing in kubernetes
        label = "Nodes";
    }

    public NodeContainer addOrReuseExisting(NodeContainer node) {
       return super.internalAddOrReuseExisting(node);
    }
    
    public List<NodeContainer> getNodes(){
        return fetchAllChildrenOfType(NodeContainer.class);
    }
}
