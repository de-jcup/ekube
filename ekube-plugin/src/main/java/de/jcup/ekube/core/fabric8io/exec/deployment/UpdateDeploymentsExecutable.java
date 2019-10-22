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
 package de.jcup.ekube.core.fabric8io.exec.deployment;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.ExecutionParameters;
import de.jcup.ekube.core.fabric8io.exec.Fabric8ioSafeExecutable;
import de.jcup.ekube.core.model.DeploymentContainer;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentStatus;
import io.fabric8.kubernetes.client.KubernetesClient;

class UpdateDeploymentsExecutable implements Fabric8ioSafeExecutable<DeploymentContainer, Void> {
    @Override
    public Void execute(EKubeContext context, KubernetesClient client, DeploymentContainer nodeContainer, ExecutionParameters parameters) {
        StringBuilder sb = new StringBuilder();
        DeploymentStatus status = parameters.get(Deployment.class).getStatus();
        sb.append("Replicas:" + status.getReplicas());
        if (status.getAvailableReplicas() != null) {
            sb.append(",avail:" + status.getAvailableReplicas());
        }
        if (status.getUnavailableReplicas() != null) {
            sb.append(",unavail:" + status.getUnavailableReplicas());
        }
        nodeContainer.setStatus(sb.toString());
        return null;
    }

}