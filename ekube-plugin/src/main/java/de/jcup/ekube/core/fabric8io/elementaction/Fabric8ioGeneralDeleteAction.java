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
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import de.jcup.ekube.core.model.EKubeElement;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.client.KubernetesClient;

public class Fabric8ioGeneralDeleteAction extends AbstractFabric8ioElementAction<EKubeElement, String> {

    public Fabric8ioGeneralDeleteAction(EKubeContext context, KubernetesClient client, EKubeElement kubeElement) {
        super(context, client, EKubeActionIdentifer.DELETE, kubeElement);
    }

    @Override
    public String execute(ExecutionParameters params) {

        Object o = kubeElement.getTechnicalObject();
        if (o instanceof Pod) {
            Pod pod = (Pod) o;
            String podName = pod.getMetadata().getName();
            Boolean deleted = client.pods().delete(pod);
            if (deleted) {
                return "Deleted POD:" + podName + ", can take some time to have effect. Please refresh the parent by yourself after a while...";
            }else {
                return "Was not able to delete POD:"+podName;
            }
        } else if (o instanceof Secret) {
            Secret secret = (Secret) o;
            String secretName = secret.getMetadata().getName();
            Boolean deleted = client.secrets().delete(secret);
            return "Deleted Secret:" + secretName + ":" + deleted;
        }
        return "Was not able to delete technical object:" + o;

    }

    public static boolean canDelete(Object technicalObject) {
        boolean canDelete = false;
        canDelete = canDelete || technicalObject instanceof Pod;
        canDelete = canDelete || technicalObject instanceof Secret;
        return canDelete;
    }

}
