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
 package de.jcup.ekube.explorer;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.jcup.ekube.core.model.EKubeContainer;
import de.jcup.ekube.core.model.EKubeElement;
import de.jcup.ekube.core.model.EKubeModel;

class EKubeElementTreeContentProvider implements ITreeContentProvider {
    private final KubernetesExplorer kubernetesExplorer;
    private EKubeModel model;

    EKubeElementTreeContentProvider(KubernetesExplorer kubernetesExplorer) {
        this.kubernetesExplorer = kubernetesExplorer;
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        if (newInput instanceof EKubeModel) {
            this.model = (EKubeModel) newInput;
            return;
        }else{
            this.model=null;
        }
    }

    public Object[] getElements(Object parent) {
        if (parent.equals(this.kubernetesExplorer.getViewSite())) {
            if (model == null) {
                return new Object[] {};
            }
            return new Object[] { model.getCurrentContext() };
        }
        return getChildren(parent);
    }

    public Object getParent(Object child) {
        if (child instanceof EKubeElement) {
            EKubeElement element = (EKubeElement) child;
            return element.getParent();
        }
        return null;
    }

    public Object[] getChildren(Object parent) {
        if (parent instanceof EKubeContainer) {
            EKubeContainer container = (EKubeContainer) parent;
            return container.getChildren().toArray();
        }
        return new Object[0];
    }

    public boolean hasChildren(Object parent) {
        if (parent instanceof EKubeContainer) {
            EKubeContainer container = (EKubeContainer) parent;
            return container.hasChildren();
        }
        return false;
    }

    public EKubeModel getModel() {
        return model;
    }

}