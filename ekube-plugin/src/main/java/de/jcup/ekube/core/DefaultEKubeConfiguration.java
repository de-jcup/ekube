package de.jcup.ekube.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DefaultEKubeConfiguration extends AbstractEKubeConfiguration implements EKubeConfiguration {

    private List<String> filteredNamespaces = new ArrayList<>();

    private File kubeConfigFile;

    private boolean namespaceFilteringEnabled;

    private boolean contextNamespaceOnly;

    public DefaultEKubeConfiguration() {
        setDefaults();
    }

    private void setDefaults() {
        kubeConfigFile = new File(System.getProperty("user.home") + "/.kube/config");
        filteredNamespaces.add("kube-system");
    }

    @Override
    public File getKubeConfigFile() {
        return kubeConfigFile;
    }

    @Override
    public List<String> getFilteredNamespaces() {
        return filteredNamespaces;
    }

    public void setNamespaceFilteringEnabled(boolean namespaceFilteringEnabled) {
        this.namespaceFilteringEnabled = namespaceFilteringEnabled;
    }

    @Override
    public boolean isNamespaceFilteringEnabled() {
        return namespaceFilteringEnabled;
    }
    
    public void setContextNamespaceOnly(boolean contextNamespaceOnly) {
        this.contextNamespaceOnly = contextNamespaceOnly;
    }
    
    @Override
    public boolean isContextNamespaceOnly() {
        return contextNamespaceOnly;
    }
}
