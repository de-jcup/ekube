package de.jcup.ekube.core.fabric8io;

import java.io.File;
import java.util.List;

import de.jcup.ekube.core.DefaultEKubeConfiguration;
import de.jcup.ekube.core.DefaultEKubeContext;
import de.jcup.ekube.core.EKubeConfiguration;
import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.EKubeContextConfigurationEntry;
import de.jcup.ekube.core.ErrorHandler;
import de.jcup.ekube.core.model.CurrentContextContainer;
import de.jcup.ekube.core.model.EKubeModel;
import de.jcup.ekube.core.model.EKubeModelBuilder;
import de.jcup.ekube.core.model.EKubeModelToStringDumpConverter;
import de.jcup.ekube.core.model.NamespaceContainer;
import io.fabric8.kubernetes.api.model.DoneableNamespace;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceList;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;

public class Fabric8ioEKubeModelBuilder implements EKubeModelBuilder {
	

	@Override
	public EKubeModel build(EKubeContext context) {
		int totalWork = 0;
		
		EKubeModel model = new EKubeModel();
		EKubeConfiguration configuration = context.getConfiguration();
		File cubeConfigFile = configuration.getKubeConfigFile();
		System.setProperty(io.fabric8.kubernetes.client.Config.KUBERNETES_KUBECONFIG_FILE,
				cubeConfigFile.getAbsolutePath());

		String wantedKubernetesContext = configuration.getKubernetesContext();
		EKubeContextConfigurationEntry entry = configuration.findContextConfigurationEntry(wantedKubernetesContext);
		if (entry==null){
			/* not possible - return emtpy model*/
			return model;
		}
		CurrentContextContainer currentContext = setupCurrentContextContainer(model, entry);
		
		
		Config config = Config.autoConfigure(wantedKubernetesContext);
		
		KubernetesClient client = new DefaultKubernetesClient(config);
		
		
		
		
		/* build child content by using current context... if need more we must change the context and rebuild!*/
		addNamespacesToCurrentContext(context, model, client);

		List<NamespaceContainer> namespaceContainers = currentContext.getNamespaces();
		for (NamespaceContainer namespaceContainer: namespaceContainers){
			context.getProgressHandler().beginSubTask("inspecting namespace: "+namespaceContainer.getLabel(), totalWork++);
			PodUtils.addPodsFromNamespace(client, namespaceContainer);
			ServiceUtils.addServicesFromNamespace(client, namespaceContainer);
			VolumeUtils.addVolumeClaimsFromNamespace(client, namespaceContainer);
			NetworkUtils.addNetworkPolicies(client, namespaceContainer);
			ConfigMapUtils.addConfigMapsFromNamespace(client,namespaceContainer);
		}
		return model;
				
	}


	protected CurrentContextContainer setupCurrentContextContainer(EKubeModel model,
			EKubeContextConfigurationEntry entry) {
		/* setup current context container */
		CurrentContextContainer currentContextContainer = model.getCurrentContext();
		
		/* use settings from configuration - will be used for connection */
		currentContextContainer.setName(entry.getName());
		currentContextContainer.setCluster(entry.getCluster());
		currentContextContainer.setUser(entry.getUser());
		return currentContextContainer;
	}

	protected void addNamespacesToCurrentContext(EKubeContext context, EKubeModel model, KubernetesClient client) {
		CurrentContextContainer currentContextContainer = model.getCurrentContext();
		NonNamespaceOperation<Namespace, NamespaceList, DoneableNamespace, Resource<Namespace, DoneableNamespace>> namespaces = client.namespaces();
		NamespaceList namespacesList = namespaces.list();
		for (Namespace namespace: namespacesList.getItems()){
			String namespaceName = namespace.getMetadata().getName();
			if (isNamespaceFiltered(context, namespaceName)){
				continue;
			}

			NamespaceContainer namespaceContainer = new NamespaceContainer();
			namespaceContainer.setName(namespaceName);

			currentContextContainer.add(namespaceContainer);
		}
	}


	protected boolean isNamespaceFiltered(EKubeContext context, String namespaceName) {
		return context.getConfiguration().getFilteredNamespaces().contains(namespaceName);
	}

	
	
	public static void main(String[] args) {
		DefaultEKubeConfiguration configuration = new DefaultEKubeConfiguration();
		configuration.setKubernetesContext("minikube");
		
		DefaultEKubeContext context = new DefaultEKubeContext(
		new ErrorHandler() {
			
			@Override
			public void logError(String message, Exception e) {
				System.err.println(message);
				if (e!=null){
					e.printStackTrace();
				}
			}
		}, configuration);
		
		long time1 = System.currentTimeMillis();
		EKubeModel model = new Fabric8ioEKubeModelBuilder().build(context);
		long time2 = System.currentTimeMillis();
	
		System.out.println(new EKubeModelToStringDumpConverter().convert(model));
		
		System.out.println("time:"+(time2-time1));
	}
}
