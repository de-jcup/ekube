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