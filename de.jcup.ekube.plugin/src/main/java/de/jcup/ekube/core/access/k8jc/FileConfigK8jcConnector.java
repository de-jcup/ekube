package de.jcup.ekube.core.access.k8jc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import de.jcup.ekube.core.EKubeException;
import de.jcup.ekube.core.UIAdapter;
import de.jcup.ekube.core.access.Kubernetes;
import de.jcup.ekube.core.access.fallback.FallbackKubernetes;
import de.jcup.ekube.core.access.Connector;
import de.jcup.ekube.core.access.ErrorHandler;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.KubeConfig;
/* K8JC = kubernetes java client*/
public class FileConfigK8jcConnector implements Connector {
	
	private File file;

	public FileConfigK8jcConnector(File file){
		this.file=file;
	}
	
	@Override
	public Kubernetes connect(ErrorHandler errorHandler){
		try {
			KubeConfig config = KubeConfig.loadKubeConfig(new FileReader(file));
			K8jcKubernetes kubernetes = new K8jcKubernetes(errorHandler,config);
			kubernetes.name="file configuration";
			return kubernetes;

		} catch (FileNotFoundException e) {
			errorHandler.logError("Not able to get api client", e);
		} catch (IOException e) {
			errorHandler.logError("File exists, but still not able to get api client", e);
		}
		return new FallbackKubernetes();
		
	}

}
