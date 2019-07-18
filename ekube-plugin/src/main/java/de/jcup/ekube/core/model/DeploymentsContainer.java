package de.jcup.ekube.core.model;

public class DeploymentsContainer extends AbstractEKubeContainer implements SyntheticKubeElement {

    public DeploymentsContainer() {
        super(null, null);// no uid available - because synthetic element which
                          // is not existing in kubernetes
        label = "Deployments";
    }

    public DeploymentContainer AddOrReuseExisting(DeploymentContainer element){
        return super.internalAddOrReuseExisting(element);
    }

    
}