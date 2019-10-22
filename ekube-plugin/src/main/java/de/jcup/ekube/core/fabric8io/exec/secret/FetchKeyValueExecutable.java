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

import java.util.Map;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.ExecutionParameters;
import de.jcup.ekube.core.KeyValueMap;
import de.jcup.ekube.core.fabric8io.exec.Fabric8ioSafeExecutable;
import de.jcup.ekube.core.model.SecretElement;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.client.KubernetesClient;

class FetchKeyValueExecutable implements Fabric8ioSafeExecutable<SecretElement,KeyValueMap> {

    @Override
    public KeyValueMap execute(EKubeContext context, KubernetesClient client, SecretElement kubeElement, ExecutionParameters parameters) {
        // https://kubernetes.io/docs/concepts/services-networking/service/
        /* set this itself as action for refresh */
        
        KeyValueMap keyValueMap = new KeyValueMap();
        Object object = kubeElement.getTechnicalObject();
        if (object instanceof Secret){
            Secret secret = (Secret) object;
            Map<String, String> data = secret.getData();
            if (data!=null){
                keyValueMap.putAll(data);
            }
        }
        return keyValueMap;
    }
}