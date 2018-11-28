package de.jcup.ekube.core.fabric8io.exec;

import java.util.List;
import java.util.Map;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.model.NamespaceContainer;
import de.jcup.ekube.core.model.PersistentVolumeClaimElement;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimList;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimStatus;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.client.KubernetesClient;

public class VolumeSupport extends AbstractSupport{


	public VolumeSupport(Fabric8ioSupportContext context) {
		super(context);
	}

	private AddVolumeClaimsFromNamespaceExecutable addVolumesClaims = new AddVolumeClaimsFromNamespaceExecutable();
	private UpdateVolumeClaimStatusExecutable updateVolumeClaimStatus = new UpdateVolumeClaimStatusExecutable();

	public void addVolumeClaimsFromNamespace(NamespaceContainer namespaceContainer) {
		this.addVolumeClaimsFromNamespace(getContext(), getClient(), namespaceContainer);;
	}
	
	public void addVolumeClaimsFromNamespace(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer) {
		context.getExecutor().execute(context, addVolumesClaims, namespaceContainer, client);
	}
	
	public void updateStatus(EKubeContext context, KubernetesClient client, PersistentVolumeClaim claim, PersistentVolumeClaimElement claimElement){
		context.getExecutor().execute(context, updateVolumeClaimStatus, claimElement, client,claim);
	}
	
	private class UpdateVolumeClaimStatusExecutable implements Fabric8ioSafeExecutable<PersistentVolumeClaimElement,PersistentVolumeClaim>{

		@Override
		public void execute(EKubeContext context, KubernetesClient client, PersistentVolumeClaimElement element,
				PersistentVolumeClaim claim) {
			
			PersistentVolumeClaimStatus status = claim.getStatus();
			
			StringBuilder sb = new StringBuilder();
			sb.append(status.getPhase());
			Map<String, Quantity> capacity = status.getCapacity();
			Quantity storage = capacity.get("storage");
			if (storage!=null){
				sb.append(" ").append(storage.getAmount());
			}
			List<String> modes = status.getAccessModes();
			for (String mode: modes){
				sb.append(" ").append(mode);
			}
			element.setStatus(sb.toString());
			
		}
		
	}
	
	private class AddVolumeClaimsFromNamespaceExecutable implements Fabric8ioSafeExecutableNoData<NamespaceContainer>{

		@Override
		public void execute(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer, Void ignore) {
			PersistentVolumeClaimList list = client.persistentVolumeClaims().inNamespace(namespaceContainer.getName()).list();
			List<PersistentVolumeClaim> items = list.getItems();
			for (PersistentVolumeClaim volumeClaim: items){
				PersistentVolumeClaimElement container = new PersistentVolumeClaimElement();
//				String volume = volumeClaim.getSpec().getVolumeName();
//				PersistentVolume pv = client.persistentVolumes().withName(volume).get();
				container.setName(volumeClaim.getMetadata().getName());
				namespaceContainer.fetchPerstitentVolumeClaimsContainer().add(container);
				updateStatus(context,client, volumeClaim, container);
			}

			client.persistentVolumes().list();
		}
		
	}

	
}
