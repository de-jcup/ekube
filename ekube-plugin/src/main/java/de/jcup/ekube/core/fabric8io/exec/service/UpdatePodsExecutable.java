package de.jcup.ekube.core.fabric8io.exec.service;

import java.util.List;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.ExecutionParameters;
import de.jcup.ekube.core.fabric8io.exec.Fabric8ioSafeExecutable;
import de.jcup.ekube.core.model.ServiceContainer;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.ServiceSpec;
import io.fabric8.kubernetes.client.KubernetesClient;

class UpdatePodsExecutable implements Fabric8ioSafeExecutable<ServiceContainer, Void> {

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