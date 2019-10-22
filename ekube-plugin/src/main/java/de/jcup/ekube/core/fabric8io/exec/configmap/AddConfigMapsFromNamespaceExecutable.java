package de.jcup.ekube.core.fabric8io.exec.configmap;

import java.util.List;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.ExecutionParameters;
import de.jcup.ekube.core.fabric8io.elementaction.Fabric8ioGenericExecutionAction;
import de.jcup.ekube.core.fabric8io.exec.Fabric8ioSafeExecutableNoData;
import de.jcup.ekube.core.model.ConfigMapElement;
import de.jcup.ekube.core.model.ConfigMapsContainer;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import de.jcup.ekube.core.model.NamespaceContainer;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapList;
import io.fabric8.kubernetes.client.KubernetesClient;

class AddConfigMapsFromNamespaceExecutable implements Fabric8ioSafeExecutableNoData<NamespaceContainer> {

    /**
     * 
     */
    private final ConfigMapSupport configMapSupport;

    /**
     * @param configMapSupport
     */
    AddConfigMapsFromNamespaceExecutable(ConfigMapSupport configMapSupport) {
        this.configMapSupport = configMapSupport;
    }

    @Override
    public Void execute(EKubeContext context, KubernetesClient client, NamespaceContainer namespaceContainer, ExecutionParameters parameters) {
        String namespaceName = namespaceContainer.getName();
        ConfigMapList serviceList = client.configMaps().inNamespace(namespaceName).list();
        List<ConfigMap> items = serviceList.getItems();

        ConfigMapsContainer configMapsContainer = namespaceContainer.fetchConfigMapsContainer();

        /* set this itself as action for rebuild */
        Fabric8ioGenericExecutionAction<NamespaceContainer, Void> x = new Fabric8ioGenericExecutionAction<>(this,
                EKubeActionIdentifer.REFRESH_CHILDREN, context, client, namespaceContainer);
        configMapsContainer.register(x);

        configMapsContainer.startOrphanCheck(parameters);
        for (ConfigMap configMap : items) {
            ConfigMapElement newElement = new ConfigMapElement(configMap.getMetadata().getUid(), configMap);
            if (! parameters.isHandling(newElement)){
                continue;
            }
            ConfigMapElement configMapElement = configMapsContainer
                    .AddOrReuseExisting(newElement);
            configMapElement.setName(configMap.getMetadata().getName());
            this.configMapSupport.updateStatus(context, client, configMap, configMapElement);

        }
        configMapsContainer.removeOrphans();
        return null;
    }

}