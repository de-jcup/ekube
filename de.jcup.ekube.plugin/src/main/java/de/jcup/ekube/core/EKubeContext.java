package de.jcup.ekube.core;

public interface EKubeContext {

    public ErrorHandler getErrorHandler();

    public EKubeConfiguration getConfiguration();

    public EKubeProgressHandler getProgressHandler();

    public SafeExecutor getExecutor();

}
