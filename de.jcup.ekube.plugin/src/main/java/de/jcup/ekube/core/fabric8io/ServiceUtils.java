package de.jcup.ekube.core.fabric8io;

import java.util.List;

import de.jcup.ekube.core.model.NamespaceContainer;
import de.jcup.ekube.core.model.ServiceContainer;
import io.fabric8.kubernetes.api.model.LoadBalancerIngress;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.fabric8.kubernetes.api.model.ServiceStatus;
import io.fabric8.kubernetes.client.KubernetesClient;

public class ServiceUtils {

	public static void addServicesFromNamespace(KubernetesClient client, NamespaceContainer namespaceContainer) {
		String namespaceName = namespaceContainer.getName();
		ServiceList serviceList = client.services().inNamespace(namespaceName).list();
		List<Service> items = serviceList.getItems();
		for (Service service: items){
			ServiceContainer serviceContainer = new ServiceContainer();
			serviceContainer.setLabel(service.getMetadata().getName());
			
			updateStatus(service, serviceContainer);
			
			namespaceContainer.fetchServicesContainer().add(serviceContainer);
		}
	}

	public static void updateStatus(Service technicalObject, ServiceContainer kubeElement) {
		ServiceStatus status = technicalObject.getStatus();
		List<LoadBalancerIngress> ingressList = status.getLoadBalancer().getIngress();
		kubeElement.setStatus("loadbalancers:"+ingressList.size());// TODO Auto-generated method stub
		
	}
}
