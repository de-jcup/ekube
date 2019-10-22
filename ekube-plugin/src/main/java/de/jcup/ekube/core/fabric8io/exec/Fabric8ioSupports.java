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
 package de.jcup.ekube.core.fabric8io.exec;

import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.fabric8io.exec.pod.PodSupport;
import io.fabric8.kubernetes.client.KubernetesClient;

/**
 * Composite class for all fabric8io support instances. Just create one and have
 * all accessible...
 * 
 * @author Albert Tregnaghi
 *
 */
public class Fabric8ioSupports implements Fabric8ioSupportContext {

    private NetworkSupport networkSupport;
    private PodSupport podSupport;
    private VolumeSupport volumeSupport;
    private ServiceSupport serviceSupport;
    private ConfigMapSupport configMapSupport;
    private NodesSupport nodesSupport;
    private DefaultSupport defaultSupport;
    private SecretsSupport secretsSupport;

    private EKubeContext context;
    private KubernetesClient client;
    private DeploymentsSupport deploymentsSupport;
    private NamespaceSupport namespaceSupport;

    public Fabric8ioSupports(EKubeContext context, KubernetesClient client) {
        this.context = context;
        this.client = client;

        networkSupport = new NetworkSupport(this);
        podSupport = new PodSupport(this);
        volumeSupport = new VolumeSupport(this);
        serviceSupport = new ServiceSupport(this);
        configMapSupport = new ConfigMapSupport(this);
        secretsSupport = new SecretsSupport(this);
        nodesSupport = new NodesSupport(this);
        deploymentsSupport = new DeploymentsSupport(this);
        defaultSupport = new DefaultSupport(this);
        namespaceSupport = new NamespaceSupport(this);
    }

    @Override
    public NamespaceSupport namespaces() {
        return namespaceSupport;
    }

    public DeploymentsSupport deployments() {
        return deploymentsSupport;
    }

    public DefaultSupport defaults() {
        return defaultSupport;
    }

    public NodesSupport nodes() {
        return nodesSupport;
    }

    public NetworkSupport networks() {
        return networkSupport;
    }

    public PodSupport pods() {
        return podSupport;
    }

    public VolumeSupport volumes() {
        return volumeSupport;
    }

    public ServiceSupport services() {
        return serviceSupport;
    }

    public ConfigMapSupport configMaps() {
        return configMapSupport;
    }

    @Override
    public EKubeContext getContext() {
        return context;
    }

    public SecretsSupport secrets() {
        return secretsSupport;
    }

    @Override
    public KubernetesClient getClient() {
        return client;
    }

}
