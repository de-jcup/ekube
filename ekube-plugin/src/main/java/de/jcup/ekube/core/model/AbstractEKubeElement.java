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

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import de.jcup.ekube.core.ExecutionParameters;

public abstract class AbstractEKubeElement implements EKubeElement {

    protected String label;
    protected String name;

    private Map<EKubeActionIdentifer<?>, EKubeElementAction<?>> actions = new TreeMap<>();
    EKubeContainer parent;
    private boolean locked;
    private String errorMessage;

    private String uid;
    private Object technicalObject;

    public AbstractEKubeElement(String uid, Object technicalObject) {
        this.uid = uid;
        this.technicalObject = technicalObject;
        if (uid == null) {
            /* fall back uid for synthetic parts */
            uid = "fallback:" + getClass().getName() + "#" + super.hashCode();
        }
    }

    public void setTechnicalObject(Object metaData) {
        this.technicalObject = metaData;
    }

    @Override
    public Object getTechnicalObject() {
        return technicalObject;
    }

    @Override
    public String getUid() {
        return uid;
    }

    @Override
    public EKubeContainer getParent() {
        return parent;
    }

    @Override
    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    /**
     * This sets directly the label. Normally you do NOT have to use this method
     * when your label shall be same as name.
     * 
     * @param text
     */
    public void setLabel(String text) {
        this.label = text;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getLabel() {
        if (label != null) {
            return label;
        }
        if (name != null) {
            return name;
        }
        return getClass().getSimpleName() + hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + getLabel();
    }

    public <T> T execute(EKubeActionIdentifer<T> actionIdentifier) {
        return execute(actionIdentifier, null);
    }

    @Override
    public <T> T execute(EKubeActionIdentifer<T> actionIdentifier, ExecutionParameters params) {
        @SuppressWarnings("unchecked") // ugly but necessary -currently no other
                                       // way to have fluent
        EKubeElementAction<T> action = (EKubeElementAction<T>) actions.get(actionIdentifier);
        if (action == null) {
            return null;
        }
        return action.execute(params);
    }

    /**
     * Registers an action for its identifier. Attention: an action with same
     * identifier already registered will be removed! If you want to have
     * multiple actions executed for same identifier please create a composite
     * action cotaining the others...
     * 
     * @param action
     */
    public void register(EKubeElementAction<?> action) {
        actions.put(action.getIdentifier(), action);
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    @Override
    public boolean hasError() {
        return errorMessage!=null && !errorMessage.isEmpty();
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public Set<EKubeActionIdentifer<?>> getExecutableActionIdentifiers() {
        return Collections.unmodifiableSet(actions.keySet());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((uid == null) ? 0 : uid.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbstractEKubeElement other = (AbstractEKubeElement) obj;
        if (uid == null) {
            if (other.uid != null)
                return false;
        } else if (!uid.equals(other.uid))
            return false;
        return true;
    }

}
