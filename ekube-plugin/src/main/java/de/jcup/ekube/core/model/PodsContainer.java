package de.jcup.ekube.core.model;

import java.util.List;

public class PodsContainer extends AbstractEKubeContainer implements SyntheticKubeElement {

    public PodsContainer() {
        super(null, null);// no uid available - because synthetic element which
                          // is not existing in kubernetes
        label = "Pods";
    }

    public PodContainer addOrReuseExisting(PodContainer pod) {
        return super.internalAddOrReuseExisting(pod);
    }

    public List<PodContainer> getPods() {
        return fetchAllChildrenOfType(PodContainer.class);
    }
}
