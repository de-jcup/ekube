package de.jcup.ekube.core.model;

public class NetworksContainer extends AbstractEKubeContainer implements SyntheticKubeElement {

    public NetworksContainer() {
        super(null, null);// no uid available - because synthetic element which
                          // is not existing in kubernetes
        label = "Network";
    }

    public void add(NetworkPolicyElement networkPolicyElement) {
        addChild(networkPolicyElement);
    }
}
