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
 package de.jcup.ekube.core.model;

public class EKubeModelToStringDumpConverter {

    public String convert(EKubeModel model) {
        CurrentContextContainer context = model.getCurrentContext();
        StringBuilder sb = new StringBuilder();
        output(context, 0, sb);

        return sb.toString();
    }

    private void output(EKubeElement element, int indent, StringBuilder sb) {
        for (int i = 0; i < indent; i++) {
            sb.append(" ");
        }
        sb.append(element.toString());
        if (element instanceof EKubeStatusElement) {
            sb.append("[");
            EKubeStatusElement se = (EKubeStatusElement) element;
            sb.append(se.getStatus());
            sb.append("]");
        }
        sb.append("\n");

        if (element instanceof EKubeContainer) {
            EKubeContainer container = (EKubeContainer) element;
            for (EKubeElement child : container.getChildren()) {
                output(child, indent + 1, sb);
            }
        }
    }

}
