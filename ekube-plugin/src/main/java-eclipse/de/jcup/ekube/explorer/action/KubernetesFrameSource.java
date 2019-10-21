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

import org.eclipse.ui.views.framelist.TreeFrame;
import org.eclipse.ui.views.framelist.TreeViewerFrameSource;

import de.jcup.ekube.explorer.KubernetesExplorer;

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
