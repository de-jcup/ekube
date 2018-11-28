package de.jcup.ekube.core.fabric8io.exec;

import java.util.List;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.model.NamespaceContainer;
import de.jcup.ekube.core.model.ServiceContainer;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.ServiceSpec;
import io.fabric8.kubernetes.client.KubernetesClient;

public class ServiceSupport {

	private AddServicesFromNamespaceExecutable addPods = new AddServicesFromNamespaceExecutable();
	private UpdatePodsExecutable updateService = new UpdatePodsExecutable();

	public void addServicesFromNamespace(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer) {
		context.getExecutor().execute(context, addPods, namespaceContainer, client);
	}
	
	public void updateStatus(EKubeContext context, KubernetesClient client, Service service, ServiceContainer podContainer){
		context.getExecutor().execute(context, updateService, podContainer,client,service);
	}
	
	private class UpdatePodsExecutable implements Fabric8ioSafeExecutable<ServiceContainer, Service>{

		@Override
		public void execute(EKubeContext context, KubernetesClient client, ServiceContainer podContainer, Service pod) {
			//https://kubernetes.io/docs/concepts/services-networking/service/
			ServiceSpec spec = pod.getSpec();
			
			StringBuilder sb = new StringBuilder();
			
			List<String> externalIps = spec.getExternalIPs();
			
//			Example:
//				spec:
//					  type: NodePort
//					  selector:
//					    name: xyz-server
//					  ports:
//					  - name: https 
//					    port: 8443 #<- internal spring boot port
//					    targetPort: 8443 #<- docker target port
//					    nodePort: 30443 #<- external port - outside kubernetes
//					  - name: debug
//					    port: 5005  #<- internal spring boot port
//					    targetPort: 5005
//					    nodePort: 30366 #<- external port - outside kubernetes
			
			
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
			
			podContainer.setStatus(sb.toString());
		}
		
	}
	private class AddServicesFromNamespaceExecutable implements Fabric8ioSafeExecutableNoData<NamespaceContainer>{

		@Override
		public void execute(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer, Void ignore) {
			String namespaceName = namespaceContainer.getName();
			ServiceList serviceList = client.services().inNamespace(namespaceName).list();
			List<Service> items = serviceList.getItems();
			for (Service service: items){
				ServiceContainer serviceContainer = new ServiceContainer();
				serviceContainer.setName(service.getMetadata().getName());
				
				updateStatus(context, client, service, serviceContainer);
				
				namespaceContainer.fetchServicesContainer().add(serviceContainer);
			}
		}
		
	}
}
