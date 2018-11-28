package de.jcup.ekube.core.fabric8io;

import java.io.File;
import java.util.List;

import de.jcup.ekube.core.EKubeConfiguration;
import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.EKubeContextConfigurationEntry;
import de.jcup.ekube.core.SafeExecutable;
import de.jcup.ekube.core.SafeExecutionListener;
import de.jcup.ekube.core.SafeExecutor;
import de.jcup.ekube.core.fabric8io.exec.Fabric8ioSupports;
import de.jcup.ekube.core.model.AbstractEKubeElement;
import de.jcup.ekube.core.model.CurrentContextContainer;
import de.jcup.ekube.core.model.EKubeElement;
import de.jcup.ekube.core.model.EKubeModel;
import de.jcup.ekube.core.model.EKubeModelBuilder;
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
		Fabric8ioSupports supports = new Fabric8ioSupports(context,client);
		
		BuildTimeSafeExecutionListener listener = new BuildTimeSafeExecutionListener(supports);
		
		SafeExecutor executor = context.getExecutor();
		executor.add(listener);
		
		
		/* build child content by using current context... if need more we must change the context and rebuild!*/
		addNamespacesToCurrentContext(context, model, client);
		
		context.getProgressHandler().beginSubTask("inspecting nodes", totalWork++);
		supports.nodes().addnodesFromNamespace(context, client, currentContext.getNodesContainer());
		
		List<NamespaceContainer> namespaceContainers = currentContext.getNamespaces();
		for (NamespaceContainer namespaceContainer: namespaceContainers){
			context.getProgressHandler().beginSubTask("inspecting namespace: "+namespaceContainer.getLabel(), totalWork++);
			supports.deployments().addDeploymentFromNamespace(namespaceContainer);
			supports.pods().addPodsFromNamespace(namespaceContainer);
			supports.services().addServicesFromNamespace(namespaceContainer);
			supports.volumes().addVolumeClaimsFromNamespace(namespaceContainer);
			supports.networks().addNetworkPolicies(namespaceContainer);
			supports.configMaps().addConfigMapsFromNamespace(namespaceContainer);
			
		}
		
		/* dispose build listener */
		executor.remove(listener);
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
		return context.getConfiguration().isNamespaceFiltered(namespaceName);
	}

	/**
	 * Special executor which does add default actions after execution - only interesting on build time!
	 * @author Albert Tregnaghi
	 *
	 */
	private class BuildTimeSafeExecutionListener implements SafeExecutionListener{
		private Fabric8ioSupports supports;

		public BuildTimeSafeExecutionListener(Fabric8ioSupports supports) {
			this.supports=supports;
		}

		@Override
		public <E extends EKubeElement, C, D> void afterExecute(EKubeContext context,
				SafeExecutable<E, C, D> executable, E element, C client, D data) {
			supports.defaults().appendDefaultActions(supports.getContext(), supports.getClient(), (AbstractEKubeElement) element, data);
			
		}
	}
}
