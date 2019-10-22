package de.jcup.ekube.core.fabric8io.exec.volume;

import java.util.List;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.ExecutionParameters;
import de.jcup.ekube.core.fabric8io.elementaction.Fabric8ioGenericExecutionAction;
import de.jcup.ekube.core.fabric8io.exec.Fabric8ioSafeExecutableNoData;
import de.jcup.ekube.core.model.CurrentContextContainer;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import de.jcup.ekube.core.model.PersistentVolumeElement;
import de.jcup.ekube.core.model.PersistentVolumesContainer;
import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.api.model.PersistentVolumeList;
import io.fabric8.kubernetes.client.KubernetesClient;

class AddVolumesFromNamespaceExecutable implements Fabric8ioSafeExecutableNoData<CurrentContextContainer> {

    /**
     * 
     */
    private final VolumeSupport volumeSupport;

    /**
     * @param volumeSupport
     */
    AddVolumesFromNamespaceExecutable(VolumeSupport volumeSupport) {
        this.volumeSupport = volumeSupport;
    }

    @Override
    public Void execute(EKubeContext context, KubernetesClient client, CurrentContextContainer container, ExecutionParameters parameters) {
        PersistentVolumeList list = client.persistentVolumes().list();
        List<PersistentVolume> items = list.getItems();
        PersistentVolumesContainer fetchPersistentVolumeContainer = container.fetchPersistentVolumesContainer();

        /* set this itself as action for rebuild */
        Fabric8ioGenericExecutionAction<CurrentContextContainer, Void> refreshAction = new Fabric8ioGenericExecutionAction<>(this.volumeSupport.addVolumes,
                EKubeActionIdentifer.REFRESH_CHILDREN, context, client, container);
        fetchPersistentVolumeContainer.register(refreshAction);

        fetchPersistentVolumeContainer.startOrphanCheck(parameters);
        
        for (PersistentVolume volume : items) {
            PersistentVolumeElement newElement = new PersistentVolumeElement(volume.getMetadata().getUid(), volume);
            if (! parameters.isHandling(newElement)){
                continue;
            }
            PersistentVolumeElement pvElement = fetchPersistentVolumeContainer.addOrReuseExisting(newElement);
            pvElement.setName(volume.getMetadata().getName());
            this.volumeSupport.updateStatus(context, client, volume, pvElement);
        }
        fetchPersistentVolumeContainer.removeOrphans();
        return null;
    }

}