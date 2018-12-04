package de.jcup.ekube.core.fabric8io.exec;

import java.util.List;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.fabric8io.elementaction.Fabric8ioGenericExecutionAction;
import de.jcup.ekube.core.model.ConfigMapElement;
import de.jcup.ekube.core.model.ConfigMapsContainer;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import de.jcup.ekube.core.model.NamespaceContainer;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapList;
import io.fabric8.kubernetes.client.KubernetesClient;

public class ConfigMapSupport extends AbstractSupport {

	public ConfigMapSupport(Fabric8ioSupportContext context) {
		super(context);
	}

	private UpdateConfigMapsExecutable updateConfigMapsExecutable = new UpdateConfigMapsExecutable();
	private AddConfigMapsFromNamespaceExecutable addConfigMapsFromNamespaceExecutable = new AddConfigMapsFromNamespaceExecutable();

	public void addConfigMapsFromNamespace(NamespaceContainer namespaceContainer) {
		this.addConfigMapsFromNamespace(getContext(), getClient(), namespaceContainer);
	}

	public void addConfigMapsFromNamespace(EKubeContext context, KubernetesClient client,
			NamespaceContainer namespaceContainer) {
		context.getExecutor().execute(context, addConfigMapsFromNamespaceExecutable, namespaceContainer, client);
	}

	public void updateStatus(EKubeContext context, KubernetesClient client, ConfigMap technicalObject,
			ConfigMapElement kubeElement) {
		context.getExecutor().execute(context, updateConfigMapsExecutable, kubeElement, client, technicalObject);
	}

	private class UpdateConfigMapsExecutable implements Fabric8ioSafeExecutable<ConfigMapElement, ConfigMap, Void> {

		@Override
		public Void execute(EKubeContext context, KubernetesClient client, ConfigMapElement kubeElement,
				ConfigMap technicalObject) {
			// https://kubernetes.io/docs/concepts/services-networking/service/
			kubeElement.setData(technicalObject.getData());
			kubeElement.setStatus("elements:" + kubeElement.getData().size());
			
			/* set this itself as action for refresh */
			Fabric8ioGenericExecutionAction<ConfigMapElement, ConfigMap, Void> x = new Fabric8ioGenericExecutionAction<>(updateConfigMapsExecutable, EKubeActionIdentifer.REFRESH_STATUS, context, client, kubeElement, technicalObject);
			kubeElement.register(x);
			
			return null;
		}
	}

	private class AddConfigMapsFromNamespaceExecutable implements Fabric8ioSafeExecutableNoData<NamespaceContainer> {

		@Override
		public Void execute(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer,
				Object ignore) {
			String namespaceName = namespaceContainer.getName();
			ConfigMapList serviceList = client.configMaps().inNamespace(namespaceName).list();
			List<ConfigMap> items = serviceList.getItems();
			
			ConfigMapsContainer configMapsContainer = namespaceContainer.fetchConfigMapsContainer();
			
			/* set this itself as action for rebuild */
			Fabric8ioGenericExecutionAction<NamespaceContainer, Object, Void> x = new Fabric8ioGenericExecutionAction<>(addConfigMapsFromNamespaceExecutable, EKubeActionIdentifer.REFRESH_CHILDREN, context, client, namespaceContainer, ignore);
			configMapsContainer.register(x);
		
			
			configMapsContainer.startOrphanCheck();
			for (ConfigMap service : items) {
				ConfigMapElement configMapElement = new ConfigMapElement(service.getMetadata().getUid());
				if (configMapsContainer.isAlreadyFoundAndSoNoOrphan(configMapElement)){
					continue;
				}
				configMapElement.setName(service.getMetadata().getName());

				updateStatus(context, client, service, configMapElement);

				configMapsContainer.add(configMapElement);

			}
			configMapsContainer.removeOrphans();
			return null;
		}

	}

}
