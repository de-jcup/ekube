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