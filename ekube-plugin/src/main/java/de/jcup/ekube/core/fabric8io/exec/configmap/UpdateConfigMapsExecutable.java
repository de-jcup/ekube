package de.jcup.ekube.core.fabric8io.exec.configmap;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.ExecutionParameters;
import de.jcup.ekube.core.fabric8io.elementaction.Fabric8ioGenericExecutionAction;
import de.jcup.ekube.core.fabric8io.exec.Fabric8ioSafeExecutableNoData;
import de.jcup.ekube.core.model.ConfigMapElement;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.client.KubernetesClient;

class UpdateConfigMapsExecutable implements Fabric8ioSafeExecutableNoData<ConfigMapElement> {

    /**
     * 
     */
    private final ConfigMapSupport configMapSupport;

    /**
     * @param configMapSupport
     */
    UpdateConfigMapsExecutable(ConfigMapSupport configMapSupport) {
        this.configMapSupport = configMapSupport;
    }

    @Override
    public Void execute(EKubeContext context, KubernetesClient client, ConfigMapElement kubeElement, ExecutionParameters parameters) {

        ConfigMap configMap = parameters.get(ConfigMap.class);

        // https://kubernetes.io/docs/concepts/services-networking/service/
        kubeElement.setData(configMap.getData());
        kubeElement.setStatus("elements:" + kubeElement.getData().size());

        /* set this itself as action for refresh */
        Fabric8ioGenericExecutionAction<ConfigMapElement, Void> x = new Fabric8ioGenericExecutionAction<>(this.configMapSupport.updateConfigMapsExecutable,
                EKubeActionIdentifer.REFRESH_STATUS, context, client, kubeElement);
        kubeElement.register(x);

        return null;
    }
}