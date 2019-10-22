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