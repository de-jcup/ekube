package de.jcup.ekube.core.fabric8io.exec;

import java.util.List;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.model.NamespaceContainer;
import de.jcup.ekube.core.model.SecretElement;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretList;
import io.fabric8.kubernetes.client.KubernetesClient;

public class SecretsSupport extends AbstractSupport {

	public SecretsSupport(Fabric8ioSupportContext context) {
		super(context);
	}

	private AddSecretsFromNamespaceExecutable addSecretsFromNamespaceExecutable = new AddSecretsFromNamespaceExecutable();
	private UpdateConfigMapsExecutable updateConfigMapsExecutable = new UpdateConfigMapsExecutable();

	public void addSecretsFromNamespace(NamespaceContainer namespaceContainer) {
		this.addSecretsFromNamespace(getContext(), getClient(), namespaceContainer);
	}

	public void addSecretsFromNamespace(EKubeContext context, KubernetesClient client,
			NamespaceContainer namespaceContainer) {
		context.getExecutor().execute(context, addSecretsFromNamespaceExecutable, namespaceContainer, client);
	}

	public void updateStatus(EKubeContext context, KubernetesClient client, Secret technicalObject,
			SecretElement kubeElement) {
		context.getExecutor().execute(context, updateConfigMapsExecutable, kubeElement, client, technicalObject);
	}

	private class UpdateConfigMapsExecutable implements Fabric8ioSafeExecutable<SecretElement, Secret> {

		@Override
		public void execute(EKubeContext context, KubernetesClient client, SecretElement kubeElement,
				Secret technicalObject) {
			// https://kubernetes.io/docs/concepts/services-networking/service/
			kubeElement.setData(technicalObject.getData());
			kubeElement.setStatus("elements:" + kubeElement.getData().size());
		}
	}

	private class AddSecretsFromNamespaceExecutable implements Fabric8ioSafeExecutableNoData<NamespaceContainer> {

		@Override
		public void execute(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer,
				Void ignore) {
			String namespaceName = namespaceContainer.getName();
			SecretList serviceList = client.secrets().inNamespace(namespaceName).list();
			List<Secret> items = serviceList.getItems();
			for (Secret secret : items) {
				SecretElement secretElement = new SecretElement();
				secretElement.setName(secret.getMetadata().getName());

				updateStatus(context, client, secret, secretElement);
				
				namespaceContainer.fetchSecretsContainer().add(secretElement);

			}
		}
	}

}
