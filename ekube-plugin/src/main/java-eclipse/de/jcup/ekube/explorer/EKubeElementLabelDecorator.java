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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;

import de.jcup.eclipse.commons.ui.EclipseUtil;
import de.jcup.ekube.Activator;
import de.jcup.ekube.core.model.EKubeElement;

public class EKubeElementLabelDecorator extends LabelProvider implements ILightweightLabelDecorator {

    private static ImageDescriptor DESC_LOCK = EclipseUtil.createImageDescriptor("/icons/ovr/lock_ovr.png", Activator.PLUGIN_ID);
    private static ImageDescriptor DESC_ERROR = EclipseUtil.createImageDescriptor("/icons/ovr/error_ovr.png", Activator.PLUGIN_ID);

    @Override
    public void decorate(Object element, IDecoration decoration) {
        if (!(element instanceof EKubeElement)) {
            return;
        }

        EKubeElement ekubeElement = (EKubeElement) element;
        if (ekubeElement.isLocked()) {
            decoration.addOverlay(DESC_LOCK, IDecoration.BOTTOM_RIGHT);
            /* when locked we do not show any other decoration - e.g. errors*/
            return;
        }
        if (ekubeElement.hasError()){
            decoration.addOverlay(DESC_ERROR, IDecoration.BOTTOM_LEFT);
        }
    }

}
