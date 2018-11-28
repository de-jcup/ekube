package de.jcup.ekube.core.fabric8io.exec;

import java.util.List;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.model.NamespaceContainer;
import de.jcup.ekube.core.model.NetworkPolicyElement;
import io.fabric8.kubernetes.api.model.networking.NetworkPolicy;
import io.fabric8.kubernetes.api.model.networking.NetworkPolicyList;
import io.fabric8.kubernetes.client.KubernetesClient;

public class NetworkSupport extends AbstractSupport {


	public NetworkSupport(Fabric8ioSupportContext context) {
		super(context);
	}

	private AddNetworksFromNamespaceExecutable addNetworksFromNamespaceExecutable = new AddNetworksFromNamespaceExecutable();

	public void addNetworkPolicies(NamespaceContainer namespaceContainer) {
		this.addNetworkPolicies(getContext(), getClient(), namespaceContainer);
	}

	public void addNetworkPolicies(EKubeContext context, KubernetesClient client,
			NamespaceContainer namespaceContainer) {
		context.getExecutor().execute(context, addNetworksFromNamespaceExecutable, namespaceContainer, client);
	}

	private class AddNetworksFromNamespaceExecutable implements Fabric8ioSafeExecutableNoData<NamespaceContainer> {

		@Override
		public void execute(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer,
				Void ignore) {
			String namespaceName = namespaceContainer.getName();
			NetworkPolicyList networkPoliciesList = client.network().networkPolicies().inNamespace(namespaceName)
					.list();
			List<NetworkPolicy> items = networkPoliciesList.getItems();
			for (NetworkPolicy networkPolicy : items) {
				NetworkPolicyElement networkPolicyElement = new NetworkPolicyElement();
				networkPolicyElement.setName(networkPolicy.getMetadata().getName());
				// NetworkPolicySpec spec = networkPolicy.getSpec();
				namespaceContainer.fetchNetworksContainer().add(networkPolicyElement);
			}
		}

	}

}
