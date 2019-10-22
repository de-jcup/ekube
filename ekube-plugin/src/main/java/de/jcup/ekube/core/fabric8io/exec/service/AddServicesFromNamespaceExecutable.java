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

import java.util.List;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.ExecutionParameters;
import de.jcup.ekube.core.fabric8io.elementaction.Fabric8ioGenericExecutionAction;
import de.jcup.ekube.core.fabric8io.exec.Fabric8ioSafeExecutableNoData;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import de.jcup.ekube.core.model.NamespaceContainer;
import de.jcup.ekube.core.model.ServiceContainer;
import de.jcup.ekube.core.model.ServicesContainer;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.fabric8.kubernetes.client.KubernetesClient;

class AddServicesFromNamespaceExecutable implements Fabric8ioSafeExecutableNoData<NamespaceContainer> {

    /**
     * 
     */
    private final ServiceSupport serviceSupport;

    /**
     * @param serviceSupport
     */
    AddServicesFromNamespaceExecutable(ServiceSupport serviceSupport) {
        this.serviceSupport = serviceSupport;
    }

    @Override
    public Void execute(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer, ExecutionParameters parameters) {
        String namespaceName = namespaceContainer.getName();
        ServiceList serviceList = client.services().inNamespace(namespaceName).list();
        List<Service> items = serviceList.getItems();
        ServicesContainer fetchServicesContainer = namespaceContainer.fetchServicesContainer();

        /* set this itself as action for rebuild */
        Fabric8ioGenericExecutionAction<NamespaceContainer, Void> x = new Fabric8ioGenericExecutionAction<>(this.serviceSupport.addPods,
                EKubeActionIdentifer.REFRESH_CHILDREN, context, client, namespaceContainer);
        fetchServicesContainer.register(x);

        fetchServicesContainer.startOrphanCheck(parameters);
        for (Service service : items) {
            ServiceContainer newElement = new ServiceContainer(service.getMetadata().getUid(), service);
            if (!parameters.isHandling(newElement)) {
                continue;
            }
            ServiceContainer serviceContainer = fetchServicesContainer.addOrReuseExisting(newElement);
            serviceContainer.setName(service.getMetadata().getName());

            this.serviceSupport.updateStatus(context, client, serviceContainer, service);
        }
        fetchServicesContainer.removeOrphans();
        return null;
    }

}