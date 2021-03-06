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
import de.jcup.ekube.core.fabric8io.exec.configmap.ConfigMapSupport;
import de.jcup.ekube.core.fabric8io.exec.deployment.DeploymentsSupport;
import de.jcup.ekube.core.fabric8io.exec.node.NodesSupport;
import de.jcup.ekube.core.fabric8io.exec.pod.PodSupport;
import de.jcup.ekube.core.fabric8io.exec.secret.SecretsSupport;
import de.jcup.ekube.core.fabric8io.exec.service.ServiceSupport;
import de.jcup.ekube.core.fabric8io.exec.volume.VolumeSupport;
import io.fabric8.kubernetes.client.KubernetesClient;

/**
 * A context which provides different support objects and access to to ekube context and client
 * @author albert
 *
 */
public interface Fabric8ioSupportContext {

    public EKubeContext getContext();

    public KubernetesClient getClient();

    public DeploymentsSupport deployments();

    public DefaultSupport defaults();

    public NodesSupport nodes();

    public NetworkSupport networks();

    public PodSupport pods();

    public VolumeSupport volumes();

    public ServiceSupport services();

    public ConfigMapSupport configMaps();

    public NamespaceSupport namespaces();

    public SecretsSupport secrets();
}
