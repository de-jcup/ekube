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
 package de.jcup.ekube.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DefaultEKubeConfiguration extends AbstractEKubeConfiguration implements EKubeConfiguration {

    private List<String> filteredNamespaces = new ArrayList<>();

    private File kubeConfigFile;

    private boolean namespaceFilteringEnabled;

    public DefaultEKubeConfiguration() {
        setDefaults();
    }

    private void setDefaults() {
        kubeConfigFile = EKubeFiles.getDefaultKubeConfigFile();
        filteredNamespaces.add("kube-system");
    }

    @Override
    public File getKubeConfigFile() {
        return kubeConfigFile;
    }

    @Override
    public List<String> getFilteredNamespaces() {
        return filteredNamespaces;
    }

    public void setNamespaceFilteringEnabled(boolean namespaceFilteringEnabled) {
        this.namespaceFilteringEnabled = namespaceFilteringEnabled;
    }

    @Override
    public boolean isNamespaceFilteringEnabled() {
        return namespaceFilteringEnabled;
    }
    
}
