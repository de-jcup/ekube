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

/**
 * Model containing relevant data usable for ui and information gathering. Will
 * be created by dedicated - maybe specific (k8jc/fabric8client) plementations
 * of builders/factories.
 * 
 * @author Albert Tregnaghi
 *
 */
public class EKubeModel {

    private CurrentContextContainer currentContextContainer;

    public EKubeModel() {
        this.currentContextContainer = new CurrentContextContainer();
        this.currentContextContainer.setName("<no context selected>"); // just
                                                                       // initial
                                                                       // name+label
    }

    public CurrentContextContainer getCurrentContext() {
        return currentContextContainer;
    }

}
