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

import java.util.Set;

import de.jcup.ekube.core.ExecutionParameters;

public interface EKubeElement {

    public String getUid();

    /**
     * @return label, or when no label defined, the name. No name defined
     *         returns a fallback
     */
    public String getLabel();

    public String getName();

    public <T> T execute(EKubeActionIdentifer<T> actionIdentifier);

    public <T> T execute(EKubeActionIdentifer<T> actionIdentifier, ExecutionParameters params);

    public Set<EKubeActionIdentifer<?>> getExecutableActionIdentifiers();

    public EKubeContainer getParent();

    /**
     * @return <code>true</code> when this element is not accessible by current
     *         user
     */
    public boolean isLocked();

    /**
     * @return error message or <code>null</code>
     */
    public String getErrorMessage();

    public Object getTechnicalObject();

    public boolean hasError();

}
