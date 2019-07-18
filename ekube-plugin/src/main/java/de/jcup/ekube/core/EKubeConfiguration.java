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
import java.util.List;

public interface EKubeConfiguration {

    File getKubeConfigFile();

    void updateEntries(List<EKubeContextConfigurationEntry> entries);

    /**
     * Set current kubernetes context
     * 
     * @param currentContext
     */
    void setKubernetesContext(String currentContext);

    List<EKubeContextConfigurationEntry> getConfigurationContextList();

    /**
     * @return current kubernetes context
     */
    String getKubernetesContext();

    List<String> getFilteredNamespaces();

    /**
     * Finds context configuration entry or <code>null</code>
     * 
     * @param newCurrentContextName
     * @return config or <code>null</code> when not found
     */
    EKubeContextConfigurationEntry findContextConfigurationEntry(String newCurrentContextName);

    boolean isNamespaceFiltered(String namespaceName);

    boolean isNamespaceFilteringEnabled();

    boolean isContextNamespaceOnly();

}