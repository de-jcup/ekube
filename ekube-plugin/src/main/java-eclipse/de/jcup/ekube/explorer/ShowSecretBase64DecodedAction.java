package de.jcup.ekube.explorer;

import org.eclipse.jface.action.Action;

import de.jcup.eclipse.commons.ui.EclipseUtil;
import de.jcup.ekube.core.KeyValueMap;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import de.jcup.ekube.core.model.EKubeElement;
import de.jcup.ekube.core.model.SecretElement;

final class ShowSecretBase64DecodedAction extends Action {
    private final KubernetesExplorerActionGroup kubernetesExplorerActionGroup;

    ShowSecretBase64DecodedAction(KubernetesExplorerActionGroup kubernetesExplorerActionGroup) {
        this.kubernetesExplorerActionGroup = kubernetesExplorerActionGroup;
    }

    public void run() {
        Object obj = this.kubernetesExplorerActionGroup.explorer.getFirstSelectedElement();
        if (obj instanceof SecretElement) {
            EKubeElement eelement = (EKubeElement) obj;
            KeyValueMap keyValueMap = eelement.execute(EKubeActionIdentifer.FETCH_KEY_VALUE);
            if (keyValueMap==null){
                keyValueMap=new KeyValueMap();
            }
            ShowBase64EncodedSecretDataDialog dialog = new ShowBase64EncodedSecretDataDialog(EclipseUtil.getSafeDisplay().getActiveShell());
            dialog.setKeyValueMap(keyValueMap);
            dialog.open();
        }

    }
}