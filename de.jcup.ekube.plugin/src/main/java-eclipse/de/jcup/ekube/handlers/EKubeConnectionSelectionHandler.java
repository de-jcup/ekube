package de.jcup.ekube.handlers;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import de.jcup.ekube.EclipseKubernetesErrorHandler;
import de.jcup.ekube.core.fabric8io.DefaultFabric8ioModelBuilder;
import de.jcup.ekube.core.model.EKubeModel;
import de.jcup.ekube.core.model.EKubeModelToStringDumpConverter;

public class EKubeConnectionSelectionHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		
		String home = System.getProperty("user.home");
		File file = new File(home + "/.kube/config");
		MessageDialog.openInformation(window.getShell(), "Info", "Start connection to kubernetes as defined in\n "+file);
		EKubeModel model = new DefaultFabric8ioModelBuilder().build(null, new EclipseKubernetesErrorHandler());
		System.out.println(new EKubeModelToStringDumpConverter().convert(model));
//		Kubernetes kubernetes = connector.connect(new EclipseKubernetesErrorHandler());
//		KubernetesRegistry.set(kubernetes);
		return null;
	}
}
