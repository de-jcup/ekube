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
import java.io.IOException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PartInitException;

import de.jcup.ekube.Activator;
import de.jcup.ekube.ResourceUtil;
import de.jcup.ekube.core.FileUtil;
import de.jcup.ekube.core.fabric8io.plantuml.ModelToPlantUMLConverter;
import de.jcup.ekube.core.model.EKubeModel;

public class ShowPlantUMLAction extends Action {
    private KubernetesExplorerActionGroup kubernetesExplorerActionGroup;
    
    ShowPlantUMLAction(KubernetesExplorerActionGroup kubernetesExplorerActionGroup) {
        this.kubernetesExplorerActionGroup = kubernetesExplorerActionGroup;
    }
    
    @Override
    public void run() {
        Activator activator = Activator.getDefault();

        EKubeModel model = activator.getExplorer().getModel();
        if (model == null) {
            MessageDialog.openInformation(null, "No model available", "To show plantuml information you must first switch to a context to use!");
            return;
        }
        try {
            ModelToPlantUMLConverter converter = new ModelToPlantUMLConverter();
            String plantUML = converter.convert(model);

            File file = File.createTempFile("ekube_model_to_plantuml", ".plantuml");
            FileUtil.writeTextFile(plantUML, file);

            ResourceUtil.openInEditor(file);

        } catch (IOException e) {
            activator.getErrorHandler().logError("Problems on conversion to plantuml", e);
        } catch (PartInitException e) {
            activator.getErrorHandler().logError("Problems on showing generated plantuml file", e);
        }
    }
}
