/*
 * Copyright 2019 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
 package de.jcup.ekube.explorer.action;

import org.eclipse.jface.action.Action;

import de.jcup.eclipse.commons.ui.EclipseUtil;
import de.jcup.ekube.core.KeyValueMap;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import de.jcup.ekube.core.model.EKubeElement;
import de.jcup.ekube.core.model.SecretElement;
import de.jcup.ekube.explorer.ShowBase64EncodedSecretDataDialog;

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