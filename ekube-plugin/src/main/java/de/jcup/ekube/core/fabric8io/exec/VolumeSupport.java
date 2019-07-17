package de.jcup.ekube.core.fabric8io.exec;

import java.util.List;
import java.util.Map;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.ExecutionParameters;
import de.jcup.ekube.core.fabric8io.elementaction.Fabric8ioGenericExecutionAction;
import de.jcup.ekube.core.model.CurrentContextContainer;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import de.jcup.ekube.core.model.NamespaceContainer;
import de.jcup.ekube.core.model.PersistentVolumeClaimElement;
import de.jcup.ekube.core.model.PersistentVolumeElement;
import de.jcup.ekube.core.model.PersistentVolumeClaimesContainer;
import de.jcup.ekube.core.model.PersistentVolumesContainer;
import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimList;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimStatus;
import io.fabric8.kubernetes.api.model.PersistentVolumeList;
import io.fabric8.kubernetes.api.model.PersistentVolumeStatus;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.client.KubernetesClient;

public class VolumeSupport extends AbstractSupport {

    public VolumeSupport(Fabric8ioSupportContext context) {
        super(context);
    }

    private AddVolumeClaimsFromNamespaceExecutable addVolumesClaims = new AddVolumeClaimsFromNamespaceExecutable();
    private AddVolumesFromNamespaceExecutable addVolumes = new AddVolumesFromNamespaceExecutable();
    private UpdateVolumeClaimStatusExecutable updateVolumeClaimStatus = new UpdateVolumeClaimStatusExecutable();
    private UpdateVolumeStatusExecutable updateVolumeStatus = new UpdateVolumeStatusExecutable();

    public void addVolumeClaimsFromNamespace(NamespaceContainer namespaceContainer) {
        this.addVolumeClaimsFromNamespace(getContext(), getClient(), namespaceContainer);
    }
    public void addVolumes(CurrentContextContainer container) {
        this.addVolumes(getContext(), getClient(), container);
    }

    public void addVolumeClaimsFromNamespace(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer) {
        context.getExecutor().execute(context, addVolumesClaims, namespaceContainer, client);
    }
    
    public void addVolumes(EKubeContext context, KubernetesClient client, CurrentContextContainer container) {
        context.getExecutor().execute(context, addVolumes, container, client);
    }

    public void updateStatus(EKubeContext context, KubernetesClient client, PersistentVolumeClaim claim, PersistentVolumeClaimElement claimElement) {
        context.getExecutor().execute(context, updateVolumeClaimStatus, claimElement, client,
                new ExecutionParameters().set(PersistentVolumeClaim.class, claim));
    }
    
    public void updateStatus(EKubeContext context, KubernetesClient client, PersistentVolume volume, PersistentVolumeElement claimElement) {
        context.getExecutor().execute(context, updateVolumeStatus, claimElement, client,
                new ExecutionParameters().set(PersistentVolume.class, volume));
    }

    private class UpdateVolumeClaimStatusExecutable implements Fabric8ioSafeExecutableNoData<PersistentVolumeClaimElement> {

        @Override
        public Void execute(EKubeContext context, KubernetesClient client, PersistentVolumeClaimElement element, ExecutionParameters parameters) {

            PersistentVolumeClaim claim = parameters.get(PersistentVolumeClaim.class);
            PersistentVolumeClaimStatus status = claim.getStatus();

            StringBuilder sb = new StringBuilder();
            sb.append(status.getPhase());
            Map<String, Quantity> capacity = status.getCapacity();
            Quantity storage = capacity.get("storage");
            if (storage != null) {
                sb.append(" ").append(storage.getAmount());
            }
            List<String> modes = status.getAccessModes();
            for (String mode : modes) {
                sb.append(" ").append(mode);
            }
            element.setStatus(sb.toString());
            return null;
        }

    }
    private class UpdateVolumeStatusExecutable implements Fabric8ioSafeExecutableNoData<PersistentVolumeElement> {

        @Override
        public Void execute(EKubeContext context, KubernetesClient client, PersistentVolumeElement element, ExecutionParameters parameters) {

            PersistentVolume claim = parameters.get(PersistentVolume.class);
            PersistentVolumeStatus status = claim.getStatus();

            StringBuilder sb = new StringBuilder();
            sb.append(status.getPhase());
            sb.append(" ").append(status.getMessage());
            element.setStatus(sb.toString());
            return null;
        }

    }

    private class AddVolumeClaimsFromNamespaceExecutable implements Fabric8ioSafeExecutableNoData<NamespaceContainer> {

        @Override
        public Void execute(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer, ExecutionParameters parameters) {
            PersistentVolumeClaimList list = client.persistentVolumeClaims().inNamespace(namespaceContainer.getName()).list();
            List<PersistentVolumeClaim> items = list.getItems();
            PersistentVolumeClaimesContainer fetchPersistentVolumeClaimsContainer = namespaceContainer.fetchPersistentVolumeClaimsContainer();

            /* set this itself as action for rebuild */
            Fabric8ioGenericExecutionAction<NamespaceContainer, Void> x = new Fabric8ioGenericExecutionAction<>(addVolumesClaims,
                    EKubeActionIdentifer.REFRESH_CHILDREN, context, client, namespaceContainer);
            fetchPersistentVolumeClaimsContainer.register(x);

            fetchPersistentVolumeClaimsContainer.startOrphanCheck(parameters);
            
            for (PersistentVolumeClaim volumeClaim : items) {
                PersistentVolumeClaimElement newElement = new PersistentVolumeClaimElement(volumeClaim.getMetadata().getUid(), volumeClaim);
                if (! parameters.isHandling(newElement)){
                    continue;
                }
                PersistentVolumeClaimElement pvcElement = fetchPersistentVolumeClaimsContainer.addOrReuseExisting(newElement);
                pvcElement.setName(volumeClaim.getMetadata().getName());
                updateStatus(context, client, volumeClaim, pvcElement);
            }
            fetchPersistentVolumeClaimsContainer.removeOrphans();
            return null;
        }

    }

    
    private class AddVolumesFromNamespaceExecutable implements Fabric8ioSafeExecutableNoData<CurrentContextContainer> {

        @Override
        public Void execute(EKubeContext context, KubernetesClient client, CurrentContextContainer container, ExecutionParameters parameters) {
            PersistentVolumeList list = client.persistentVolumes().list();
            List<PersistentVolume> items = list.getItems();
            PersistentVolumesContainer fetchPersistentVolumeContainer = container.fetchPersistentVolumesContainer();

            /* set this itself as action for rebuild */
            Fabric8ioGenericExecutionAction<CurrentContextContainer, Void> refreshAction = new Fabric8ioGenericExecutionAction<>(addVolumes,
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
                updateStatus(context, client, volume, pvElement);
            }
            fetchPersistentVolumeContainer.removeOrphans();
            return null;
        }

    }
}
