package de.jcup.ekube.core.model;

import java.util.List;

public class PersistentVolumesContainer extends AbstractEKubeContainer implements SyntheticKubeElement {

    public PersistentVolumesContainer() {
        super(null, null);// no uid available - because synthetic element which
                          // is not existing in kubernetes
        label = "Persistent volumes";
    }

    public PersistentVolumeElement addOrReuseExisting(PersistentVolumeElement pvc) {
        return internalAddOrReuseExisting(pvc);
    }
    
    public List<PersistentVolumeElement> getVolumes(){
        return fetchAllChildrenOfType(PersistentVolumeElement.class);
    }
}
