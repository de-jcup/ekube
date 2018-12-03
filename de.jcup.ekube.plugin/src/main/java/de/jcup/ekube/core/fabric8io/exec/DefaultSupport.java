package de.jcup.ekube.core.fabric8io.exec;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.fabric8io.elementaction.Fabric8ioGeneralGetStringInfoAction;
import de.jcup.ekube.core.fabric8io.elementaction.Fabric8ioGeneralRefreshAction;
import de.jcup.ekube.core.model.AbstractEKubeElement;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.KubernetesClient;

public class DefaultSupport extends AbstractSupport{


	public DefaultSupport(Fabric8ioSupportContext context) {
		super(context);
	}

	public void appendDefaultActions(EKubeContext context, KubernetesClient client, AbstractEKubeElement element, Object technicalObject){
		if (technicalObject instanceof HasMetadata){
			HasMetadata hasMetadata = (HasMetadata) technicalObject;
			element.register(new Fabric8ioGeneralGetStringInfoAction(context,client,element,hasMetadata));
		}
		
		element.register(new Fabric8ioGeneralRefreshAction(context,client,element,technicalObject));
		
	}

}
