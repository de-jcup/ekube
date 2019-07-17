package de.jcup.ekube.core;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.jcup.ekube.core.model.DeploymentContainer;
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
