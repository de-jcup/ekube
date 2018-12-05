package de.jcup.ekube.core;

public class DefaultEKubeContext implements EKubeContext {

    private ErrorHandler errorHandler;
    private EKubeConfiguration configuration;
    private EKubeProgressHandler progressHandler;
    private SafeExecutor executor;

    public DefaultEKubeContext(ErrorHandler errorHandler, EKubeConfiguration configuration) {
        this(errorHandler, configuration, null);
    }

    public DefaultEKubeContext(ErrorHandler errorHandler, EKubeConfiguration configuration, EKubeProgressHandler progressHandler) {
        this.errorHandler = errorHandler;
        this.configuration = configuration;
        if (progressHandler == null) {
            this.progressHandler = new NullProgressHandler();
        } else {
            this.progressHandler = progressHandler;
        }
        this.executor = new DefaultSafeExecutor();
    }

    @Override
    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    @Override
    public EKubeConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public EKubeProgressHandler getProgressHandler() {
        return progressHandler;
    }

    @Override
    public SafeExecutor getExecutor() {
        return executor;
    }

}
