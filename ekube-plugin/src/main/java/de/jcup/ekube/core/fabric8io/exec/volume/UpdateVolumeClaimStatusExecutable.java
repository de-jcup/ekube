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
 package de.jcup.ekube.core.fabric8io.exec.volume;

import java.util.List;
import java.util.Map;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.ExecutionParameters;
import de.jcup.ekube.core.fabric8io.exec.Fabric8ioSafeExecutableNoData;
import de.jcup.ekube.core.model.PersistentVolumeClaimElement;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimStatus;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.client.KubernetesClient;

class UpdateVolumeClaimStatusExecutable implements Fabric8ioSafeExecutableNoData<PersistentVolumeClaimElement> {

    @Override
    public Void execute(EKubeContext context, KubernetesClient client, PersistentVolumeClaimElement element, ExecutionParameters parameters) {

        PersistentVolumeClaim claim = parameters.get(PersistentVolumeClaim.class);
        PersistentVolumeClaimStatus status = claim.getStatus();

        StringBuilder sb = new StringBuilder();
        sb.append(status.getPhase());
        Map<String, Quantity> capacity = status.getCapacity();
        Quantity storage = capacity.get("storage");
        if (storage != null) {
            sb.append(" ").append(storage.getAmount());
        }
        List<String> modes = status.getAccessModes();
        for (String mode : modes) {
            sb.append(" ").append(mode);
        }
        element.setStatus(sb.toString());
        return null;
    }

}