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

import java.util.List;

import de.jcup.ekube.core.KubernetesContextInfo;

public class CurrentContextContainer extends AbstractContextContainer implements SyntheticKubeElement, KubernetesContextInfo {

    private String namespace;

    public CurrentContextContainer() {
        super(null);// no uid available - because synthetic element which is not
                    // existing in kubernetes
        addChild(new NodesContainer());
        addChild(new PersistentVolumesContainer());
    }

    public void add(NamespaceContainer namespaceContainer) {
        addChild(namespaceContainer);
    }
    
    public void add(IngressesContainer ingressesContainer) {
        addChild(ingressesContainer);
    }

    public List<NamespaceContainer> getNamespaces() {
        return fetchAllChildrenOfType(NamespaceContainer.class);
    }

    public NodesContainer getNodesContainer() {
        return fetchAllChildrenOfType(NodesContainer.class).iterator().next();
    }

    public PersistentVolumesContainer fetchPersistentVolumesContainer() {
        /* always available, no check necessary */
        return fetchAllChildrenOfType(PersistentVolumesContainer.class).iterator().next();
    }
    
    public void setNamespace(String namespace) {
       this.namespace=namespace;
    }
    
    public String getNamespace() {
        return namespace;
    }

}
