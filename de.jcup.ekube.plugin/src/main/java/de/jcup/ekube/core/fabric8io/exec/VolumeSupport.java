package de.jcup.ekube.core.fabric8io.exec;

import java.util.List;
import java.util.Map;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.ExecutionParameters;
import de.jcup.ekube.core.fabric8io.elementaction.Fabric8ioGenericExecutionAction;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import de.jcup.ekube.core.model.NamespaceContainer;
import de.jcup.ekube.core.model.PersistentVolumeClaimElement;
import de.jcup.ekube.core.model.VolumesContainer;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimList;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimStatus;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.client.KubernetesClient;

public class VolumeSupport extends AbstractSupport {

    public VolumeSupport(Fabric8ioSupportContext context) {
        super(context);
    }

    private AddVolumeClaimsFromNamespaceExecutable addVolumesClaims = new AddVolumeClaimsFromNamespaceExecutable();
    private UpdateVolumeClaimStatusExecutable updateVolumeClaimStatus = new UpdateVolumeClaimStatusExecutable();

    public void addVolumeClaimsFromNamespace(NamespaceContainer namespaceContainer) {
        this.addVolumeClaimsFromNamespace(getContext(), getClient(), namespaceContainer);
        ;
    }

    public void addVolumeClaimsFromNamespace(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer) {
        context.getExecutor().execute(context, addVolumesClaims, namespaceContainer, client);
    }

    public void updateStatus(EKubeContext context, KubernetesClient client, PersistentVolumeClaim claim, PersistentVolumeClaimElement claimElement) {
        context.getExecutor().execute(context, updateVolumeClaimStatus, claimElement, client,
                new ExecutionParameters().set(PersistentVolumeClaim.class, claim));
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

    private class AddVolumeClaimsFromNamespaceExecutable implements Fabric8ioSafeExecutableNoData<NamespaceContainer> {

        @Override
        public Void execute(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer, ExecutionParameters parameters) {
            PersistentVolumeClaimList list = client.persistentVolumeClaims().inNamespace(namespaceContainer.getName()).list();
            List<PersistentVolumeClaim> items = list.getItems();
            VolumesContainer fetchPersistentVolumeClaimsContainer = namespaceContainer.fetchPersistentVolumeClaimsContainer();

            /* set this itself as action for rebuild */
            Fabric8ioGenericExecutionAction<NamespaceContainer, Void> x = new Fabric8ioGenericExecutionAction<>(addVolumesClaims,
                    EKubeActionIdentifer.REFRESH_CHILDREN, context, client, namespaceContainer);
            fetchPersistentVolumeClaimsContainer.setAction(x);

            fetchPersistentVolumeClaimsContainer.startOrphanCheck(parameters);
            
            for (PersistentVolumeClaim volumeClaim : items) {
                PersistentVolumeClaimElement newElement = new PersistentVolumeClaimElement(volumeClaim.getMetadata().getUid(), volumeClaim);
                if (! parameters.isHandling(newElement)){
                    continue;
                }
                PersistentVolumeClaimElement container = fetchPersistentVolumeClaimsContainer.addOrReuseExisting(newElement);
                container.setName(volumeClaim.getMetadata().getName());
                updateStatus(context, client, volumeClaim, container);
            }
            fetchPersistentVolumeClaimsContainer.removeOrphans();
            return null;
        }

    }

}
