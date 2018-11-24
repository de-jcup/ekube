package de.jcup.ekube;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import de.jcup.ekube.core.ErrorHandler;

public class EclipseKubernetesErrorHandler implements ErrorHandler {

	@Override
	public void logError(String message, Exception e) {
		Activator.getDefault().getLog().log(new Status(IStatus.ERROR,Activator.PLUGIN_ID,message,e));
	}

}
