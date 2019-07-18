package de.jcup.ekube.preferences;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import de.jcup.ekube.Activator;
import de.jcup.ekube.core.DefaultEKubeConfiguration;
import de.jcup.ekube.core.process.ShellExecutor;

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
        store.setDefault(EKubePreferenceConstants.SHELL_EXECUTOR_LAUNCH_COMMAND.getId(), ShellExecutor.resolveOSDefaultLaunchCommand());
        store.setDefault(EKubePreferenceConstants.SHELL_EXECUTOR_INTERACTIVE_SHELL_COMMAND.getId(), ShellExecutor.resolveDefaultInteractiveShellCommand());
        store.setDefault(EKubePreferenceConstants.SHELL_EXECUTOR_INTERACTIVE_LOGVIEWER_COMMAND.getId(), ShellExecutor.resolveDefaultInteractiveLogViewerCommand());
        store.setDefault(EKubePreferenceConstants.SHELL_EXECUTOR_SET_TITLE_COMMAND.getId(), ShellExecutor.resolveDefaultTitleCommand());
        
    }

}
