package de.jcup.ekube.core.fabric8io;

import java.io.File;
import java.util.List;

import de.jcup.ekube.core.EKubeConfiguration;
import de.jcup.ekube.core.EKubeConfigurationContext;
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
	
	private ErrorHandler errorHandler;
	private EKubeConfiguration configuration;

	public Fabric8ioEKubeModelBuilder(EKubeConfiguration configuration, ErrorHandler errorHandler){
		this.errorHandler=errorHandler;
		this.configuration=configuration;
	}
	
	
	@Override
	public EKubeModel build() {
		EKubeModel model = new EKubeModel(errorHandler);
		File cubeConfigFile = configuration.getKubeConfigFile();
		System.setProperty(io.fabric8.kubernetes.client.Config.KUBERNETES_KUBECONFIG_FILE,
				cubeConfigFile.getAbsolutePath());

		String newCurrentContextName = configuration.getCurrentContext();
		EKubeConfigurationContext contextConfiguration = configuration.findContextConfiguration(newCurrentContextName);
		if (contextConfiguration==null){
			/* not possible - return emtpy model*/
			return model;
		}
		CurrentContextContainer currentContext = setupCurrentContextContainer(model, contextConfiguration);
		
		
		Config config = Config.autoConfigure(newCurrentContextName);
		
		KubernetesClient client = new DefaultKubernetesClient(config);
		
		
		
		
		/* build child content by using current context... if need more we must change the context and rebuild!*/
		addNamespacesToCurrentContext(model, client);

		List<NamespaceContainer> namespaceContainers = currentContext.getNamespaces();
		for (NamespaceContainer namespaceContainer: namespaceContainers){
			PodUtils.addPodsFromNamespace(client, namespaceContainer);
			ServiceUtils.addServicesFromNamespace(client, namespaceContainer);
		}
		return model;
				
	}


	protected CurrentContextContainer setupCurrentContextContainer(EKubeModel model,
			EKubeConfigurationContext contextConfiguration) {
		/* setup current context container */
		CurrentContextContainer currentContext = model.getCurrentContext();
		
		/* use settings from configuration - will be used for connection */
		currentContext.setLabel(contextConfiguration.getName());
		currentContext.setName(contextConfiguration.getName());
		currentContext.setCluster(contextConfiguration.getCluster());
		currentContext.setUser(contextConfiguration.getUser());
		return currentContext;
	}

	protected void addNamespacesToCurrentContext(EKubeModel model, KubernetesClient client) {
		CurrentContextContainer currentContextContainer = model.getCurrentContext();
		NonNamespaceOperation<Namespace, NamespaceList, DoneableNamespace, Resource<Namespace, DoneableNamespace>> namespaces = client.namespaces();
		NamespaceList namespacesList = namespaces.list();
		for (Namespace namespace: namespacesList.getItems()){
			String namespaceName = namespace.getMetadata().getName();

			NamespaceContainer namespaceContainer = new NamespaceContainer();
			namespaceContainer.setLabel(namespaceName);
			namespaceContainer.setName(namespaceName);

			currentContextContainer.add(namespaceContainer);
		}
	}

	
	
	public static void main(String[] args) {
		long time1 = System.currentTimeMillis();
		EKubeModel model = new Fabric8ioEKubeModelBuilder(new EKubeConfiguration(), new ErrorHandler() {
			
			@Override
			public void logError(String message, Exception e) {
				System.err.println(message);
				if (e!=null){
					e.printStackTrace();
				}
			}
		}).build();
		long time2 = System.currentTimeMillis();
	
		System.out.println(new EKubeModelToStringDumpConverter().convert(model));
		
		System.out.println("time:"+(time2-time1));
	}
}
