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
import de.jcup.ekube.core.fabric8io.condition.PodConditionInfoBuilder;
import de.jcup.ekube.core.fabric8io.exec.AbstractSupport;
import de.jcup.ekube.core.fabric8io.exec.Fabric8ioSupportContext;
import de.jcup.ekube.core.fabric8io.exec.pod.kubectl.InteractiveLogViewerExecutable;
import de.jcup.ekube.core.fabric8io.exec.pod.kubectl.InteractiveShellExecutable;
import de.jcup.ekube.core.model.NamespaceContainer;
import de.jcup.ekube.core.model.PodContainer;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;

public class PodSupport extends AbstractSupport {

    public PodSupport(Fabric8ioSupportContext context) {
        super(context);
    }

    private AddPodsFromNamespaceExecutable addPodsFromNamespaceExecutable = new AddPodsFromNamespaceExecutable(this);
    private AddPodActionsAndChildrenExecutable addPodActionsAndChildrenExecutable = new AddPodActionsAndChildrenExecutable(this);
    FetchPodLogsExecutable fetchLogsExecutable = new FetchPodLogsExecutable();
    InteractiveShellExecutable interactiveShellExecutable = new InteractiveShellExecutable();
    InteractiveLogViewerExecutable interactiveLogViewerExecutable = new InteractiveLogViewerExecutable();

    PodConditionInfoBuilder conditionInfoBuilder = new PodConditionInfoBuilder();
    private UpdatePodsExecutable updateStatus = new UpdatePodsExecutable();

    public void addPodsFromNamespace(NamespaceContainer namespaceContainer) {
        this.addPodsFromNamespace(getContext(), getClient(), namespaceContainer);
    }

    public void addPodsFromNamespace(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer) {
        context.getExecutor().execute(context, addPodsFromNamespaceExecutable, namespaceContainer, client);
    }

    public void addPodChildren(EKubeContext context, KubernetesClient client, PodContainer namespaceContainer, Pod pod) {
        context.getExecutor().execute(context, addPodActionsAndChildrenExecutable, namespaceContainer, client, new ExecutionParameters().set(Pod.class, pod));
    }

    public void updateStatus(EKubeContext context, KubernetesClient client, PodContainer podContainer, ExecutionParameters parameters) {
        context.getExecutor().execute(context, updateStatus, podContainer, client, parameters);
    }

}
