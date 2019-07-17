package de.jcup.ekube;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import de.jcup.ekube.core.DefaultSafeExecutor;
import de.jcup.ekube.core.EKubeConfiguration;
import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.EKubeProgressHandler;
import de.jcup.ekube.core.ErrorHandler;
import de.jcup.ekube.core.SafeExecutor;

public class EclipseEKubeContext implements EKubeContext {

    private EclipseProgressHandler progressHandler;
    private SafeExecutor executor;

    public EclipseEKubeContext() {
        this(null);
    }

    public EclipseEKubeContext(IProgressMonitor monitor) {
        this.progressHandler = new EclipseProgressHandler(monitor);
    }

    public void setExecutor(SafeExecutor executor) {
        this.executor = executor;
    }

    @Override
    public ErrorHandler getErrorHandler() {
        return Activator.getDefault().getErrorHandler();
    }

    @Override
    public EKubeConfiguration getConfiguration() {
        return Activator.getDefault().getConfiguration();
    }

    @Override
    public EKubeProgressHandler getProgressHandler() {
        return progressHandler;
    }

    private class EclipseProgressHandler implements EKubeProgressHandler {
        private IProgressMonitor monitor;

        public EclipseProgressHandler(IProgressMonitor monitor) {
            if (monitor == null) {
                this.monitor = new NullProgressMonitor();
            } else {
                this.monitor = monitor;
            }
        }

        @Override
        public void beginTask(String name, int totalWork) {
            monitor.beginTask(name, totalWork);
        }

        @Override
        public void worked(int summary) {
            monitor.worked(summary);
        }

        @Override
        public void beginSubTask(String name) {
            monitor.subTask(name);

        }

    }

    @Override
    public SafeExecutor getExecutor() {
        if (executor == null) {
            executor = new DefaultSafeExecutor();
        }
        return executor;
    }

}
