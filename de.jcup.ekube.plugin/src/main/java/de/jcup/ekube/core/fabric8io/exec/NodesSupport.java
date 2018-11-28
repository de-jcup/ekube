package de.jcup.ekube.core.fabric8io.exec;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.model.NodeConditionElement;
import de.jcup.ekube.core.model.NodeContainer;
import de.jcup.ekube.core.model.NodesContainer;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeAddress;
import io.fabric8.kubernetes.api.model.NodeCondition;
import io.fabric8.kubernetes.api.model.NodeList;
import io.fabric8.kubernetes.api.model.NodeStatus;
import io.fabric8.kubernetes.client.KubernetesClient;

public class NodesSupport {
	
	private AddNodesExcecutable addNodesExcecutable = new AddNodesExcecutable();
	private UpdateNodeExecutable updateStatus = new UpdateNodeExecutable();

	public void addnodesFromNamespace(EKubeContext context, KubernetesClient client, NodesContainer nodesContainer) {
		context.getExecutor().execute(context, addNodesExcecutable, nodesContainer, client);
	}
	
	public void updateStatus(EKubeContext context, KubernetesClient client, Node node, NodeContainer nodeContainer){
		context.getExecutor().execute(context, updateStatus, nodeContainer,client,node);
	}
	
	private class UpdateNodeExecutable implements Fabric8ioSafeExecutable<NodeContainer, Node>{

		@Override
		public void execute(EKubeContext context, KubernetesClient client, NodeContainer nodeContainer, Node node) {
			StringBuilder sb = new StringBuilder();
			NodeStatus status = node.getStatus();
			List<NodeAddress> adresses = status.getAddresses();
			for (NodeAddress adress: adresses){
				String addressString = adress.getAddress();
				if (StringUtils.equals(nodeContainer.getName(), addressString)){
					continue;
				}
				sb.append(addressString);
				sb.append(" ");
			}
			String phase = status.getPhase();
			if (phase!=null){
				sb.append(phase);
			}
			nodeContainer.setStatus(sb.toString());
		}
		
	}
	
	private class AddNodesExcecutable implements Fabric8ioSafeExecutableNoData<NodesContainer>{

		@Override
		public void execute(EKubeContext context, KubernetesClient client, NodesContainer nodesContainer, Void ignore) {
			NodeList nodeList = client.nodes().list();
			for (Node node: nodeList.getItems()){
				NodeContainer nodeContainer = new NodeContainer();
				nodeContainer.setName(node.getMetadata().getName());
				nodesContainer.add(nodeContainer);

				updateStatus(context,client,node,nodeContainer);

				List<NodeCondition> conditions = node.getStatus().getConditions();
				for (NodeCondition condition: conditions){
					NodeConditionElement element = new NodeConditionElement();
					element.setName(condition.getType());
					
					StringBuilder sb = new StringBuilder();
					sb.append(condition.getStatus());
					sb.append(" ");
					sb.append(condition.getMessage());
					sb.append(" ");
					sb.append(condition.getLastHeartbeatTime());
					element.setStatus(sb.toString());
					
					nodeContainer.add(element);
				}
				
				
			}
		}
		
	}
}
