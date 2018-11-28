package de.jcup.ekube;

import org.eclipse.core.runtime.IProgressMonitor;

import de.jcup.ekube.core.fabric8io.Fabric8ioConfgurationUpdater;

public class KubeConfigLoader {
	
	private boolean loaded;
	
	public void load() {
		load(null);
	}

	public void load(IProgressMonitor monitor) {
		loaded=false;
		try{
			EclipseEKubeContext context = new EclipseEKubeContext(monitor);
			
			Fabric8ioConfgurationUpdater updater = new Fabric8ioConfgurationUpdater();
			updater.update(context);
			loaded=true;
		}catch(Exception e){
			Activator.getDefault().getErrorHandler().logError("Was not able to load kube config", e);
		}
	}
	
	public boolean isLoaded() {
		return loaded;
	}

}
