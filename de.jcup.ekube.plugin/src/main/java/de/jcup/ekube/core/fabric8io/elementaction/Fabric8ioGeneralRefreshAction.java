package de.jcup.ekube.core.fabric8io.elementaction;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import de.jcup.ekube.core.model.EKubeContainer;
import de.jcup.ekube.core.model.EKubeElement;
import io.fabric8.kubernetes.client.KubernetesClient;

public class Fabric8ioGeneralRefreshAction extends AbstractFabric8ioElementAction<EKubeElement, Object,Void> {

	public Fabric8ioGeneralRefreshAction(EKubeContext context, KubernetesClient client, EKubeElement kubeElement, Object technicalObject) {
		super(context,client, EKubeActionIdentifer.REFRESH, kubeElement, technicalObject);
	}

	@Override
	public Void execute() {
		refreshChildren();
		refreshStatus();
		triggerRefreshActionDownwardsToChildren();
		return null;
	}
	
	private void refreshStatus() {
		kubeElement.execute(EKubeActionIdentifer.REFRESH_STATUS);
	}

	protected void refreshChildren(){
		kubeElement.execute(EKubeActionIdentifer.REFRESH_CHILDREN);
	}

	protected void triggerRefreshActionDownwardsToChildren() {
		if (this.kubeElement instanceof EKubeContainer){
			EKubeContainer container = (EKubeContainer) this.kubeElement;
			for (EKubeElement element: container.getChildren()){
				element.execute(EKubeActionIdentifer.REFRESH);
			}
		}
	}

}
