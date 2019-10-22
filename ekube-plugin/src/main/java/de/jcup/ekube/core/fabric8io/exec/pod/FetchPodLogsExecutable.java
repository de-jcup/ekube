package de.jcup.ekube.core.fabric8io.exec.pod;

import java.io.File;
import java.io.FileOutputStream;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.ExecutionParameters;
import de.jcup.ekube.core.fabric8io.exec.Fabric8ioSafeExecutable;
import de.jcup.ekube.core.model.PodContainer;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;

class FetchPodLogsExecutable implements Fabric8ioSafeExecutable<PodContainer,File> {

    @Override
    public File execute(EKubeContext context, KubernetesClient client, PodContainer element, ExecutionParameters parameters) {
        Object obj = element.getTechnicalObject();
        if (! (obj instanceof Pod)){
            return null;
        }
        Pod pod = (Pod) obj;
        Integer logSize= parameters.get(Integer.class);
        if (logSize==null){
            logSize=100;
        }
        String podName = pod.getMetadata().getName();
        try{
            File file = File.createTempFile("ekube_log_"+podName+"_", ".log");
            file.deleteOnExit();
            
            FileOutputStream fos = new FileOutputStream(file);
            client.pods().inNamespace(pod.getMetadata().getNamespace()).withName(podName).tailingLines(logSize).watchLog(fos);
            return file;
        }catch(Exception e){
            context.getErrorHandler().logError("Was not able to fetch logs from pod:"+podName, e);
            return null;
        }
        
    }
    
}