package de.jcup.ekube.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import de.jcup.ekube.KubeConfigLoader;

public class EKubeReloadKubeConfigurationHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		KubeConfigLoader loader = new KubeConfigLoader();
		loader.load();
		return null;
	}
}
