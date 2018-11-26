package de.jcup.ekube.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import de.jcup.ekube.Activator;
import de.jcup.ekube.EclipseEKubeContext;
import de.jcup.ekube.core.fabric8io.Fabric8ioConfgurationUpdater;

public class EKubeConnectionSelectionHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Activator activator = Activator.getDefault();
		
		EclipseEKubeContext context  = new EclipseEKubeContext();

		/* next lines load kube config data and creates a list of indexes*/
		Fabric8ioConfgurationUpdater updater = new Fabric8ioConfgurationUpdater();
		updater.update(context);
		
		/* kube config is now up to date and can be used in explorer to switch context*/
		activator.getExplorer().connect(context.getConfiguration());
		return null;
	}
}
