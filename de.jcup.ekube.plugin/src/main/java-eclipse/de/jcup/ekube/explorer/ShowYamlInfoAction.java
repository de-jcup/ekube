package de.jcup.ekube.explorer;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import de.jcup.ekube.Activator;
import de.jcup.ekube.ResourceUtil;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import de.jcup.ekube.core.model.EKubeElement;

final class ShowYamlInfoAction extends Action {
    /**
     * 
     */
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

                    File tmpfile = File.createTempFile("ekube_info_"+eelement.getName(), ".yaml");
                    tmpfile.deleteOnExit();

                    try (FileWriter fw = new FileWriter(tmpfile)) {
                        StringBuilder yaml = new StringBuilder();
                        yaml.append("# ---------------------------------------------------------------------------\n");
                        yaml.append("# EKube info about : ").append(eelement.getClass().getSimpleName()).append(" '").append(eelement.getLabel())
                                .append("'\n");
                        yaml.append("# Timestamp        : ").append(this.kubernetesExplorerActionGroup.explorer.getDateFormat().format(new Date()))
                                .append("\n");
                        yaml.append("# ---------------------------------------------------------------------------\n");
                        yaml.append(info);
                        fw.write(yaml.toString());
                        fw.close();
                        /*
                         * loading temporary file from outside eclipse workspace
                         * :
                         */
                        ResourceUtil.openInEditor(tmpfile);
                        return;
                    }
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
}