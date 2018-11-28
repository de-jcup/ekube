package de.jcup.ekube.core.fabric8io.elementaction;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.fabric8io.exec.Fabric8ioSupports;
import de.jcup.ekube.core.model.AbstractEKubeElementAction;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import de.jcup.ekube.core.model.EKubeElement;
import io.fabric8.kubernetes.client.KubernetesClient;

public abstract class AbstractFabric8ioElementAction<E extends EKubeElement,T,R> extends AbstractEKubeElementAction<E, T,R>{

	protected KubernetesClient client;
	protected Fabric8ioSupports support;

	public AbstractFabric8ioElementAction(EKubeContext context, KubernetesClient client, EKubeActionIdentifer<R> actionIdentifier, E ekubeElement,
			T technicalObject) {
		super(context, actionIdentifier, ekubeElement, technicalObject);
		this.client=client;
		this.support=new Fabric8ioSupports(context, client);
	}
	


}
