package de.jcup.ekube.core.fabric8io.exec.secret;

import java.util.Map;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.ExecutionParameters;
import de.jcup.ekube.core.KeyValueMap;
import de.jcup.ekube.core.fabric8io.exec.Fabric8ioSafeExecutable;
import de.jcup.ekube.core.model.SecretElement;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.client.KubernetesClient;

class FetchKeyValueExecutable implements Fabric8ioSafeExecutable<SecretElement,KeyValueMap> {

    @Override
    public KeyValueMap execute(EKubeContext context, KubernetesClient client, SecretElement kubeElement, ExecutionParameters parameters) {
        // https://kubernetes.io/docs/concepts/services-networking/service/
        /* set this itself as action for refresh */
        
        KeyValueMap keyValueMap = new KeyValueMap();
        Object object = kubeElement.getTechnicalObject();
        if (object instanceof Secret){
            Secret secret = (Secret) object;
            Map<String, String> data = secret.getData();
            if (data!=null){
                keyValueMap.putAll(data);
            }
        }
        return keyValueMap;
    }
}