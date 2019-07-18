package de.jcup.ekube.core.model;

public class ConfigMapsContainer extends AbstractEKubeContainer implements SyntheticKubeElement {

    public ConfigMapsContainer() {
        super(null, null);// no uid available - because synthetic element which
                          // is not existing in kubernetes
        label = "Config maps";
    }

    public ConfigMapElement AddOrReuseExisting(ConfigMapElement element){
        return super.internalAddOrReuseExisting(element);
    }

}