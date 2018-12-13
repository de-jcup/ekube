package de.jcup.ekube.core.model;

import java.util.List;

public class ServicesContainer extends AbstractEKubeContainer {

    public ServicesContainer() {
        super(null, null);// no uid available - because synthetic element which
                          // is not existing in kubernetes
        label = "Services";
    }

    public ServiceContainer addOrReuseExisting(ServiceContainer service) {
        return internalAddOrReuseExisting(service);
    }

    public List<ServiceContainer> getServices() {
        return fetchAllChildrenOfType(ServiceContainer.class);
    }
}
