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

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import de.jcup.ekube.core.model.EKubeElement;

public class ExecutionParameters {

    private Map<Class<?>, Object> objectMap = new HashMap<>();

    private Set<EKubeElement> children = new LinkedHashSet<EKubeElement>();

    public ExecutionParameters setChildren(Set<EKubeElement> children) {
        this.children.clear();
        if (children != null) {
            this.children.addAll(children);
        }
        return this;
    }

    public <T> T get(Class<T> clazz) {
        return (T) objectMap.get(clazz);
    }

    public <T> ExecutionParameters set(Class<T> clazz, T object) {
        objectMap.put(clazz, object);
        return this;
    }

    public Set<EKubeElement> getChildren() {
        return children;
    }

    public boolean isHandling(EKubeElement element) {
        if (children.isEmpty()) {
            /* nothing defined, so always allowed */
            return true;
        }
        /* only allowed when contained */
        return children.contains(element);
    }

}
