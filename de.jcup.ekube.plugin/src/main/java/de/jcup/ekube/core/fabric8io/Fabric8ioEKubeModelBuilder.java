package de.jcup.ekube.core.fabric8io;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.jcup.ekube.core.EKubeConfiguration;
import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.EKubeContextConfigurationEntry;
import de.jcup.ekube.core.SafeExecutable;
import de.jcup.ekube.core.SafeExecutionListener;
import de.jcup.ekube.core.SafeExecutor;
import de.jcup.ekube.core.fabric8io.elementaction.Fabric8ioGenericExecutionAction;
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
		if (entry == null) {
			/* not possible - return emtpy model */
			return model;
		}
		CurrentContextContainer currentContext = setupCurrentContextContainer(model, entry);

		Config config = Config.autoConfigure(wantedKubernetesContext);

		KubernetesClient client = new DefaultKubernetesClient(config);
		Fabric8ioSupports supports = new Fabric8ioSupports(context, client);

		BuildTimeSafeExecutionListener listener = new BuildTimeSafeExecutionListener(supports);

		SafeExecutor executor = context.getExecutor();
		executor.add(listener);

		/*
		 * build child content by using current context... if need more we must
		 * change the context and rebuild!
		 */
		addNamespacesToCurrentContext(context, model, client, supports);

		List<NamespaceContainer> namespaceContainers = currentContext.getNamespaces();
		int size = namespaceContainers.size() + 1;
		context.getProgressHandler().beginTask("Building tree", size);

		context.getProgressHandler().beginSubTask("Inspecting nodes");
		supports.nodes().addnodesFromNamespace(context, client, currentContext.getNodesContainer());
		context.getProgressHandler().worked(totalWork++);

		for (NamespaceContainer namespaceContainer : namespaceContainers) {
			context.getProgressHandler().beginSubTask("Inspecting namespace: " + namespaceContainer.getLabel());
			
			supports.namespaces().fill(context, client, namespaceContainer);
			
			context.getProgressHandler().worked(totalWork++);
			// EndpointsList endpointList = client.endpoints().list();
			// List<Endpoints> items = endpointList.getItems();
			// for (Endpoints endpoints: items){
			// endpoints.getMetadata();
			// }
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

	protected void addNamespacesToCurrentContext(EKubeContext context, EKubeModel model, KubernetesClient client,
			Fabric8ioSupports supports) {
		CurrentContextContainer currentContextContainer = model.getCurrentContext();
		NonNamespaceOperation<Namespace, NamespaceList, DoneableNamespace, Resource<Namespace, DoneableNamespace>> namespaces = client
				.namespaces();
		NamespaceList namespacesList = namespaces.list();
		for (Namespace namespace : namespacesList.getItems()) {
			String namespaceName = namespace.getMetadata().getName();
			if (isNamespaceFiltered(context, namespaceName)) {
				continue;
			}

			NamespaceContainer namespaceContainer = new NamespaceContainer(namespace.getMetadata().getUid());
			namespaceContainer.setName(namespaceName);

			addDefaultActionsToNamespaceAndPredefinedChildren(context, client, supports, namespace, namespaceContainer);
			currentContextContainer.add(namespaceContainer);
		}
	}

	/*
	 * add default actions to namespace and also to the (predefined) children -
	 * necessary, because no actions will be added otherwise
	 */
	protected List<EKubeElement> addDefaultActionsToNamespaceAndPredefinedChildren(EKubeContext context,
			KubernetesClient client, Fabric8ioSupports supports, Namespace namespace,
			NamespaceContainer namespaceContainer) {
		List<EKubeElement> defaultsMissing = new ArrayList<>();
		defaultsMissing.add(namespaceContainer);
		defaultsMissing.addAll(namespaceContainer.getChildren());
		for (EKubeElement element : defaultsMissing) {
			supports.defaults().appendDefaultActions(context, client, element, namespace);
		}
		return defaultsMissing;
	}

	protected boolean isNamespaceFiltered(EKubeContext context, String namespaceName) {
		return context.getConfiguration().isNamespaceFiltered(namespaceName);
	}

	/**
	 * Special executor which does add default actions after execution - only
	 * interesting on build time!
	 * 
	 * @author Albert Tregnaghi
	 *
	 */
	private class BuildTimeSafeExecutionListener implements SafeExecutionListener {
		private Fabric8ioSupports supports;

		public BuildTimeSafeExecutionListener(Fabric8ioSupports supports) {
			this.supports = supports;
		}

		@Override
		public <E extends EKubeElement, C, D,R> void afterExecute(EKubeContext context,
				SafeExecutable<E, C, D,R > executable, E element, C client, D data) {
			supports.defaults().appendDefaultActions(supports.getContext(), supports.getClient(),
					(AbstractEKubeElement) element, data);

		}
	}
}
