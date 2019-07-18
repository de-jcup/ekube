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

public class NamespaceContainer extends AbstractEKubeContainer {

    public NamespaceContainer(String uid, Object technialObject) {
        super(uid, technialObject);
        addChild(new ServicesContainer());
        addChild(new DeploymentsContainer());
        addChild(new PodsContainer());
        addChild(new PersistentVolumeClaimesContainer());
        addChild(new NetworksContainer());
        addChild(new ConfigMapsContainer());
        addChild(new SecretsContainer());
    }

    public NetworksContainer fetchNetworksContainer() {
        /* always available, no check necessary */
        return fetchAllChildrenOfType(NetworksContainer.class).iterator().next();
    }
    
    public PersistentVolumeClaimesContainer fetchPersistentVolumeClaimsContainer() {
        /* always available, no check necessary */
        return fetchAllChildrenOfType(PersistentVolumeClaimesContainer.class).iterator().next();
    }

    public ConfigMapsContainer fetchConfigMapsContainer() {
        /* always available, no check necessary */
        return fetchAllChildrenOfType(ConfigMapsContainer.class).iterator().next();
    }

    public PodsContainer fetchPodsContainer() {
        /* always available, no check necessary */
        return fetchAllChildrenOfType(PodsContainer.class).iterator().next();
    }

    public ServicesContainer fetchServicesContainer() {
        /* always available, no check necessary */
        return fetchAllChildrenOfType(ServicesContainer.class).iterator().next();
    }

    public SecretsContainer fetchSecretsContainer() {
        /* always available, no check necessary */
        return fetchAllChildrenOfType(SecretsContainer.class).iterator().next();
    }

    public DeploymentsContainer fetchDeploymentsContainer() {
        /* always available, no check necessary */
        return fetchAllChildrenOfType(DeploymentsContainer.class).iterator().next();

    }

   

}
