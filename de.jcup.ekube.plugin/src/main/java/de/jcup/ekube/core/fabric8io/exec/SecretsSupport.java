package de.jcup.ekube.core.fabric8io.exec;

import java.util.List;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.ExecutionParameters;
import de.jcup.ekube.core.fabric8io.elementaction.Fabric8ioGenericExecutionAction;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import de.jcup.ekube.core.model.NamespaceContainer;
import de.jcup.ekube.core.model.SecretElement;
import de.jcup.ekube.core.model.SecretsContainer;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretList;
import io.fabric8.kubernetes.client.KubernetesClient;

public class SecretsSupport extends AbstractSupport {

    public SecretsSupport(Fabric8ioSupportContext context) {
        super(context);
    }

    private AddSecretsFromNamespaceExecutable addSecretsFromNamespaceExecutable = new AddSecretsFromNamespaceExecutable();
    private UpdateConfigMapsExecutable updateConfigMapsExecutable = new UpdateConfigMapsExecutable();

    public void addSecretsFromNamespace(NamespaceContainer namespaceContainer) {
        this.addSecretsFromNamespace(getContext(), getClient(), namespaceContainer);
    }

    public void addSecretsFromNamespace(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer) {
        context.getExecutor().execute(context, addSecretsFromNamespaceExecutable, namespaceContainer, client);
    }

    public void updateStatus(EKubeContext context, KubernetesClient client, Secret technicalObject, SecretElement kubeElement) {
        context.getExecutor().execute(context, updateConfigMapsExecutable, kubeElement, client,
                new ExecutionParameters().set(Secret.class, technicalObject));
    }

    private class UpdateConfigMapsExecutable implements Fabric8ioSafeExecutable<SecretElement, Void> {

        @Override
        public Void execute(EKubeContext context, KubernetesClient client, SecretElement kubeElement, ExecutionParameters parameters) {
            // https://kubernetes.io/docs/concepts/services-networking/service/
            kubeElement.setData(parameters.get(Secret.class).getData());
            kubeElement.setStatus("elements:" + kubeElement.getData().size());

            /* set this itself as action for refresh */
            Fabric8ioGenericExecutionAction<SecretElement, Void> x = new Fabric8ioGenericExecutionAction<>(updateConfigMapsExecutable,
                    EKubeActionIdentifer.REFRESH_STATUS, context, client, kubeElement);
            kubeElement.setAction(x);

            return null;
        }
    }

    private class AddSecretsFromNamespaceExecutable implements Fabric8ioSafeExecutableNoData<NamespaceContainer> {

        @Override
        public Void execute(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer, ExecutionParameters parameters) {
            String namespaceName = namespaceContainer.getName();
            SecretList serviceList = client.secrets().inNamespace(namespaceName).list();
            List<Secret> items = serviceList.getItems();
            SecretsContainer fetchSecretsContainer = namespaceContainer.fetchSecretsContainer();

            /* set this itself as action for rebuild */
            Fabric8ioGenericExecutionAction<NamespaceContainer, Void> x = new Fabric8ioGenericExecutionAction<>(addSecretsFromNamespaceExecutable,
                    EKubeActionIdentifer.REFRESH_CHILDREN, context, client, namespaceContainer);
            fetchSecretsContainer.setAction(x);

            fetchSecretsContainer.startOrphanCheck(parameters);
            for (Secret secret : items) {
                SecretElement newElement = new SecretElement(secret.getMetadata().getUid(), secret);
                if (!parameters.isHandling(newElement)) {
                    continue;
                }
                SecretElement secretElement = fetchSecretsContainer.AddOrReuseExisting(newElement);
                secretElement.setName(secret.getMetadata().getName());

                updateStatus(context, client, secret, secretElement);

            }
            fetchSecretsContainer.removeOrphans();
            return null;
        }
    }

}
