package de.jcup.ekube.core.fabric8io;

import java.util.List;

import de.jcup.ekube.core.model.ConfigMapElement;
import de.jcup.ekube.core.model.NamespaceContainer;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapList;
import io.fabric8.kubernetes.client.KubernetesClient;

public class ConfigMapUtils {

	public static void addConfigMapsFromNamespace(KubernetesClient client, NamespaceContainer namespaceContainer) {
		String namespaceName = namespaceContainer.getName();
		ConfigMapList serviceList = client.configMaps().inNamespace(namespaceName).list();
		List<ConfigMap> items = serviceList.getItems();
		for (ConfigMap service: items){
			ConfigMapElement configMapElement = new ConfigMapElement();
			configMapElement.setName(service.getMetadata().getName());
			
			updateStatus(service, configMapElement);
			
			namespaceContainer.fetchConfigMapsContainer().add(configMapElement);
			
		}
	}

	public static void updateStatus(ConfigMap technicalObject, ConfigMapElement kubeElement) {
		//https://kubernetes.io/docs/concepts/services-networking/service/
		kubeElement.setData(technicalObject.getData());
		kubeElement.setStatus("elements:"+kubeElement.getData().size());
	}
}
