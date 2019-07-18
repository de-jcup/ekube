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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.jcup.ekube.core.ExecutionParameters;

public abstract class AbstractEKubeContainer extends AbstractEKubeElement implements EKubeContainer {

    public AbstractEKubeContainer(String uid, Object technicalObject) {
        super(uid, technicalObject);
    }

    private List<EKubeElement> children = new ArrayList<>();

    /**
     * Adds kubeElement to children list. Will also set this as parent for the
     * child.
     * 
     * @param kubeElement
     */
    protected void addChild(EKubeElement kubeElement) {
        children.add(kubeElement);
        if (kubeElement instanceof AbstractEKubeElement) {
            AbstractEKubeElement abstractElement = (AbstractEKubeElement) kubeElement;
            abstractElement.parent = this;
        }
    }
    
    protected void removeAllChildren(Class<?> clazz) {
        List<?> childrenToRemove = fetchAllChildrenOfType(clazz);
        for (Object object: childrenToRemove){
            removeChild((EKubeElement) object);
        }
        
    }

    @SuppressWarnings("unchecked")
    <E extends EKubeElement> E internalAddOrReuseExisting(E newElement) {
        for (EKubeElement existing : children) {
            if (existing.equals(newElement)) {
                orphans.remove(existing);
                if (existing instanceof AbstractEKubeElement) {
                    AbstractEKubeElement abstractElement = (AbstractEKubeElement) existing;
                    abstractElement.setTechnicalObject(newElement.getTechnicalObject());
                }
                return (E) existing;
            }
        }
        addChild(newElement);
        return newElement;
    }

    private Set<EKubeElement> orphans = new HashSet<>();

    /**
     * Marks all children as being an orphan ! Use
     * {@link #internalAddOrReuseExisting(EKubeElement)} to unmark them
     * 
     * @param parameters
     *            - when children ar set in parameters, only those children will
     *            be handled as potential orphans
     */
    public void startOrphanCheck(ExecutionParameters parameters) {
        orphans.clear();
        Collection<EKubeElement> toInspect = parameters.getChildren();
        if (toInspect.isEmpty()) {
            toInspect = children;
        }
        orphans.addAll(toInspect);
    }

    /**
     * Removes all children being orphans
     */
    public void removeOrphans() {
        for (EKubeElement element : orphans) {
            removeChild(element);
        }
    }

    protected void removeChild(EKubeElement element) {
        if (element instanceof AbstractEKubeElement) {
            AbstractEKubeElement abstractElement = (AbstractEKubeElement) element;
            abstractElement.parent = null;
        }
        children.remove(element);

    }

    @Override
    public List<EKubeElement> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public boolean hasChildren() {
        return !children.isEmpty();
    }

    @SuppressWarnings("unchecked")
    protected <T> List<T> fetchAllChildrenOfType(Class<T> clazz) {
        List<T> list = new ArrayList<>();
        for (EKubeElement element : children) {
            if (clazz.isAssignableFrom(element.getClass())) {
                list.add((T) element);
            }
        }
        return list;
    }
}
