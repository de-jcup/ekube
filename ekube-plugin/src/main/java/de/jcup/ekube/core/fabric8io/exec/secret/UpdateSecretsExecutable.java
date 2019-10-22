package de.jcup.ekube.core.fabric8io.exec.secret;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.ExecutionParameters;
import de.jcup.ekube.core.fabric8io.elementaction.Fabric8ioGenericExecutionAction;
import de.jcup.ekube.core.fabric8io.exec.Fabric8ioSafeExecutable;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import de.jcup.ekube.core.model.SecretElement;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.client.KubernetesClient;

class UpdateSecretsExecutable implements Fabric8ioSafeExecutable<SecretElement, Void> {

    @Override
    public Void execute(EKubeContext context, KubernetesClient client, SecretElement kubeElement, ExecutionParameters parameters) {
        // https://kubernetes.io/docs/concepts/services-networking/service/
        kubeElement.setData(parameters.get(Secret.class).getData());
        kubeElement.setStatus("elements:" + kubeElement.getData().size());

        /* set this itself as action for refresh */
        Fabric8ioGenericExecutionAction<SecretElement, Void> x = new Fabric8ioGenericExecutionAction<>(this,
                EKubeActionIdentifer.REFRESH_STATUS, context, client, kubeElement);
        kubeElement.register(x);

        return null;
    }
}