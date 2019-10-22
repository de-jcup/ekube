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