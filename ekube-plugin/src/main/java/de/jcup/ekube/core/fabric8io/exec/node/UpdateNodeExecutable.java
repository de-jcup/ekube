package de.jcup.ekube.core.fabric8io.exec.node;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.ExecutionParameters;
import de.jcup.ekube.core.fabric8io.exec.Fabric8ioSafeExecutable;
import de.jcup.ekube.core.model.NodeContainer;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeAddress;
import io.fabric8.kubernetes.api.model.NodeStatus;
import io.fabric8.kubernetes.client.KubernetesClient;

class UpdateNodeExecutable implements Fabric8ioSafeExecutable<NodeContainer, Void> {

    @Override
    public Void execute(EKubeContext context, KubernetesClient client, NodeContainer nodeContainer, ExecutionParameters parameters) {
        Node node = parameters.get(Node.class);
        StringBuilder sb = new StringBuilder();
        NodeStatus status = node.getStatus();
        List<NodeAddress> adresses = status.getAddresses();
        for (NodeAddress adress : adresses) {
            String addressString = adress.getAddress();
            if (StringUtils.equals(nodeContainer.getName(), addressString)) {
                continue;
            }
            sb.append(addressString);
            sb.append(" ");
        }
        String phase = status.getPhase();
        if (phase != null) {
            sb.append(phase);
        }
        nodeContainer.setStatus(sb.toString());
        return null;
    }

}