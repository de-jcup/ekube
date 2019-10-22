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

import java.util.TreeMap;

import javax.crypto.SealedObject;

import de.jcup.ekube.core.CryptoAccess;
import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.ExecutionParameters;
import de.jcup.ekube.core.KeyValueMap;
import de.jcup.ekube.core.fabric8io.exec.AbstractSupport;
import de.jcup.ekube.core.fabric8io.exec.Fabric8ioSupportContext;
import de.jcup.ekube.core.model.NamespaceContainer;
import de.jcup.ekube.core.model.SecretElement;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.client.KubernetesClient;

public class SecretsSupport extends AbstractSupport {

    CryptoAccess<TreeMap<String,KeyValueMap>> copiedSecretsAccess;
    
    public SecretsSupport(Fabric8ioSupportContext context) {
        super(context);
        this.copiedSecretsAccess = new CryptoAccess<>();
    }

    AddSecretsFromNamespaceExecutable addSecretsFromNamespaceExecutable = new AddSecretsFromNamespaceExecutable(this);
    private UpdateSecretsExecutable updateSecretsExecutable = new UpdateSecretsExecutable();
    FetchKeyValueExecutable fetchKeyValueExecutable = new FetchKeyValueExecutable();
    
    SealedObject encryptedClipboardValue;
    
    public void addSecretsFromNamespace(NamespaceContainer namespaceContainer) {
        this.addSecretsFromNamespace(getContext(), getClient(), namespaceContainer);
    }

    public void addSecretsFromNamespace(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer) {
        context.getExecutor().execute(context, addSecretsFromNamespaceExecutable, namespaceContainer, client);
    }

    public void updateStatus(EKubeContext context, KubernetesClient client, Secret technicalObject, SecretElement kubeElement) {
        context.getExecutor().execute(context, updateSecretsExecutable, kubeElement, client,
                new ExecutionParameters().set(Secret.class, technicalObject));
    }

}
