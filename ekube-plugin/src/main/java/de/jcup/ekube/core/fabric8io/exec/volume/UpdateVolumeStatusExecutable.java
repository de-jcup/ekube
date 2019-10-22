package de.jcup.ekube.core.fabric8io.exec.volume;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.ExecutionParameters;
import de.jcup.ekube.core.fabric8io.exec.Fabric8ioSafeExecutableNoData;
import de.jcup.ekube.core.model.PersistentVolumeElement;
import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.api.model.PersistentVolumeStatus;
import io.fabric8.kubernetes.client.KubernetesClient;

class UpdateVolumeStatusExecutable implements Fabric8ioSafeExecutableNoData<PersistentVolumeElement> {

    @Override
    public Void execute(EKubeContext context, KubernetesClient client, PersistentVolumeElement element, ExecutionParameters parameters) {

        PersistentVolume claim = parameters.get(PersistentVolume.class);
        PersistentVolumeStatus status = claim.getStatus();

        StringBuilder sb = new StringBuilder();
        sb.append(status.getPhase());
        sb.append(" ").append(status.getMessage());
        element.setStatus(sb.toString());
        return null;
    }

}