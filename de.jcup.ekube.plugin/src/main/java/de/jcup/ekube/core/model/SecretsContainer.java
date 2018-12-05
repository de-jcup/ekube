package de.jcup.ekube.core.model;

public class SecretsContainer extends AbstractEKubeContainer implements SyntheticKubeElement {

    public SecretsContainer() {
        super(null, null);// no uid available - because synthetic element which
                          // is not existing in kubernetes
        label = "Secrets";
    }

    public void add(SecretElement secret) {
        addChild(secret);
    }
}
