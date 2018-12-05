package de.jcup.ekube.core.model;

public class ServicesContainer extends AbstractEKubeContainer {

    public ServicesContainer() {
        super(null, null);// no uid available - because synthetic element which
                          // is not existing in kubernetes
        label = "Services";
    }

    public void add(ServiceContainer serviceContainer) {
        addChild(serviceContainer);
    }
}
