package de.jcup.ekube.core.fabric8io.elementaction;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import de.jcup.ekube.core.model.EKubeElement;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.internal.SerializationUtils;

public class Fabric8ioGeneralGetStringInfoAction extends AbstractFabric8ioElementAction<EKubeElement, HasMetadata,String> {

	public Fabric8ioGeneralGetStringInfoAction(EKubeContext context, KubernetesClient client, EKubeElement kubeElement, HasMetadata technicalObject) {
		super(context,client, EKubeActionIdentifer.GRAB_STRING_INFO, kubeElement, technicalObject);
	}

	@Override
	public String execute() {
		try {
			String asYaml =  SerializationUtils.dumpAsYaml(technicalObject);
			return asYaml;
		} catch (JsonProcessingException e) {
			getContext().getErrorHandler().logError("was not able to dump as yaml", e);
			return "Failed:"+e.getMessage();
		}
	}

}
