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