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

import java.util.Collections;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.ExecutionParameters;
import de.jcup.ekube.core.model.AbstractEKubeElement;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import de.jcup.ekube.core.model.EKubeContainer;
import de.jcup.ekube.core.model.EKubeElement;
import io.fabric8.kubernetes.client.KubernetesClient;

public class Fabric8ioGeneralRefreshAction extends AbstractFabric8ioElementAction<EKubeElement, Void> {

    public Fabric8ioGeneralRefreshAction(EKubeContext context, KubernetesClient client, EKubeElement kubeElement) {
        super(context, client, EKubeActionIdentifer.REFRESH, kubeElement);
    }

    @Override
    public Void execute(ExecutionParameters params) {
        /*
         * refresh yourself by using parent doing a "refresh child" only on
         * this!
         */
        EKubeContainer parent = kubeElement.getParent();
        if(kubeElement instanceof AbstractEKubeElement){
            AbstractEKubeElement abstractElement = (AbstractEKubeElement) kubeElement;
            abstractElement.setErrorMessage(null);
        }
        if (parent==null){
            return null;
        }
        parent.execute(EKubeActionIdentifer.REFRESH_CHILDREN, new ExecutionParameters().setChildren(Collections.singleton(kubeElement)));
        return null;
    }

}
