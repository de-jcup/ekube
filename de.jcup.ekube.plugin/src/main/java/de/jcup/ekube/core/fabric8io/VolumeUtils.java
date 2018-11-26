package de.jcup.ekube.core.fabric8io;

import java.util.List;
import java.util.Map;

import de.jcup.ekube.core.model.NamespaceContainer;
import de.jcup.ekube.core.model.PersistentVolumeClaimElement;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimList;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimStatus;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.client.KubernetesClient;

public class VolumeUtils {

	public static void addVolumeClaimsFromNamespace(KubernetesClient client, NamespaceContainer namespaceContainer) {
		PersistentVolumeClaimList list = client.persistentVolumeClaims().inNamespace(namespaceContainer.getName()).list();
		List<PersistentVolumeClaim> items = list.getItems();
		for (PersistentVolumeClaim volumeClaim: items){
			PersistentVolumeClaimElement container = new PersistentVolumeClaimElement();
//			String volume = volumeClaim.getSpec().getVolumeName();
//			PersistentVolume pv = client.persistentVolumes().withName(volume).get();
			container.setName(volumeClaim.getMetadata().getName());
			namespaceContainer.fetchPerstitentVolumeClaimsContainer().add(container);
			updateStatus(volumeClaim, container);
		}

		client.persistentVolumes().list();
	}
	
	public static void updateStatus(PersistentVolumeClaim claim, PersistentVolumeClaimElement container){
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
		container.setStatus(sb.toString());
	}
}
