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

import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import de.jcup.eclipse.commons.ui.EclipseUtil;
import de.jcup.ekube.Activator;
import de.jcup.ekube.core.ExecutionParameters;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import de.jcup.ekube.core.model.EKubeElement;
import de.jcup.ekube.preferences.EKubePreferences;

@Deprecated // reason:we got leaks here - as long as there is no real good concept to avoid
            // watchlog leaks and how to surveillance the logs , editors etc. we only
            // provide the kubectl log approach - it's better, works and is without leaks
final class ShowPodLogAction extends Action {
    /**
     * 
     */
    private final KubernetesExplorerActionGroup kubernetesExplorerActionGroup;

    /**
     * @param kubernetesExplorerActionGroup
     */
    ShowPodLogAction(KubernetesExplorerActionGroup kubernetesExplorerActionGroup) {
        this.kubernetesExplorerActionGroup = kubernetesExplorerActionGroup;
    }

    public void run() {
        Object obj = this.kubernetesExplorerActionGroup.explorer.getFirstSelectedElement();
        if (obj instanceof EKubeElement) {
            EKubeElement eelement = (EKubeElement) obj;
            Job job = new Job("Fetching logs for:" + eelement.getLabel()) {

                @Override
                protected IStatus run(IProgressMonitor monitor) {
                    File info = eelement.execute(EKubeActionIdentifer.FETCH_LOGS, new ExecutionParameters().set(Integer.class, EKubePreferences.getInstance().getLogLinesToFetch()));
                    if (info == null || !info.exists()) {
                        return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "File not found:" + info);
                    }
                    try {
                        IFileStore fileStore = EFS.getLocalFileSystem().getStore(new Path(info.getAbsolutePath()));

                        EclipseUtil.safeAsyncExec(new Runnable() {

                            @Override
                            public void run() {
                                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                                try {
                                    IDE.openEditorOnFileStore(page, fileStore);
                                } catch (PartInitException e) {
                                    Activator.getDefault().getErrorHandler().logError("Was not able to open log", e);
                                }

                            }
                        });

                        return Status.OK_STATUS;
                    } catch (Exception e) {
                        Activator.getDefault().getErrorHandler().logError("Was not able to get log", e);
                        StringBuilder sb = new StringBuilder();
                        sb.append("Failed:");
                        sb.append(e.getClass());
                        sb.append("\nMessage:");
                        sb.append(e.getMessage());
                        sb.append("\n");
                        return new Status(IStatus.ERROR, Activator.PLUGIN_ID, sb.toString(), e);
                    }
                }

            };
            job.setSystem(false);
            job.schedule();

        }

    }
}