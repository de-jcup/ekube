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
 package de.jcup.ekube.core.fabric8io.elementaction;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.ExecutionParameters;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import de.jcup.ekube.core.model.EKubeElement;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.internal.SerializationUtils;

public class Fabric8ioGeneralGetStringInfoAction extends AbstractFabric8ioElementAction<EKubeElement, String> {

    public Fabric8ioGeneralGetStringInfoAction(EKubeContext context, KubernetesClient client, EKubeElement kubeElement) {
        super(context, client, EKubeActionIdentifer.SHOW_YAML, kubeElement);
    }

    @Override
    public String execute(ExecutionParameters params) {
        try {

            Object m = kubeElement.getTechnicalObject();
            if (m instanceof HasMetadata) {
                HasMetadata metaData = (HasMetadata) m;
                String asYaml = SerializationUtils.dumpAsYaml(metaData);
                return asYaml;
            }
            return null;

        } catch (JsonProcessingException e) {
            getContext().getErrorHandler().logError("was not able to dump as yaml", e);
            return "Failed:" + e.getMessage();
        }
    }

}
