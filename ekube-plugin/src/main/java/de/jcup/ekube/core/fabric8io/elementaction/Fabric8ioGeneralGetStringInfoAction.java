package de.jcup.ekube.core.fabric8io.elementaction;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.ExecutionParameters;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import de.jcup.ekube.core.model.EKubeElement;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.internal.SerializationUtils;

public class Fabric8ioGeneralGetStringInfoAction extends AbstractFabric8ioElementAction<EKubeElement, String> {

    public Fabric8ioGeneralGetStringInfoAction(EKubeContext context, KubernetesClient client, EKubeElement kubeElement) {
        super(context, client, EKubeActionIdentifer.SHOW_YAML, kubeElement);
    }

    @Override
    public String execute(ExecutionParameters params) {
        try {

            Object m = kubeElement.getTechnicalObject();
            if (m instanceof HasMetadata) {
                HasMetadata metaData = (HasMetadata) m;
                String asYaml = SerializationUtils.dumpAsYaml(metaData);
                return asYaml;
            }
            return null;

        } catch (JsonProcessingException e) {
            getContext().getErrorHandler().logError("was not able to dump as yaml", e);
            return "Failed:" + e.getMessage();
        }
    }

}
