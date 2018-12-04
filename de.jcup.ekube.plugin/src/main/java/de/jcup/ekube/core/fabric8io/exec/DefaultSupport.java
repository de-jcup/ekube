package de.jcup.ekube.core.fabric8io.exec;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.fabric8io.elementaction.Fabric8ioGeneralGetStringInfoAction;
import de.jcup.ekube.core.fabric8io.elementaction.Fabric8ioGeneralRefreshAction;
import de.jcup.ekube.core.model.AbstractEKubeContainer;
import de.jcup.ekube.core.model.AbstractEKubeElement;
import de.jcup.ekube.core.model.EKubeElement;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.KubernetesClient;

public class DefaultSupport extends AbstractSupport{


	public DefaultSupport(Fabric8ioSupportContext context) {
		super(context);
	}

	public void appendDefaultActions(EKubeContext context, KubernetesClient client, EKubeElement element, Object technicalObject){
		if (!(element instanceof AbstractEKubeElement)){
			return;
		}
		AbstractEKubeElement abstractElement = (AbstractEKubeElement) element;
		if (technicalObject instanceof HasMetadata){
			HasMetadata hasMetadata = (HasMetadata) technicalObject;
			abstractElement.register(new Fabric8ioGeneralGetStringInfoAction(context,client,element,hasMetadata));
		}
		
		abstractElement.register(new Fabric8ioGeneralRefreshAction(context,client,element,technicalObject));
//		if (!(abstractElement instanceof AbstractEKubeContainer)){
//			return;
//		}
//		AbstractEKubeContainer abstractContainer =(AbstractEKubeContainer) abstractElement;
	}

}
