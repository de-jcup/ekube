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
import java.util.Map;
import java.util.TreeMap;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.ExecutionParameters;
import de.jcup.ekube.core.KeyValueMap;
import de.jcup.ekube.core.fabric8io.exec.Fabric8ioSafeExecutable;
import de.jcup.ekube.core.model.EKubeElement;
import de.jcup.ekube.core.model.SecretsContainer;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.client.KubernetesClient;

@Deprecated // currently under development, will change, not added to ui
class CopySecretsToInternalClipboardExecutable implements Fabric8ioSafeExecutable<SecretsContainer,Void> {

    /**
     * 
     */
    private final SecretsSupport secretsSupport;

    /**
     * @param secretsSupport
     */
    CopySecretsToInternalClipboardExecutable(SecretsSupport secretsSupport) {
        this.secretsSupport = secretsSupport;
    }

    @Override
    public Void execute(EKubeContext context, KubernetesClient client, SecretsContainer kubeElement, ExecutionParameters parameters) {
        TreeMap<String,KeyValueMap> map = new TreeMap<>();
        // https://kubernetes.io/docs/concepts/services-networking/service/
        /* set this itself as action for refresh */
        List<EKubeElement> secretElements = kubeElement.getChildren();
        for (EKubeElement secretElement: secretElements) {
            KeyValueMap keyValueMap = new KeyValueMap();
            Object object = kubeElement.getTechnicalObject();
            if (object instanceof Secret){
                Secret secret = (Secret) object;
                String secretName = secret.getMetadata().getName();
                Map<String, String> data = secret.getData();
                if (data!=null){
                    keyValueMap.putAll(data);
                }
                map.put(secretName,keyValueMap);
            }
            
        }
        this.secretsSupport.encryptedClipboardValue=this.secretsSupport.copiedSecretsAccess.seal(map);
        return null;
    }
}