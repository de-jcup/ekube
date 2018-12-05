package de.jcup.ekube.core;

public interface KubernetesContextInfo {

    String getName();

    String getUser();

    String getCluster();

    String getNamespace();

}