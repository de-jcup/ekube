package de.jcup.ekube.core.fabric8io.exec;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.ExecutionParameters;
import de.jcup.ekube.core.fabric8io.condition.ConditionInfo;
import de.jcup.ekube.core.fabric8io.condition.ConditionInfoUtil;
import de.jcup.ekube.core.fabric8io.condition.ConditionInfoUtil.ConditionInfoStatus;
import de.jcup.ekube.core.fabric8io.condition.NodeConditionInfoBuilder;
import de.jcup.ekube.core.fabric8io.elementaction.Fabric8ioGenericExecutionAction;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import de.jcup.ekube.core.model.NodeConditionElement;
import de.jcup.ekube.core.model.NodeContainer;
import de.jcup.ekube.core.model.NodesContainer;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeAddress;
import io.fabric8.kubernetes.api.model.NodeCondition;
import io.fabric8.kubernetes.api.model.NodeList;
import io.fabric8.kubernetes.api.model.NodeStatus;
import io.fabric8.kubernetes.client.KubernetesClient;

public class NodesSupport extends AbstractSupport {

    public NodesSupport(Fabric8ioSupportContext context) {
        super(context);
    }
    private NodeConditionInfoBuilder nodeConditionInfoBuilder = new NodeConditionInfoBuilder();
    private AddNodesExcecutable addNodesExcecutable = new AddNodesExcecutable();
    private UpdateNodeExecutable updateStatus = new UpdateNodeExecutable();
    private AddNodeChildrenExcecutable addNodeChildrenExcecutable = new AddNodeChildrenExcecutable();

    public void addnodesFromNamespace(EKubeContext context, KubernetesClient client, NodesContainer nodesContainer) {
        context.getExecutor().execute(context, addNodesExcecutable, nodesContainer, client);
    }

    public void updateChildren(EKubeContext context, KubernetesClient client, Node node, NodeContainer nodeContainer) {
        context.getExecutor().execute(context, addNodeChildrenExcecutable, nodeContainer, client, new ExecutionParameters().set(Node.class, node));
    }

    public void updateStatus(EKubeContext context, KubernetesClient client, Node node, NodeContainer nodeContainer) {
        context.getExecutor().execute(context, updateStatus, nodeContainer, client, new ExecutionParameters().set(Node.class, node));
    }

    private class UpdateNodeExecutable implements Fabric8ioSafeExecutable<NodeContainer, Void> {

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

    private class AddNodesExcecutable implements Fabric8ioSafeExecutableNoData<NodesContainer> {

        @Override
        public Void execute(EKubeContext context, KubernetesClient client, NodesContainer nodesContainer, ExecutionParameters parameters) {
            nodesContainer.startOrphanCheck(parameters);
            NodeList nodeList = client.nodes().list();
            for (Node node : nodeList.getItems()) {
                NodeContainer newElement = new NodeContainer(node.getMetadata().getUid(), node);
                if (!parameters.isHandling(newElement)) {
                    continue;
                }
                NodeContainer nodeContainer = nodesContainer.AddOrReuseExisting(newElement);
                nodeContainer.setName(node.getMetadata().getName());
                updateStatus(context, client, node, nodeContainer);

                updateChildren(context, client, node, nodeContainer);

            }
            nodesContainer.removeOrphans();
            return null;
        }

    }

    private class AddNodeChildrenExcecutable implements Fabric8ioSafeExecutableNoData<NodeContainer> {

        @Override
        public Void execute(EKubeContext context, KubernetesClient client, NodeContainer nodeContainer, ExecutionParameters parameters) {
            Node node = parameters.get(Node.class);

            /* set this itself as action for rebuild */
            Fabric8ioGenericExecutionAction<NodeContainer, Void> x = new Fabric8ioGenericExecutionAction<>(this,
                    EKubeActionIdentifer.REFRESH_CHILDREN, context, client, nodeContainer);
            nodeContainer.setAction(x);

            /* add node conditions */
            List<NodeCondition> conditions = node.getStatus().getConditions();
            ConditionInfoStatus status = ConditionInfoUtil.createStatus();
            for (NodeCondition condition : conditions) {
                NodeConditionElement element = new NodeConditionElement(node.getMetadata().getUid() + "#" + condition.getType(), condition);
              
                ConditionInfo buildInfo = nodeConditionInfoBuilder.buildInfo(condition);
                element.setInfo(buildInfo);
                status.handle(buildInfo);
                element.setName(condition.getType());
                nodeContainer.add(element);
            }
            if (status.hasAtLeastOneFailed()){
                nodeContainer.setErrorMessage(status.getErrorMessage());
            }
            return null;
        }

    }

   
}
