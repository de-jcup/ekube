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

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.ExecutionParameters;
import de.jcup.ekube.core.fabric8io.elementaction.Fabric8ioGenericExecutionAction;
import de.jcup.ekube.core.fabric8io.exec.Fabric8ioSafeExecutableNoData;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import de.jcup.ekube.core.model.NamespaceContainer;
import de.jcup.ekube.core.model.PersistentVolumeClaimElement;
import de.jcup.ekube.core.model.PersistentVolumeClaimesContainer;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimList;
import io.fabric8.kubernetes.client.KubernetesClient;

class AddVolumeClaimsFromNamespaceExecutable implements Fabric8ioSafeExecutableNoData<NamespaceContainer> {

    /**
     * 
     */
    private final VolumeSupport volumeSupport;

    /**
     * @param volumeSupport
     */
    AddVolumeClaimsFromNamespaceExecutable(VolumeSupport volumeSupport) {
        this.volumeSupport = volumeSupport;
    }

    @Override
    public Void execute(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer, ExecutionParameters parameters) {
        PersistentVolumeClaimList list = client.persistentVolumeClaims().inNamespace(namespaceContainer.getName()).list();
        List<PersistentVolumeClaim> items = list.getItems();
        PersistentVolumeClaimesContainer fetchPersistentVolumeClaimsContainer = namespaceContainer.fetchPersistentVolumeClaimsContainer();

        /* set this itself as action for rebuild */
        Fabric8ioGenericExecutionAction<NamespaceContainer, Void> x = new Fabric8ioGenericExecutionAction<>(this.volumeSupport.addVolumesClaims,
                EKubeActionIdentifer.REFRESH_CHILDREN, context, client, namespaceContainer);
        fetchPersistentVolumeClaimsContainer.register(x);

        fetchPersistentVolumeClaimsContainer.startOrphanCheck(parameters);
        
        for (PersistentVolumeClaim volumeClaim : items) {
            PersistentVolumeClaimElement newElement = new PersistentVolumeClaimElement(volumeClaim.getMetadata().getUid(), volumeClaim);
            if (! parameters.isHandling(newElement)){
                continue;
            }
            PersistentVolumeClaimElement pvcElement = fetchPersistentVolumeClaimsContainer.addOrReuseExisting(newElement);
            pvcElement.setName(volumeClaim.getMetadata().getName());
            this.volumeSupport.updateStatus(context, client, volumeClaim, pvcElement);
        }
        fetchPersistentVolumeClaimsContainer.removeOrphans();
        return null;
    }

}