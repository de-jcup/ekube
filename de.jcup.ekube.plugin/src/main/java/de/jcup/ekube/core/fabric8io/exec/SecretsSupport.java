package de.jcup.ekube.core.fabric8io.exec;

import java.util.List;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.fabric8io.elementaction.Fabric8ioGenericExecutionAction;
import de.jcup.ekube.core.model.ConfigMapElement;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import de.jcup.ekube.core.model.NamespaceContainer;
import de.jcup.ekube.core.model.SecretElement;
import de.jcup.ekube.core.model.SecretsContainer;
import io.fabric8.kubernetes.api.model.ConfigMap;
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

	private class UpdateConfigMapsExecutable implements Fabric8ioSafeExecutable<SecretElement, Secret,Void> {

		@Override
		public Void execute(EKubeContext context, KubernetesClient client, SecretElement kubeElement,
				Secret technicalObject) {
			// https://kubernetes.io/docs/concepts/services-networking/service/
			kubeElement.setData(technicalObject.getData());
			kubeElement.setStatus("elements:" + kubeElement.getData().size());
			
			/* set this itself as action for refresh */
			Fabric8ioGenericExecutionAction<SecretElement, Secret, Void> x = new Fabric8ioGenericExecutionAction<>(updateConfigMapsExecutable, EKubeActionIdentifer.REFRESH_STATUS, context, client, kubeElement, technicalObject);
			kubeElement.register(x);
		
			
			return null;
		}
	}

	private class AddSecretsFromNamespaceExecutable implements Fabric8ioSafeExecutableNoData<NamespaceContainer> {

		@Override
		public Void execute(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer,
				Object ignore) {
			String namespaceName = namespaceContainer.getName();
			SecretList serviceList = client.secrets().inNamespace(namespaceName).list();
			List<Secret> items = serviceList.getItems();
			SecretsContainer fetchSecretsContainer = namespaceContainer.fetchSecretsContainer();
			
			/* set this itself as action for rebuild */
			Fabric8ioGenericExecutionAction<NamespaceContainer, Object, Void> x = new Fabric8ioGenericExecutionAction<>(addSecretsFromNamespaceExecutable, EKubeActionIdentifer.REFRESH_CHILDREN, context, client, namespaceContainer, ignore);
			fetchSecretsContainer.register(x);
		
			
			fetchSecretsContainer.startOrphanCheck();
			for (Secret secret : items) {
				SecretElement secretElement = new SecretElement(secret.getMetadata().getUid());
				if (fetchSecretsContainer.isAlreadyFoundAndSoNoOrphan(secretElement)){
					continue;
				}
				secretElement.setName(secret.getMetadata().getName());

				updateStatus(context, client, secret, secretElement);
				
				fetchSecretsContainer.add(secretElement);

			}
			fetchSecretsContainer.removeOrphans();
			return null;
		}
	}

}
