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
 package de.jcup.ekube.core.fabric8io.exec.secret;

import java.util.List;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.ExecutionParameters;
import de.jcup.ekube.core.KeyValueMap;
import de.jcup.ekube.core.fabric8io.elementaction.Fabric8ioGenericExecutionAction;
import de.jcup.ekube.core.fabric8io.exec.Fabric8ioSafeExecutableNoData;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import de.jcup.ekube.core.model.NamespaceContainer;
import de.jcup.ekube.core.model.SecretElement;
import de.jcup.ekube.core.model.SecretsContainer;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretList;
import io.fabric8.kubernetes.client.KubernetesClient;

class AddSecretsFromNamespaceExecutable implements Fabric8ioSafeExecutableNoData<NamespaceContainer> {

    /**
     * 
     */
    private final SecretsSupport secretsSupport;

    /**
     * @param secretsSupport
     */
    AddSecretsFromNamespaceExecutable(SecretsSupport secretsSupport) {
        this.secretsSupport = secretsSupport;
    }

    @Override
    public Void execute(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer, ExecutionParameters parameters) {
        String namespaceName = namespaceContainer.getName();
        SecretList serviceList = client.secrets().inNamespace(namespaceName).list();
        List<Secret> items = serviceList.getItems();
        SecretsContainer fetchSecretsContainer = namespaceContainer.fetchSecretsContainer();

        /* set this itself as action for rebuild */
        Fabric8ioGenericExecutionAction<NamespaceContainer, Void> x = new Fabric8ioGenericExecutionAction<>(this.secretsSupport.addSecretsFromNamespaceExecutable,
                EKubeActionIdentifer.REFRESH_CHILDREN, context, client, namespaceContainer);
        fetchSecretsContainer.register(x);

        fetchSecretsContainer.startOrphanCheck(parameters);
        for (Secret secret : items) {
            SecretElement newElement = new SecretElement(secret.getMetadata().getUid(), secret);
            if (!parameters.isHandling(newElement)) {
                continue;
            }
            SecretElement secretElement = fetchSecretsContainer.addOrReuseExisting(newElement);
            secretElement.setName(secret.getMetadata().getName());

            this.secretsSupport.updateStatus(context, client, secret, secretElement);
            Fabric8ioGenericExecutionAction<SecretElement, KeyValueMap> fetchKeyValueAction = new Fabric8ioGenericExecutionAction<>(this.secretsSupport.fetchKeyValueExecutable,
                    EKubeActionIdentifer.FETCH_KEY_VALUE, context, client, newElement);
            newElement.register(fetchKeyValueAction);

        }
        fetchSecretsContainer.removeOrphans();
        return null;
    }
}