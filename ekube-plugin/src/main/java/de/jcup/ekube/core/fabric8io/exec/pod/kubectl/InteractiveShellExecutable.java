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
 package de.jcup.ekube.core.fabric8io.exec.pod.kubectl;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.ExecutionParameters;
import de.jcup.ekube.core.fabric8io.exec.Fabric8ioSafeExecutable;
import de.jcup.ekube.core.model.PodContainer;
import de.jcup.ekube.core.process.ShellExecutor;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;

public class InteractiveShellExecutable implements Fabric8ioSafeExecutable<PodContainer,Void> {

    @Override
    public Void execute(EKubeContext context, KubernetesClient client, PodContainer element, ExecutionParameters parameters) {
        Object obj = element.getTechnicalObject();
        if (! (obj instanceof Pod)){
            return null;
        }
        Pod pod = (Pod) obj;
        ObjectMeta metadata = pod.getMetadata();
        String podName = metadata.getName();
        String namespaceName = metadata.getNamespace();
        String contextName = context.getConfiguration().getKubernetesContext();
        try{
            ShellExecutor.INSTANCE.interactiveRunFirstContainerInPod(podName,namespaceName,contextName);
        }catch(Exception e){
            context.getErrorHandler().logError("Was not able to fetch logs from pod:"+podName, e);
        }
        return null;
        
    }
    
}