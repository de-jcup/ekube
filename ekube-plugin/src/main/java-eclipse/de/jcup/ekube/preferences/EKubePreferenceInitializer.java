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
