package de.jcup.ekube.core.fabric8io;

import java.util.List;

import de.jcup.ekube.core.model.NamespaceContainer;
import de.jcup.ekube.core.model.NetworkPolicyElement;
import io.fabric8.kubernetes.api.model.networking.NetworkPolicy;
import io.fabric8.kubernetes.api.model.networking.NetworkPolicyList;
import io.fabric8.kubernetes.api.model.networking.NetworkPolicySpec;
import io.fabric8.kubernetes.client.KubernetesClient;

public class NetworkUtils {

	public static void addNetworkPolicies(KubernetesClient client, NamespaceContainer namespaceContainer) {
		String namespaceName = namespaceContainer.getName();
		NetworkPolicyList networkPoliciesList = client.network().networkPolicies().inNamespace(namespaceName).list();
		List<NetworkPolicy> items = networkPoliciesList.getItems();
		for (NetworkPolicy networkPolicy: items){
			NetworkPolicyElement networkPolicyElement = new NetworkPolicyElement();
			networkPolicyElement.setName(networkPolicy.getMetadata().getName());
			NetworkPolicySpec spec = networkPolicy.getSpec();
			namespaceContainer.fetchNetworksContainer().add(networkPolicyElement);
		}
	}

}
