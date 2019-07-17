package de.jcup.ekube.explorer;

import org.eclipse.ui.views.framelist.TreeFrame;
import org.eclipse.ui.views.framelist.TreeViewerFrameSource;

class KubernetesFrameSource extends TreeViewerFrameSource {
    private KubernetesExplorer kubernetesExplorer;

    KubernetesFrameSource(KubernetesExplorer explorer) {
        super(explorer.getTreeViewer());
        kubernetesExplorer = explorer;
    }

    @Override
    protected TreeFrame createFrame(Object input) {
        TreeFrame frame = super.createFrame(input);
        frame.setName(kubernetesExplorer.getFrameName(input));
        frame.setToolTipText(kubernetesExplorer.getToolTipText(input));
        return frame;
    }
}
