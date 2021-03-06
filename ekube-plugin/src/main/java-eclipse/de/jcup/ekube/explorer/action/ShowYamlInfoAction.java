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
import java.io.FileWriter;
import java.util.Objects;

import org.eclipse.jface.action.Action;

import de.jcup.ekube.Activator;
import de.jcup.ekube.ResourceUtil;
import de.jcup.ekube.core.EKubeFiles;
import de.jcup.ekube.core.FileUtil;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import de.jcup.ekube.core.model.EKubeElement;

final class ShowYamlInfoAction extends Action {

    private final KubernetesExplorerActionGroup kubernetesExplorerActionGroup;

    /**
     * @param kubernetesExplorerActionGroup
     */
    ShowYamlInfoAction(KubernetesExplorerActionGroup kubernetesExplorerActionGroup) {
        this.kubernetesExplorerActionGroup = kubernetesExplorerActionGroup;
    }

    public void run() {
        Object obj = this.kubernetesExplorerActionGroup.explorer.getFirstSelectedElement();
        if (obj instanceof EKubeElement) {
            EKubeElement eelement = (EKubeElement) obj;
            String info = eelement.execute(EKubeActionIdentifer.SHOW_YAML);
            if (info != null) {
                try {
                    boolean needsToWrite = true;
                    File tmpfile = fetchTmpEditorFile(eelement);

                    String yaml = createYAML(eelement, info);
                    if (tmpfile.exists()) {
                        String origin = FileUtil.readTextFile(tmpfile);
                        if (origin.trim().equals(yaml.trim())) {
                            needsToWrite=false;
                        }
                    }
                    if (needsToWrite) {
                        try (FileWriter fw = new FileWriter(tmpfile)) {
                            /* @formatter:off */
                            fw.write(yaml);
                            fw.close();
                        }
                        
                    }
                    
                    ResourceUtil.openInEditor(tmpfile);
                    
                } catch (Exception e) {
                    Activator.getDefault().getErrorHandler().logError("Was not able to get yaml", e);
                    StringBuilder sb = new StringBuilder();
                    sb.append("Failed:");
                    sb.append(e.getClass());
                    sb.append("\nMessage:");
                    sb.append(e.getMessage());
                    sb.append("\n");
                    this.kubernetesExplorerActionGroup.explorer.showMessage(sb.toString());
                }
            } else {
                /* no meta information available just open */
                kubernetesExplorerActionGroup.explorer.toggleExpandState(eelement);
            }
        }

    }

    private String createYAML(EKubeElement eelement, String info) {
        String name = eelement.getName();
        String label = eelement.getLabel();

        String yaml = "";
        /* @formatter:on */
        yaml +="# ---------------------------------------------------------------------------\n";
        yaml +="# - UUID     : "+ eelement.getUid()+"\n";
        yaml +="# ---------------------------------------------------------------------------\n";
        yaml +="#   Type     : "+ eelement.getClass().getSimpleName()+"\n";
        yaml +="#   Name     : "+ name+"\n";
        if (! Objects.equals(label,name)){
            yaml +="#   Label    : "+ label+"\n";
        }
        yaml +="#\n";
        yaml +="# ---------------------------------------------------------------------------\n";
        yaml +=info;
        return yaml;
    }

    private File fetchTmpEditorFile(EKubeElement eelement) {
        EKubeFiles.getEKubeTempFolder().mkdirs();
        File tmpfile = new File(EKubeFiles.getEKubeTempFolder(),eelement.getUid()+".yaml");
        tmpfile.deleteOnExit();
        return tmpfile;
    }
}