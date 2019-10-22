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
 package de.jcup.ekube.core.fabric8io.exec.configmap;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.ExecutionParameters;
import de.jcup.ekube.core.fabric8io.elementaction.Fabric8ioGenericExecutionAction;
import de.jcup.ekube.core.fabric8io.exec.Fabric8ioSafeExecutableNoData;
import de.jcup.ekube.core.model.ConfigMapElement;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.client.KubernetesClient;

class UpdateConfigMapsExecutable implements Fabric8ioSafeExecutableNoData<ConfigMapElement> {

    /**
     * 
     */
    private final ConfigMapSupport configMapSupport;

    /**
     * @param configMapSupport
     */
    UpdateConfigMapsExecutable(ConfigMapSupport configMapSupport) {
        this.configMapSupport = configMapSupport;
    }

    @Override
    public Void execute(EKubeContext context, KubernetesClient client, ConfigMapElement kubeElement, ExecutionParameters parameters) {

        ConfigMap configMap = parameters.get(ConfigMap.class);

        // https://kubernetes.io/docs/concepts/services-networking/service/
        kubeElement.setData(configMap.getData());
        kubeElement.setStatus("elements:" + kubeElement.getData().size());

        /* set this itself as action for refresh */
        Fabric8ioGenericExecutionAction<ConfigMapElement, Void> x = new Fabric8ioGenericExecutionAction<>(this.configMapSupport.updateConfigMapsExecutable,
                EKubeActionIdentifer.REFRESH_STATUS, context, client, kubeElement);
        kubeElement.register(x);

        return null;
    }
}