package de.jcup.ekube.core.fabric8io;

import java.util.List;

import de.jcup.ekube.core.model.NamespaceContainer;
import de.jcup.ekube.core.model.ServiceContainer;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.ServiceSpec;
import io.fabric8.kubernetes.client.KubernetesClient;

public class ServiceUtils {

	public static void addServicesFromNamespace(KubernetesClient client, NamespaceContainer namespaceContainer) {
		String namespaceName = namespaceContainer.getName();
		ServiceList serviceList = client.services().inNamespace(namespaceName).list();
		List<Service> items = serviceList.getItems();
		for (Service service: items){
			ServiceContainer serviceContainer = new ServiceContainer();
			serviceContainer.setName(service.getMetadata().getName());
			
			updateStatus(service, serviceContainer);
			
			namespaceContainer.fetchServicesContainer().add(serviceContainer);
		}
	}

	public static void updateStatus(Service technicalObject, ServiceContainer kubeElement) {
		//https://kubernetes.io/docs/concepts/services-networking/service/
		ServiceSpec spec = technicalObject.getSpec();
		
		StringBuilder sb = new StringBuilder();
		
		List<String> externalIps = spec.getExternalIPs();
		
//		Example:
//			spec:
//				  type: NodePort
//				  selector:
//				    name: xyz-server
//				  ports:
//				  - name: https 
//				    port: 8443 #<- internal spring boot port
//				    targetPort: 8443 #<- docker target port
//				    nodePort: 30443 #<- external port - outside kubernetes
//				  - name: debug
//				    port: 5005  #<- internal spring boot port
//				    targetPort: 5005
//				    nodePort: 30366 #<- external port - outside kubernetes
		
		
		for (ServicePort servicePort: spec.getPorts()){
			sb.append(servicePort.getName());
			sb.append(":");
			
			Integer nodePort = servicePort.getNodePort();
			if (nodePort!=null){
				sb.append(nodePort);
				sb.append(" ");
			}else{
				sb.append("no nodeport");
			}
		}
		
		kubeElement.setStatus(sb.toString());
	}
}
