package de.jcup.ekube.core.fabric8io.exec.node;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.ExecutionParameters;
import de.jcup.ekube.core.fabric8io.exec.Fabric8ioSafeExecutableNoData;
import de.jcup.ekube.core.model.NodeContainer;
import de.jcup.ekube.core.model.NodesContainer;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeList;
import io.fabric8.kubernetes.client.KubernetesClient;

class AddNodesExcecutable implements Fabric8ioSafeExecutableNoData<NodesContainer> {

    /**
     * 
     */
    private final NodesSupport nodesSupport;

    /**
     * @param nodesSupport
     */
    AddNodesExcecutable(NodesSupport nodesSupport) {
        this.nodesSupport = nodesSupport;
    }

    @Override
    public Void execute(EKubeContext context, KubernetesClient client, NodesContainer nodesContainer, ExecutionParameters parameters) {
        nodesContainer.startOrphanCheck(parameters);
        NodeList nodeList = client.nodes().list();
        for (Node node : nodeList.getItems()) {
            NodeContainer newElement = new NodeContainer(node.getMetadata().getUid(), node);
            if (!parameters.isHandling(newElement)) {
                continue;
            }
            NodeContainer nodeContainer = nodesContainer.addOrReuseExisting(newElement);
            nodeContainer.setName(node.getMetadata().getName());
            this.nodesSupport.updateStatus(context, client, node, nodeContainer);

            this.nodesSupport.updateChildren(context, client, node, nodeContainer);

        }
        nodesContainer.removeOrphans();
        return null;
    }

}