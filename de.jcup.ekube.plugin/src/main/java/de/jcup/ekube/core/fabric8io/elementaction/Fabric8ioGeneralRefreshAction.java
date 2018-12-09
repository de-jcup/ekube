package de.jcup.ekube.core.fabric8io.elementaction;

import java.util.Collections;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.ExecutionParameters;
import de.jcup.ekube.core.model.AbstractEKubeElement;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import de.jcup.ekube.core.model.EKubeContainer;
import de.jcup.ekube.core.model.EKubeElement;
import io.fabric8.kubernetes.client.KubernetesClient;

public class Fabric8ioGeneralRefreshAction extends AbstractFabric8ioElementAction<EKubeElement, Void> {

    public Fabric8ioGeneralRefreshAction(EKubeContext context, KubernetesClient client, EKubeElement kubeElement) {
        super(context, client, EKubeActionIdentifer.REFRESH, kubeElement);
    }

    @Override
    public Void execute(ExecutionParameters params) {
        /*
         * refresh yourself by using parent doing a "refresh child" only on
         * this!
         */
        EKubeContainer parent = kubeElement.getParent();
        if(kubeElement instanceof AbstractEKubeElement){
            AbstractEKubeElement abstractElement = (AbstractEKubeElement) kubeElement;
            abstractElement.setErrorMessage(null);
        }
        if (parent==null){
            return null;
        }
        parent.execute(EKubeActionIdentifer.REFRESH_CHILDREN, new ExecutionParameters().setChildren(Collections.singleton(kubeElement)));
        return null;
    }

}
