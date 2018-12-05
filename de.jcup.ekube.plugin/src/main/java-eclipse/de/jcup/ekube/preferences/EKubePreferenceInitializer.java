package de.jcup.ekube.preferences;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import de.jcup.ekube.Activator;
import de.jcup.ekube.core.DefaultEKubeConfiguration;

/**
 * Class used to initialize default preference values.
 */
public class EKubePreferenceInitializer extends AbstractPreferenceInitializer {

    public void initializeDefaultPreferences() {
        DefaultEKubeConfiguration defaults = new DefaultEKubeConfiguration();
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        store.setDefault(EKubePreferenceConstants.KUBE_CONFIGFILE_PATH.getId(), defaults.getKubeConfigFile().getAbsolutePath());
        store.setDefault(EKubePreferenceConstants.FILTERED_NAMESPACES.getId(), StringUtils.join(defaults.getFilteredNamespaces(), "\n"));
        store.setDefault(EKubePreferenceConstants.FILTER_NAMESPACES_ENABLED.getId(), true);
        store.setDefault(EKubePreferenceConstants.CONTEXT_NAMESPACE_ONLY_ENABLED.getId(), true);
        store.setDefault(EKubePreferenceConstants.LOG_LINES_TO_FETCH.getId(), 20);
    }

}
