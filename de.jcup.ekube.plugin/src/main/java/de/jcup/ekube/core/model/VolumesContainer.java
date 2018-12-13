package de.jcup.ekube.core.model;

import java.util.List;

public class VolumesContainer extends AbstractEKubeContainer implements SyntheticKubeElement {

    public VolumesContainer() {
        super(null, null);// no uid available - because synthetic element which
                          // is not existing in kubernetes
        label = "Volumes";
    }

    public PersistentVolumeClaimElement addOrReuseExisting(PersistentVolumeClaimElement pvc) {
        return internalAddOrReuseExisting(pvc);
    }
    
    public List<PersistentVolumeClaimElement> getVolumeClaims(){
        return fetchAllChildrenOfType(PersistentVolumeClaimElement.class);
    }
}
