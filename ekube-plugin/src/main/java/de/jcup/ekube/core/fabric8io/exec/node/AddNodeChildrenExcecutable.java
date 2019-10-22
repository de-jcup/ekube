package de.jcup.ekube.core.fabric8io.exec.node;

import java.util.List;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.ExecutionParameters;
import de.jcup.ekube.core.fabric8io.condition.ConditionInfo;
import de.jcup.ekube.core.fabric8io.condition.ConditionInfoUtil;
import de.jcup.ekube.core.fabric8io.condition.ConditionInfoUtil.ConditionInfoStatus;
import de.jcup.ekube.core.fabric8io.elementaction.Fabric8ioGenericExecutionAction;
import de.jcup.ekube.core.fabric8io.exec.Fabric8ioSafeExecutableNoData;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import de.jcup.ekube.core.model.NodeConditionElement;
import de.jcup.ekube.core.model.NodeContainer;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeCondition;
import io.fabric8.kubernetes.client.KubernetesClient;

class AddNodeChildrenExcecutable implements Fabric8ioSafeExecutableNoData<NodeContainer> {

    /**
     * 
     */
    private final NodesSupport nodesSupport;

    /**
     * @param nodesSupport
     */
    AddNodeChildrenExcecutable(NodesSupport nodesSupport) {
        this.nodesSupport = nodesSupport;
    }

    @Override
    public Void execute(EKubeContext context, KubernetesClient client, NodeContainer nodeContainer, ExecutionParameters parameters) {
        Node node = parameters.get(Node.class);

        /* set this itself as action for rebuild */
        Fabric8ioGenericExecutionAction<NodeContainer, Void> x = new Fabric8ioGenericExecutionAction<>(this,
                EKubeActionIdentifer.REFRESH_CHILDREN, context, client, nodeContainer);
        nodeContainer.register(x);

        /* add node conditions */
        List<NodeCondition> conditions = node.getStatus().getConditions();
        ConditionInfoStatus status = ConditionInfoUtil.createStatus();
        for (NodeCondition condition : conditions) {
            NodeConditionElement element = new NodeConditionElement(node.getMetadata().getUid() + "#" + condition.getType(), condition);
          
            ConditionInfo buildInfo = this.nodesSupport.nodeConditionInfoBuilder.buildInfo(condition);
            element.setInfo(buildInfo);
            status.handle(buildInfo);
            element.setName(condition.getType());
            nodeContainer.add(element);
        }
        status.handleErrors(nodeContainer);
        return null;
    }

}