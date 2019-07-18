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

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import de.jcup.eclipse.commons.ui.EclipseUtil;
import de.jcup.ekube.Activator;
import de.jcup.ekube.core.KubernetesContextInfo;

public class EKubeSwitchContextConfigurationLabelProvider extends LabelProvider {

    public String getText(Object element) {
        if (element instanceof KubernetesContextInfo) {
            StringBuilder sb = new StringBuilder();
            KubernetesContextInfo container = (KubernetesContextInfo) element;
            sb.append(container.getName());
            sb.append("   [");
            sb.append("cluster: ");
            sb.append(container.getCluster());
            sb.append(",");
            sb.append("user: ");
            sb.append(container.getUser());
            if (container.getNamespace()!=null){
                sb.append(",");
                sb.append("ns: ");
                sb.append(container.getNamespace());
            }
            sb.append("]");
            return sb.toString();
        }
        return null;
    
    }
    @Override
    public Image getImage(Object element) {
        if (element instanceof KubernetesContextInfo){
            return EclipseUtil.getImage("/icons/model/context.png", Activator.getDefault());
        }
        return null;
    }
   
}
