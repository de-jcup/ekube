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