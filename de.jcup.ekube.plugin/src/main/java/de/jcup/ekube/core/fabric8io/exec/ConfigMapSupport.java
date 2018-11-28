package de.jcup.ekube.core.fabric8io.exec;

import java.util.List;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.model.ConfigMapElement;
import de.jcup.ekube.core.model.NamespaceContainer;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapList;
import io.fabric8.kubernetes.client.KubernetesClient;

public class ConfigMapSupport {
	
	private UpdateConfigMapsExecutable updateConfigMapsExecutable = new UpdateConfigMapsExecutable();
	private AddConfigMapsFromNamespaceExecutable addConfigMapsFromNamespaceExecutable = new AddConfigMapsFromNamespaceExecutable();

	public void addConfigMapsFromNamespace(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer) {
		context.getExecutor().execute(context, addConfigMapsFromNamespaceExecutable, namespaceContainer, client);
	}

	public void updateStatus(EKubeContext context, KubernetesClient client,  ConfigMap technicalObject, ConfigMapElement kubeElement) {
		context.getExecutor().execute(context, updateConfigMapsExecutable, kubeElement, client,technicalObject);
	}
	
	private class UpdateConfigMapsExecutable implements Fabric8ioSafeExecutable<ConfigMapElement, ConfigMap>{

		@Override
		public void execute(EKubeContext context, KubernetesClient client, ConfigMapElement kubeElement, ConfigMap technicalObject) {
			//https://kubernetes.io/docs/concepts/services-networking/service/
			kubeElement.setData(technicalObject.getData());
			kubeElement.setStatus("elements:"+kubeElement.getData().size());
		}
	}
	
	private class AddConfigMapsFromNamespaceExecutable implements Fabric8ioSafeExecutableNoData<NamespaceContainer>{

		@Override
		public void execute(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer, Void ignore) {
			String namespaceName = namespaceContainer.getName();
			ConfigMapList serviceList = client.configMaps().inNamespace(namespaceName).list();
			List<ConfigMap> items = serviceList.getItems();
			for (ConfigMap service: items){
				ConfigMapElement configMapElement = new ConfigMapElement();
				configMapElement.setName(service.getMetadata().getName());
				
				updateStatus(context, client, service, configMapElement);
				
				namespaceContainer.fetchConfigMapsContainer().add(configMapElement);
				
			}
		}
		
	}
}
