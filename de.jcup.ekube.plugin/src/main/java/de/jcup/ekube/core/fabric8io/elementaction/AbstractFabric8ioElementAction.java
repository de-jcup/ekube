package de.jcup.ekube.core.fabric8io.elementaction;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.fabric8io.exec.Fabric8ioSupport;
import de.jcup.ekube.core.model.AbstractEKubeElementAction;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import de.jcup.ekube.core.model.EKubeElement;
import io.fabric8.kubernetes.client.KubernetesClient;

public abstract class AbstractFabric8ioElementAction<E extends EKubeElement,T> extends AbstractEKubeElementAction<E, T>{

	protected KubernetesClient client;
	protected static Fabric8ioSupport support = new Fabric8ioSupport();

	public AbstractFabric8ioElementAction(EKubeContext context, KubernetesClient client, EKubeActionIdentifer actionIdentifier, E ekubeElement,
			T technicalObject) {
		super(context, actionIdentifier, ekubeElement, technicalObject);
		this.client=client;
	}
	


}
