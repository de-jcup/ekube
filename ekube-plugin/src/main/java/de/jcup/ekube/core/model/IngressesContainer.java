/*
 * Copyright 2020 Albert Tregnaghi
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

public class IngressesContainer extends AbstractEKubeContainer implements SyntheticKubeElement {

    public IngressesContainer() {
        super(null, null);// no uid available - because synthetic element which
                          // is not existing in kubernetes
        label = "Ingress list";
    }

    public IngressContainer addOrReuseExisting(IngressContainer node) {
       return super.internalAddOrReuseExisting(node);
    }
    
    public List<IngressContainer> getNodes(){
        return fetchAllChildrenOfType(IngressContainer.class);
    }
}
