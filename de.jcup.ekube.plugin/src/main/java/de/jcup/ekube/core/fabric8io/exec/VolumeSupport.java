package de.jcup.ekube.core.fabric8io.exec;

import java.util.List;
import java.util.Map;

import de.jcup.ekube.core.EKubeContext;
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

	public void addVolumeClaimsFromNamespace(EKubeContext context, KubernetesClient client,
			NamespaceContainer namespaceContainer) {
		context.getExecutor().execute(context, addVolumesClaims, namespaceContainer, client);
	}

	public void updateStatus(EKubeContext context, KubernetesClient client, PersistentVolumeClaim claim,
			PersistentVolumeClaimElement claimElement) {
		context.getExecutor().execute(context, updateVolumeClaimStatus, claimElement, client, claim);
	}

	private class UpdateVolumeClaimStatusExecutable
			implements Fabric8ioSafeExecutable<PersistentVolumeClaimElement, PersistentVolumeClaim,Void> {

		@Override
		public Void execute(EKubeContext context, KubernetesClient client, PersistentVolumeClaimElement element,
				PersistentVolumeClaim claim) {

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
		public Void execute(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer,
				Object ignore) {
			PersistentVolumeClaimList list = client.persistentVolumeClaims().inNamespace(namespaceContainer.getName())
					.list();
			List<PersistentVolumeClaim> items = list.getItems();
			VolumesContainer fetchPersistentVolumeClaimsContainer = namespaceContainer
					.fetchPerstitentVolumeClaimsContainer();
			
			/* set this itself as action for rebuild */
			Fabric8ioGenericExecutionAction<NamespaceContainer, Object, Void> x = new Fabric8ioGenericExecutionAction<>(addVolumesClaims, EKubeActionIdentifer.REFRESH_CHILDREN, context, client, namespaceContainer, ignore);
			fetchPersistentVolumeClaimsContainer.register(x);
			
			fetchPersistentVolumeClaimsContainer.startOrphanCheck();
			for (PersistentVolumeClaim volumeClaim : items) {
				PersistentVolumeClaimElement container = new PersistentVolumeClaimElement(
						volumeClaim.getMetadata().getUid());
				if (fetchPersistentVolumeClaimsContainer.isAlreadyFoundAndSoNoOrphan(container)) {
					continue;
				}
				// String volume = volumeClaim.getSpec().getVolumeName();
				// PersistentVolume pv =
				// client.persistentVolumes().withName(volume).get();
				container.setName(volumeClaim.getMetadata().getName());
				fetchPersistentVolumeClaimsContainer.add(container);
				updateStatus(context, client, volumeClaim, container);
			}
			fetchPersistentVolumeClaimsContainer.removeOrphans();
			return null;
		}

	}

}
