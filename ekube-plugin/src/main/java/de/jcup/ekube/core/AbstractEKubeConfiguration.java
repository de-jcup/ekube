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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public abstract class AbstractEKubeConfiguration implements EKubeConfiguration {
    private String currentContext;
    private List<EKubeContextConfigurationEntry> configurationContextList = new ArrayList<>();

    @Override
    public void updateEntries(List<EKubeContextConfigurationEntry> configurationContextList) {
        this.configurationContextList.clear();
        this.configurationContextList.addAll(configurationContextList);
    }

    @Override
    public void setKubernetesContext(String currentContext) {
        this.currentContext = currentContext;
    }

    @Override
    public String getKubernetesContext() {
        return currentContext;
    }

    @Override
    public List<EKubeContextConfigurationEntry> getConfigurationContextList() {
        return Collections.unmodifiableList(configurationContextList);
    }

    @Override
    public EKubeContextConfigurationEntry findContextConfigurationEntry(String newCurrentContextName) {
        for (EKubeContextConfigurationEntry ecc : configurationContextList) {
            if (StringUtils.equals(ecc.getName(), newCurrentContextName)) {
                return ecc;
            }
        }
        return null;
    }

    @Override
    public boolean isNamespaceFiltered(String namespaceName) {
        if (!isNamespaceFilteringEnabled()) {
            return false;
        }
        return getFilteredNamespaces().contains(namespaceName);
    }

}