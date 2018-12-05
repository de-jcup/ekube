package de.jcup.ekube.core.fabric8io.exec;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.fabric8io.elementaction.Fabric8ioGeneralGetStringInfoAction;
import de.jcup.ekube.core.fabric8io.elementaction.Fabric8ioGeneralRefreshAction;
import de.jcup.ekube.core.model.AbstractEKubeElement;
import de.jcup.ekube.core.model.EKubeElement;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.KubernetesClient;

public class DefaultSupport extends AbstractSupport {

    public DefaultSupport(Fabric8ioSupportContext context) {
        super(context);
    }

    public void appendDefaults(EKubeContext context, KubernetesClient client, EKubeElement element) {
        if (!(element instanceof AbstractEKubeElement)) {
            return;
        }
        AbstractEKubeElement abstractElement = (AbstractEKubeElement) element;
        Object technicalObject = element.getTechnicalObject();
        if (technicalObject instanceof HasMetadata) {
            HasMetadata hasMetadata = (HasMetadata) technicalObject;
            abstractElement.setAction(new Fabric8ioGeneralGetStringInfoAction(context, client, element));
            abstractElement.setTechnicalObject(hasMetadata);
        }

        abstractElement.setAction(new Fabric8ioGeneralRefreshAction(context, client, element));
    }

}
