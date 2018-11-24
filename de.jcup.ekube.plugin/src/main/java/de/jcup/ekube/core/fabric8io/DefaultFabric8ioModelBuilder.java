package de.jcup.ekube.core.fabric8io;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import de.jcup.ekube.core.access.ErrorHandler;
import de.jcup.ekube.core.model.ConfiguredContextContainer;
import de.jcup.ekube.core.model.CurrentContextContainer;
import de.jcup.ekube.core.model.EKubeModel;
import de.jcup.ekube.core.model.EKubeModelToStringDumpConverter;
import de.jcup.ekube.core.model.NamespaceContainer;
import de.jcup.ekube.core.model.PodContainer;
import io.fabric8.kubernetes.api.model.Config;
import io.fabric8.kubernetes.api.model.Context;
import io.fabric8.kubernetes.api.model.DoneableNamespace;
import io.fabric8.kubernetes.api.model.NamedContext;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceList;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.internal.KubeConfigUtils;

public class DefaultFabric8ioModelBuilder {

	public EKubeModel build(String currentContext, ErrorHandler handler){
		EKubeModel model = new EKubeModel(handler);
		File cubeConfigFile  = new File(System.getProperty("user.home")+"/.kube/config");
		System.setProperty(io.fabric8.kubernetes.client.Config.KUBERNETES_KUBECONFIG_FILE, cubeConfigFile.getAbsolutePath());
		addConfiguratedContextsAndMarkupCurrentContext(handler, model,cubeConfigFile);
		
		/* @formatter:off*/
		KubernetesClient client = new DefaultKubernetesClient();
		/* build childcontent by using current context... if need more we must change the context and rebuild!*/
		addNamespacesToCurrentContext(model, client);

		List<NamespaceContainer> namespaceContainers = model.getCurrentContext().getNamespaces();
		for (NamespaceContainer namespaceContainer: namespaceContainers){
			String namespaceName = namespaceContainer.getName();
			PodList podList = client.pods().inNamespace(namespaceName).list();
			for (Pod pod: podList.getItems()){
				PodContainer podContainer = new PodContainer();
				podContainer.setLabel(pod.getMetadata().getName());
				namespaceContainer.getPodsContainer().add(podContainer);
			}
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

	/**
	 * Sorrowly we got not chance to resolve the normal contexts from client config. so wie use model config and load
	 * kubernetes config by our own and set information into model.
	 * @param handler
	 * @param model
	 */
	protected void addConfiguratedContextsAndMarkupCurrentContext(ErrorHandler handler, EKubeModel model, File cubeConfigFile) {
		
		try {
			Config config = KubeConfigUtils.parseConfig(cubeConfigFile);
			String currentContext = config.getCurrentContext();
			model.setCurrentContext(currentContext);
			
			List<NamedContext> namedContexts = config.getContexts();
			
			for (NamedContext namedContext: namedContexts){
				Context context  =namedContext.getContext();
				
				/* we build context container - does only contain some properties thats all - no children*/
				ConfiguredContextContainer contextContainer = new ConfiguredContextContainer();
				String contextName = namedContext.getName();
				contextContainer.setLabel(contextName);
				contextContainer.setName(contextName);
				contextContainer.setUser(context.getUser());
				contextContainer.setCluster(context.getCluster());
				
				if (StringUtils.equals(currentContext, contextName)){
					CurrentContextContainer currentContextContainer = new CurrentContextContainer();
					currentContextContainer.setCluster(contextContainer.getCluster());
					currentContextContainer.setName(contextName);
					currentContextContainer.setLabel(contextName);
					currentContextContainer.setUser(contextContainer.getUser());
					model.setCurrentContext(currentContextContainer);
				}
				
				model.add(contextContainer);
			}
			
		} catch (IOException e) {
			handler.logError("Was not able to fetch context definitions", e);
		}
	}
	
	public static void main(String[] args) {
		EKubeModel model = new DefaultFabric8ioModelBuilder().build(null, new ErrorHandler() {
			
			@Override
			public void logError(String message, Exception e) {
				System.err.println(message);
				e.printStackTrace();
			}
		});
		System.out.println(new EKubeModelToStringDumpConverter().convert(model));
	}
}
