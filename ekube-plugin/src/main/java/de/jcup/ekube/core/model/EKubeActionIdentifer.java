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

import java.io.File;

import org.eclipse.jface.resource.ImageDescriptor;

import de.jcup.eclipse.commons.ui.EclipseUtil;
import de.jcup.ekube.Activator;
import de.jcup.ekube.core.KeyValueMap;

public class EKubeActionIdentifer<R> implements Comparable<EKubeActionIdentifer<?>>{

    public static final EKubeActionIdentifer<Void> REFRESH = new EKubeActionIdentifer<>("Refresh", Void.class).imageDescriptor("/icons/refresh.png").markDirectlyExecutable()
            .markRefreshNecessary();
    public static final EKubeActionIdentifer<Void> REFRESH_STATUS = new EKubeActionIdentifer<>("Refresh status", Void.class);
    public static final EKubeActionIdentifer<Void> REFRESH_CHILDREN = new EKubeActionIdentifer<>("Refresh children", Void.class);

    public static final EKubeActionIdentifer<String> DELETE = new EKubeActionIdentifer<>("Delete", String.class).markRefreshNecessary();
    public static final EKubeActionIdentifer<String> SHOW_YAML = new EKubeActionIdentifer<>("Show Yaml", String.class).markDirectlyExecutable();
    public static final EKubeActionIdentifer<KeyValueMap> FETCH_KEY_VALUE = new EKubeActionIdentifer<>("Fetch KeyValue Map", KeyValueMap.class);
    
    public static final EKubeActionIdentifer<File> FETCH_LOGS = new EKubeActionIdentifer<>("Grab log output", File.class).markDirectlyExecutable();
    public static final EKubeActionIdentifer<Void> INTERACTIVE_SHELL = new EKubeActionIdentifer<>("Start interactive shell (kubectl)", Void.class).markDirectlyExecutable().imageDescriptor("/icons/interactive-shell.png");
    public static final EKubeActionIdentifer<Void> INTERACTIVE_LOGVIEWER = new EKubeActionIdentifer<>("Show log tail (kubectl)", Void.class).markDirectlyExecutable().imageDescriptor("/icons/interactive-log.png");


    private String label;

    private boolean refreshNecessary;

    private boolean directExecutable;
    private ImageDescriptor imageDescriptor;

    private EKubeActionIdentifer(String label, Class<R> resultClass) {
        this.label = label;
    }

    private EKubeActionIdentifer<R> imageDescriptor(String path) {
        this.imageDescriptor = EclipseUtil.createImageDescriptor(path, Activator.PLUGIN_ID);
        return this;
    }

    public ImageDescriptor getImageDescriptor() {
        return imageDescriptor;
    }
    
    public String getLabel() {
        return label;
    }

    private EKubeActionIdentifer<R> markRefreshNecessary() {
        this.refreshNecessary = true;
        return this;
    }

    public boolean isRefreshNecessary() {
        return refreshNecessary;
    }

    private EKubeActionIdentifer<R> markDirectlyExecutable() {
        this.directExecutable = true;
        return this;
    }

    public boolean isVisibleForUser() {
        return directExecutable;
    }

    @Override
    public String toString() {
        return "ActionIdentifier:" + label;
    }

    @Override
    public int compareTo(EKubeActionIdentifer<?> o) {
        if (o==null) {
            return 1;
        }
        String label = o.getLabel();
        if (label==null) {
            return 1;
        }
        String myLabel = getLabel();
        if (myLabel==null) {
            return -1;
        }
        return myLabel.compareTo(label);
    }
}
