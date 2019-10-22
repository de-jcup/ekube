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
 package de.jcup.ekube.core.fabric8io.exec.service;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.ExecutionParameters;
import de.jcup.ekube.core.fabric8io.exec.AbstractSupport;
import de.jcup.ekube.core.fabric8io.exec.Fabric8ioSupportContext;
import de.jcup.ekube.core.model.NamespaceContainer;
import de.jcup.ekube.core.model.ServiceContainer;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.client.KubernetesClient;

public class ServiceSupport extends AbstractSupport {

    public ServiceSupport(Fabric8ioSupportContext context) {
        super(context);
    }

    AddServicesFromNamespaceExecutable addPods = new AddServicesFromNamespaceExecutable(this);
    private UpdatePodsExecutable updateService = new UpdatePodsExecutable();

    public void addServicesFromNamespace(NamespaceContainer namespaceContainer) {
        this.addServicesFromNamespace(getContext(), getClient(), namespaceContainer);
    }

    public void addServicesFromNamespace(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer) {
        context.getExecutor().execute(context, addPods, namespaceContainer, client);
    }

    public void updateStatus(EKubeContext context, KubernetesClient client, ServiceContainer podContainer, Service service) {
        context.getExecutor().execute(context, updateService, podContainer, client, new ExecutionParameters().set(Service.class, service));
    }

}
