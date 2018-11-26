package de.jcup.ekube.core;

public class DefaultEKubeContext implements EKubeContext{
	
	private ErrorHandler errorHandler;
	private EKubeConfiguration configuration;
	private EKubeProgressHandler progressHandler;
	
	public DefaultEKubeContext(ErrorHandler errorHandler, EKubeConfiguration configuration) {
		this(errorHandler,configuration,null);
	}

	public DefaultEKubeContext(ErrorHandler errorHandler, EKubeConfiguration configuration, EKubeProgressHandler handler) {
		this.errorHandler=errorHandler;
		this.configuration=configuration;
		if (progressHandler==null){
			progressHandler=new NullProgressHandler();
		}
		this.progressHandler=handler;
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

}
