package de.jcup.ekube.core.fabric8io.elementaction;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.fabric8io.exec.Fabric8ioSafeExecutable;
import de.jcup.ekube.core.model.AbstractEKubeElement;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import io.fabric8.kubernetes.client.KubernetesClient;

public class Fabric8ioGenericExecutionAction<E extends AbstractEKubeElement,T,R> extends AbstractFabric8ioElementAction<E,T,R> {

	private Fabric8ioSafeExecutable<E, T,R> executable;

	public Fabric8ioGenericExecutionAction(Fabric8ioSafeExecutable<E,T,R> executable, EKubeActionIdentifer<R>identifier, EKubeContext context, KubernetesClient client,
			E ekubeElement, T technicalObject) {
		super(context,client, identifier, ekubeElement, technicalObject);
		this.executable=executable;
	}

	@Override
	public R execute() {
		return executable.execute(context, client, kubeElement,technicalObject);
	}

}
