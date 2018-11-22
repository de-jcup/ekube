package de.jcup.ekube.handlers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1PodList;
import io.kubernetes.client.util.Config;

public class EKubeConnectionSelectionHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		MessageDialog.openInformation(window.getShell(), "EKube Plugin",
				"Hello, this will start a connection using config file");
		/*
		 * FIXME ATR, 22.11.2018: very quick and dirty approach to test login...
		 * currently only minikube conf files respectively config files
		 * supported. should be in one class etc. improve...
		 */
		String home = System.getProperty("user.home");
		File file = new File(home + "/.kube/config");
		if (!file.exists()) {
			MessageDialog.openError(window.getShell(), "Problen", "Cannot find cube config file!");
			return null;
		}

		try {
			ApiClient client = Config.fromConfig(new FileInputStream(file));
			Configuration.setDefaultApiClient(client);

		} catch (FileNotFoundException e) {
			throw new ExecutionException("Not able to get api client", e);
		} catch (IOException e) {
			throw new ExecutionException("Not able to get api client(2)", e);
		}
		
		/* quick and dirty test if it works:*/
		CoreV1Api api = new CoreV1Api();
		V1PodList list;
		try {
			list = api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null);
		} catch (ApiException e) {
			throw new ExecutionException("cannot list pods",e);
		}
		for (V1Pod item : list.getItems()) {
			System.out.println(item.getMetadata().getName());
		}
		return null;
	}
}
