package de.jcup.ekube.core.fabric8io.exec;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.ExecutionParameters;
import de.jcup.ekube.core.fabric8io.elementaction.Fabric8ioGenericExecutionAction;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import de.jcup.ekube.core.model.NamespaceContainer;
import io.fabric8.kubernetes.client.KubernetesClient;

public class NamespaceSupport extends AbstractSupport {

    private AddAllChildrenExcecutable addAllChildrenExcecutable = new AddAllChildrenExcecutable();

    public NamespaceSupport(Fabric8ioSupportContext context) {
        super(context);
    }

    public void fill(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer) {
        context.getExecutor().execute(context, addAllChildrenExcecutable, namespaceContainer, client);
    }

    private class AddAllChildrenExcecutable implements Fabric8ioSafeExecutableNoData<NamespaceContainer> {

        @Override
        public Void execute(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer, ExecutionParameters parameters) {
            Fabric8ioGenericExecutionAction<NamespaceContainer, Void> x = new Fabric8ioGenericExecutionAction<>(addAllChildrenExcecutable,
                    EKubeActionIdentifer.REFRESH_CHILDREN, context, client, namespaceContainer);
            namespaceContainer.register(x);

            supportContext.deployments().addDeploymentFromNamespace(namespaceContainer);
            supportContext.pods().addPodsFromNamespace(namespaceContainer);
            supportContext.services().addServicesFromNamespace(namespaceContainer);
            supportContext.volumes().addVolumeClaimsFromNamespace(namespaceContainer);
            supportContext.networks().addNetworkPolicies(namespaceContainer);
            supportContext.configMaps().addConfigMapsFromNamespace(namespaceContainer);
            supportContext.secrets().addSecretsFromNamespace(namespaceContainer);

            return null;
        }

    }
}
