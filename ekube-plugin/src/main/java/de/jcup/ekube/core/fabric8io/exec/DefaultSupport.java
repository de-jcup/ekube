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
 package de.jcup.ekube.core.fabric8io.exec;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.fabric8io.elementaction.Fabric8ioGeneralDeleteAction;
import de.jcup.ekube.core.fabric8io.elementaction.Fabric8ioGeneralGetStringInfoAction;
import de.jcup.ekube.core.fabric8io.elementaction.Fabric8ioGeneralRefreshAction;
import de.jcup.ekube.core.model.AbstractEKubeElement;
import de.jcup.ekube.core.model.EKubeElement;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.KubernetesClient;

public class DefaultSupport extends AbstractSupport {

    public DefaultSupport(Fabric8ioSupportContext context) {
        super(context);
    }

    public void appendDefaults(EKubeContext context, KubernetesClient client, EKubeElement element) {
        if (!(element instanceof AbstractEKubeElement)) {
            return;
        }
        AbstractEKubeElement abstractElement = (AbstractEKubeElement) element;
        Object technicalObject = element.getTechnicalObject();
        if (technicalObject instanceof HasMetadata) {
            HasMetadata hasMetadata = (HasMetadata) technicalObject;
            abstractElement.register(new Fabric8ioGeneralGetStringInfoAction(context, client, element));
            abstractElement.setTechnicalObject(hasMetadata);
        }
        if (Fabric8ioGeneralDeleteAction.canDelete(technicalObject)) {
            abstractElement.register(new Fabric8ioGeneralDeleteAction(context, client, element));
        }

        abstractElement.register(new Fabric8ioGeneralRefreshAction(context, client, element));
    }

}
