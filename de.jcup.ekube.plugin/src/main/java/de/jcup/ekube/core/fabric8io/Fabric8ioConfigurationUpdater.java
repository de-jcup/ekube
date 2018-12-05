package de.jcup.ekube.core.fabric8io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.jcup.ekube.core.EKubeConfiguration;
import de.jcup.ekube.core.EKubeContext;
import de.jcup.ekube.core.EKubeContextConfigurationEntry;
import io.fabric8.kubernetes.api.model.Config;
import io.fabric8.kubernetes.api.model.Context;
import io.fabric8.kubernetes.api.model.NamedContext;
import io.fabric8.kubernetes.client.internal.KubeConfigUtils;

/**
 * This updater will upate only EKubeConfiguration - and NEVER the original
 * KubeConfiguration.
 * 
 * @author Albert Tregnaghi
 *
 */
public class Fabric8ioConfigurationUpdater {

    /**
     * Updates list of available kubernetes contexts, does also setup current
     * context if not already set in configuration
     * 
     * @param ekubeContext
     */
    public void update(EKubeContext ekubeContext) {
        EKubeConfiguration configuration = ekubeContext.getConfiguration();
        List<EKubeContextConfigurationEntry> list = new ArrayList<>();
        try {
            Config config = KubeConfigUtils.parseConfig(configuration.getKubeConfigFile());
            String currentContext = config.getCurrentContext();
            if (configuration.getKubernetesContext() == null) {
                configuration.setKubernetesContext(currentContext);
            }

            List<NamedContext> namedContexts = config.getContexts();

            for (NamedContext namedContext : namedContexts) {
                Context context = namedContext.getContext();

                /*
                 * we build context container - does only contain some
                 * properties thats all - no children
                 */
                EKubeContextConfigurationEntry entry = new EKubeContextConfigurationEntry();
                String contextName = namedContext.getName();
                entry.setName(contextName);
                entry.setUser(context.getUser());
                entry.setNamespace(context.getNamespace());
                entry.setCluster(context.getCluster());

                list.add(entry);
            }

        } catch (IOException e) {
            ekubeContext.getErrorHandler().logError("Was not able to fetch context definitions", e);
        }
        configuration.updateEntries(list);
    }
}
