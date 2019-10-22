package de.jcup.ekube.core.fabric8io.exec.pod.kubectl;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.ExecutionParameters;
import de.jcup.ekube.core.fabric8io.exec.Fabric8ioSafeExecutable;
import de.jcup.ekube.core.model.PodContainer;
import de.jcup.ekube.core.process.ShellExecutor;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;

public class InteractiveLogViewerExecutable implements Fabric8ioSafeExecutable<PodContainer,Void> {
    
    @Override
    public Void execute(EKubeContext context, KubernetesClient client, PodContainer element, ExecutionParameters parameters) {
        Object obj = element.getTechnicalObject();
        if (! (obj instanceof Pod)){
            return null;
        }
        Pod pod = (Pod) obj;
        ObjectMeta metadata = pod.getMetadata();
        String podName = metadata.getName();
        String namespace = metadata.getNamespace();
        String contextName = context.getConfiguration().getKubernetesContext();
        try{
            ShellExecutor.INSTANCE.interactiveLogViewerInPod(podName, namespace,contextName);
        }catch(Exception e){
            context.getErrorHandler().logError("Was not able to fetch logs from pod:"+podName, e);
        }
        return null;
        
    }
    
}