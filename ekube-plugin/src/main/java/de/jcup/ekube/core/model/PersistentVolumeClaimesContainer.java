package de.jcup.ekube.core.model;

import java.util.List;

public class PersistentVolumeClaimesContainer extends AbstractEKubeContainer implements SyntheticKubeElement {

    public PersistentVolumeClaimesContainer() {
        super(null, null);// no uid available - because synthetic element which
                          // is not existing in kubernetes
        label = "PVCs";
    }

    public PersistentVolumeClaimElement addOrReuseExisting(PersistentVolumeClaimElement pvc) {
        return internalAddOrReuseExisting(pvc);
    }
    
    public List<PersistentVolumeClaimElement> getVolumeClaims(){
        return fetchAllChildrenOfType(PersistentVolumeClaimElement.class);
    }
}
