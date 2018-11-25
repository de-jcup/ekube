package de.jcup.ekube.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import de.jcup.ekube.Activator;
import de.jcup.ekube.core.EKubeConfiguration;
import de.jcup.ekube.core.fabric8io.Fabric8ioConfgurationContextUpdater;

public class EKubeConnectionSelectionHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Activator activator = Activator.getDefault();
		EKubeConfiguration configuration = activator.getConfiguration();

		/* next lines load kube config data and creates a list of indexes*/
		Fabric8ioConfgurationContextUpdater updater = new Fabric8ioConfgurationContextUpdater(activator.getErrorHandler());
		updater.update(configuration);
		
		/* kube config is now up to date and can be used in explorer to switch context*/
		activator.getExplorer().connect(configuration);
		return null;
	}
}
