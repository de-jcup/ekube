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
