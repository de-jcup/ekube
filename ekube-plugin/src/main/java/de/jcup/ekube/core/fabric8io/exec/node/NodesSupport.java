/*
 * Copyright 2019 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
 package de.jcup.ekube.core.fabric8io.exec.node;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.ExecutionParameters;
import de.jcup.ekube.core.fabric8io.condition.NodeConditionInfoBuilder;
import de.jcup.ekube.core.fabric8io.exec.AbstractSupport;
import de.jcup.ekube.core.fabric8io.exec.Fabric8ioSupportContext;
import de.jcup.ekube.core.model.NodeContainer;
import de.jcup.ekube.core.model.NodesContainer;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.client.KubernetesClient;

public class NodesSupport extends AbstractSupport {

    public NodesSupport(Fabric8ioSupportContext context) {
        super(context);
    }
    NodeConditionInfoBuilder nodeConditionInfoBuilder = new NodeConditionInfoBuilder();
    private AddNodesExcecutable addNodesExcecutable = new AddNodesExcecutable(this);
    private UpdateNodeExecutable updateStatus = new UpdateNodeExecutable();
    private AddNodeChildrenExcecutable addNodeChildrenExcecutable = new AddNodeChildrenExcecutable(this);

    public void addnodesFromNamespace(EKubeContext context, KubernetesClient client, NodesContainer nodesContainer) {
        context.getExecutor().execute(context, addNodesExcecutable, nodesContainer, client);
    }

    public void updateChildren(EKubeContext context, KubernetesClient client, Node node, NodeContainer nodeContainer) {
        context.getExecutor().execute(context, addNodeChildrenExcecutable, nodeContainer, client, new ExecutionParameters().set(Node.class, node));
    }

    public void updateStatus(EKubeContext context, KubernetesClient client, Node node, NodeContainer nodeContainer) {
        context.getExecutor().execute(context, updateStatus, nodeContainer, client, new ExecutionParameters().set(Node.class, node));
    }

   
}
