package de.jcup.ekube.core.fabric8io.exec;

import java.util.List;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.ExecutionParameters;
import de.jcup.ekube.core.fabric8io.elementaction.Fabric8ioGenericExecutionAction;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import de.jcup.ekube.core.model.NamespaceContainer;
import de.jcup.ekube.core.model.ServiceContainer;
import de.jcup.ekube.core.model.ServicesContainer;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.ServiceSpec;
import io.fabric8.kubernetes.client.KubernetesClient;

public class ServiceSupport extends AbstractSupport {

    public ServiceSupport(Fabric8ioSupportContext context) {
        super(context);
    }

    private AddServicesFromNamespaceExecutable addPods = new AddServicesFromNamespaceExecutable();
    private UpdatePodsExecutable updateService = new UpdatePodsExecutable();

    public void addServicesFromNamespace(NamespaceContainer namespaceContainer) {
        this.addServicesFromNamespace(getContext(), getClient(), namespaceContainer);
    }

    public void addServicesFromNamespace(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer) {
        context.getExecutor().execute(context, addPods, namespaceContainer, client);
    }

    public void updateStatus(EKubeContext context, KubernetesClient client, ServiceContainer podContainer, Service service) {
        context.getExecutor().execute(context, updateService, podContainer, client, new ExecutionParameters().set(Service.class, service));
    }

    private class UpdatePodsExecutable implements Fabric8ioSafeExecutable<ServiceContainer, Void> {

        @Override
        public Void execute(EKubeContext context, KubernetesClient client, ServiceContainer serviceContainer, ExecutionParameters parameters) {
            // https://kubernetes.io/docs/concepts/services-networking/service/

            ServiceSpec spec = parameters.get(Service.class).getSpec();

            StringBuilder sb = new StringBuilder();

            List<String> externalIps = spec.getExternalIPs();

            // Example:
            // spec:
            // type: NodePort
            // selector:
            // name: xyz-server
            // ports:
            // - name: https
            // port: 8443 #<- internal spring boot port
            // targetPort: 8443 #<- docker target port
            // nodePort: 30443 #<- external port - outside kubernetes
            // - name: debug
            // port: 5005 #<- internal spring boot port
            // targetPort: 5005
            // nodePort: 30366 #<- external port - outside kubernetes

            for (ServicePort servicePort : spec.getPorts()) {
                if (servicePort.getName() != null) {
                    sb.append(servicePort.getName());
                    sb.append(":");
                }

                Integer nodePort = servicePort.getNodePort();
                if (nodePort != null) {
                    sb.append(nodePort);
                    sb.append(" ");
                } else {
                    sb.append("no nodeport");
                }
            }

            serviceContainer.setStatus(sb.toString());
            return null;
        }

    }

    private class AddServicesFromNamespaceExecutable implements Fabric8ioSafeExecutableNoData<NamespaceContainer> {

        @Override
        public Void execute(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer, ExecutionParameters parameters) {
            String namespaceName = namespaceContainer.getName();
            ServiceList serviceList = client.services().inNamespace(namespaceName).list();
            List<Service> items = serviceList.getItems();
            ServicesContainer fetchServicesContainer = namespaceContainer.fetchServicesContainer();

            /* set this itself as action for rebuild */
            Fabric8ioGenericExecutionAction<NamespaceContainer, Void> x = new Fabric8ioGenericExecutionAction<>(addPods,
                    EKubeActionIdentifer.REFRESH_CHILDREN, context, client, namespaceContainer);
            fetchServicesContainer.register(x);

            fetchServicesContainer.startOrphanCheck(parameters);
            for (Service service : items) {
                ServiceContainer newElement = new ServiceContainer(service.getMetadata().getUid(), service);
                if (!parameters.isHandling(newElement)) {
                    continue;
                }
                ServiceContainer serviceContainer = fetchServicesContainer.addOrReuseExisting(newElement);
                serviceContainer.setName(service.getMetadata().getName());

                updateStatus(context, client, serviceContainer, service);
            }
            fetchServicesContainer.removeOrphans();
            return null;
        }

    }

}
