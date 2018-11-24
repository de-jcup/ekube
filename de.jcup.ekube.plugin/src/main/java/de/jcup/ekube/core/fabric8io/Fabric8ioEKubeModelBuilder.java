package de.jcup.ekube.core.fabric8io;

import java.io.File;
import java.util.List;

import de.jcup.ekube.core.EKubeConfiguration;
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

		String newCurrentContext = configuration.getCurrentContext();
		Config config = Config.autoConfigure(newCurrentContext);
		
		KubernetesClient client = new DefaultKubernetesClient(config);
		model.getCurrentContext().setName(newCurrentContext);
		model.getCurrentContext().setLabel(newCurrentContext);
		
		/* build child content by using current context... if need more we must change the context and rebuild!*/
		addNamespacesToCurrentContext(model, client);

		List<NamespaceContainer> namespaceContainers = model.getCurrentContext().getNamespaces();
		for (NamespaceContainer namespaceContainer: namespaceContainers){
			PodUtils.addPodsFromNamespace(client, namespaceContainer);
			ServiceUtils.addServicesFromNamespace(client, namespaceContainer);
		}
		return model;
				
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
