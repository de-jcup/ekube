package de.jcup.ekube.core;

public class EKubeContextConfigurationEntry implements KubernetesContextInfo {

    private String name;
    private String user;
    private String cluster;
    private String namespace;

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String getUser() {
        return user;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    @Override
    public String getCluster() {
        return cluster;
    }

    public void setNamespace(String namespace) {
        this.namespace=namespace;
    }
    
    @Override
    public String getNamespace() {
        return namespace;
    }
}
