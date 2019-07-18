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
import de.jcup.ekube.core.ExecutionParameters;
import de.jcup.ekube.core.fabric8io.exec.Fabric8ioSafeExecutable;
import de.jcup.ekube.core.model.AbstractEKubeElement;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import io.fabric8.kubernetes.client.KubernetesClient;

public class Fabric8ioGenericExecutionAction<E extends AbstractEKubeElement, R> extends AbstractFabric8ioElementAction<E, R> {

    private Fabric8ioSafeExecutable<E, R> executable;

    public Fabric8ioGenericExecutionAction(Fabric8ioSafeExecutable<E, R> executable, EKubeActionIdentifer<R> identifier, EKubeContext context,
            KubernetesClient client, E ekubeElement) {
        super(context, client, identifier, ekubeElement);
        this.executable = executable;
    }

    @Override
    public R execute(ExecutionParameters params) {
        return executable.execute(context, client, kubeElement, params);
    }

}
