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

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.ExecutionParameters;
import de.jcup.ekube.core.fabric8io.elementaction.Fabric8ioGenericExecutionAction;
import de.jcup.ekube.core.fabric8io.exec.Fabric8ioSafeExecutableNoData;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import de.jcup.ekube.core.model.NamespaceContainer;
import de.jcup.ekube.core.model.PodContainer;
import de.jcup.ekube.core.model.PodsContainer;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.KubernetesClient;

class AddPodsFromNamespaceExecutable implements Fabric8ioSafeExecutableNoData<NamespaceContainer> {

    /**
     * 
     */
    private final PodSupport podSupport;

    /**
     * @param podSupport
     */
    AddPodsFromNamespaceExecutable(PodSupport podSupport) {
        this.podSupport = podSupport;
    }

    @Override
    public Void execute(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer, ExecutionParameters parameters) {
        String namespaceName = namespaceContainer.getName();
        PodList podList = client.pods().inNamespace(namespaceName).list();
        PodsContainer podsContainer = namespaceContainer.fetchPodsContainer();

        /* set this itself as action for rebuild */
        Fabric8ioGenericExecutionAction<NamespaceContainer, Void> x = new Fabric8ioGenericExecutionAction<>(this,
                EKubeActionIdentifer.REFRESH_CHILDREN, context, client, namespaceContainer);
        podsContainer.register(x);

        podsContainer.startOrphanCheck(parameters);
        for (Pod pod : podList.getItems()) {
            PodContainer newElement = new PodContainer(pod.getMetadata().getUid(), pod);
            if (!parameters.isHandling(newElement)) {
                continue;
            }
            PodContainer podContainer = podsContainer.addOrReuseExisting(newElement);
            this.podSupport.updateStatus(context, client, podContainer, new ExecutionParameters().set(Pod.class, pod));

            podContainer.setName(pod.getMetadata().getName());
            this.podSupport.addPodChildren(context, client, podContainer, pod);
        }
        podsContainer.removeOrphans();
        return null;
    }

}