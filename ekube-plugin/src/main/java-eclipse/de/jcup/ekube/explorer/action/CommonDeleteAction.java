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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;

import de.jcup.eclipse.commons.ui.EclipseUtil;
import de.jcup.ekube.Activator;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import de.jcup.ekube.core.model.EKubeElement;
import de.jcup.ekube.core.model.PodContainer;
import de.jcup.ekube.core.model.SecretElement;

final class CommonDeleteAction extends Action {

    private final KubernetesExplorerActionGroup kubernetesExplorerActionGroup;

    CommonDeleteAction(KubernetesExplorerActionGroup kubernetesExplorerActionGroup) {
        this.kubernetesExplorerActionGroup = kubernetesExplorerActionGroup;
    }

    public void run() {
        Object obj = this.kubernetesExplorerActionGroup.explorer.getFirstSelectedElement();
        if (obj instanceof EKubeElement) {
            EKubeElement eelement = (EKubeElement) obj;
            boolean result = MessageDialog.openConfirm(EclipseUtil.getActiveWorkbenchShell(), "Are you sure?",  "Do you really want to delete selected kube element ?");
            if (!result) {
                return;
            }
            Job job = new Job("Fetching logs for:" + eelement.getLabel()) {

                @Override
                protected IStatus run(IProgressMonitor monitor) {
                    String info = eelement.execute(EKubeActionIdentifer.DELETE);
                    if (info == null ) {
                        return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "No result:" + info);
                    }
                    EclipseUtil.safeAsyncExec(new Runnable() {
                        
                        @Override
                        public void run() {
                            MessageDialog.openInformation(EclipseUtil.getActiveWorkbenchShell(), "Result", info);
                        }
                    });
                    
                    return Status.OK_STATUS;
                }

            };
            job.setSystem(false);
            job.schedule();

        }

    }

    public boolean canDelete(EKubeElement eke) {
        if (eke instanceof SecretElement) {
            return true;
        }
        if (eke instanceof PodContainer) {
            return true;
        }
        return false;
    }
}