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

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.fabric8io.exec.Fabric8ioSupports;
import de.jcup.ekube.core.model.AbstractEKubeElementAction;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import de.jcup.ekube.core.model.EKubeElement;
import io.fabric8.kubernetes.client.KubernetesClient;

public abstract class AbstractFabric8ioElementAction<E extends EKubeElement, R> extends AbstractEKubeElementAction<E, R> {

    protected KubernetesClient client;
    protected Fabric8ioSupports support;

    public AbstractFabric8ioElementAction(EKubeContext context, KubernetesClient client, EKubeActionIdentifer<R> actionIdentifier, E ekubeElement) {
        super(context, actionIdentifier, ekubeElement);
        this.client = client;
        this.support = new Fabric8ioSupports(context, client);
    }

}
