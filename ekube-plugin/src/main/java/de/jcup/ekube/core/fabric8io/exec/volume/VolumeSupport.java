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
import de.jcup.ekube.core.fabric8io.exec.AbstractSupport;
import de.jcup.ekube.core.fabric8io.exec.Fabric8ioSupportContext;
import de.jcup.ekube.core.model.CurrentContextContainer;
import de.jcup.ekube.core.model.NamespaceContainer;
import de.jcup.ekube.core.model.PersistentVolumeClaimElement;
import de.jcup.ekube.core.model.PersistentVolumeElement;
import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.client.KubernetesClient;

public class VolumeSupport extends AbstractSupport {

    public VolumeSupport(Fabric8ioSupportContext context) {
        super(context);
    }

    AddVolumeClaimsFromNamespaceExecutable addVolumesClaims = new AddVolumeClaimsFromNamespaceExecutable(this);
    AddVolumesFromNamespaceExecutable addVolumes = new AddVolumesFromNamespaceExecutable(this);
    private UpdateVolumeClaimStatusExecutable updateVolumeClaimStatus = new UpdateVolumeClaimStatusExecutable();
    private UpdateVolumeStatusExecutable updateVolumeStatus = new UpdateVolumeStatusExecutable();

    public void addVolumeClaimsFromNamespace(NamespaceContainer namespaceContainer) {
        this.addVolumeClaimsFromNamespace(getContext(), getClient(), namespaceContainer);
    }
    public void addVolumes(CurrentContextContainer container) {
        this.addVolumes(getContext(), getClient(), container);
    }

    public void addVolumeClaimsFromNamespace(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer) {
        context.getExecutor().execute(context, addVolumesClaims, namespaceContainer, client);
    }
    
    public void addVolumes(EKubeContext context, KubernetesClient client, CurrentContextContainer container) {
        context.getExecutor().execute(context, addVolumes, container, client);
    }

    public void updateStatus(EKubeContext context, KubernetesClient client, PersistentVolumeClaim claim, PersistentVolumeClaimElement claimElement) {
        context.getExecutor().execute(context, updateVolumeClaimStatus, claimElement, client,
                new ExecutionParameters().set(PersistentVolumeClaim.class, claim));
    }
    
    public void updateStatus(EKubeContext context, KubernetesClient client, PersistentVolume volume, PersistentVolumeElement claimElement) {
        context.getExecutor().execute(context, updateVolumeStatus, claimElement, client,
                new ExecutionParameters().set(PersistentVolume.class, volume));
    }
}
