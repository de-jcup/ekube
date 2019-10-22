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
 package de.jcup.ekube.core.fabric8io.exec.pod;

import java.util.List;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.ExecutionParameters;
import de.jcup.ekube.core.fabric8io.exec.Fabric8ioSafeExecutable;
import de.jcup.ekube.core.model.PodContainer;
import io.fabric8.kubernetes.api.model.ContainerStatus;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodStatus;
import io.fabric8.kubernetes.client.KubernetesClient;

class UpdatePodsExecutable implements Fabric8ioSafeExecutable<PodContainer, Void> {

    @Override
    public Void execute(EKubeContext context, KubernetesClient client, PodContainer podContainer, ExecutionParameters parameters) {
        Pod pod = parameters.get(Pod.class);
        PodStatus status = pod.getStatus();
        List<ContainerStatus> containerStatuses = status.getContainerStatuses();
        int ready = 0;
        int count = 0;
        for (ContainerStatus scs : containerStatuses) {
            count++;
            if (Boolean.TRUE.equals(scs.getReady())) {
                ready++;
            }
        }
        podContainer.setStatus("Ready: " + ready + "/" + count);
        return null;
    }

}