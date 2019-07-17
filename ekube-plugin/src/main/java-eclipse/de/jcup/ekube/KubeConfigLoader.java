package de.jcup.ekube;

import org.eclipse.core.runtime.IProgressMonitor;

import de.jcup.ekube.core.fabric8io.Fabric8ioConfigurationUpdater;

public class KubeConfigLoader {

    private boolean loaded;

    public void load() {
        load(null);
    }

    public void load(IProgressMonitor monitor) {
        loaded = false;
        try {
            EclipseEKubeContext context = new EclipseEKubeContext(monitor);

            Fabric8ioConfigurationUpdater updater = new Fabric8ioConfigurationUpdater();
            updater.update(context);
            loaded = true;
        } catch (Exception e) {
            Activator.getDefault().getErrorHandler().logError("Was not able to load kube config", e);
        }
    }

    public boolean isLoaded() {
        return loaded;
    }

}
