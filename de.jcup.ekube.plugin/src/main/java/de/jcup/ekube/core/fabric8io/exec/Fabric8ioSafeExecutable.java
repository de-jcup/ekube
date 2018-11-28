package de.jcup.ekube.core.fabric8io.exec;

import de.jcup.ekube.core.SafeExecutable;
import de.jcup.ekube.core.model.AbstractEKubeElement;
import io.fabric8.kubernetes.client.KubernetesClient;

public interface Fabric8ioSafeExecutable<E extends AbstractEKubeElement,D > extends SafeExecutable<E, KubernetesClient, D>{

}
