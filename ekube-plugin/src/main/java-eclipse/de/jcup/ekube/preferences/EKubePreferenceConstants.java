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

/**
 * Constant definitions for plug-in preferences
 */
public enum EKubePreferenceConstants implements PreferenceIdentifiable {

    KUBE_CONFIGFILE_PATH("kubeConfigFilePath"),

    FILTER_NAMESPACES_ENABLED("filterNamespacesEnabled"),

    FILTERED_NAMESPACES("filteredNameSpaces"), 
    
    CONTEXT_NAMESPACE_ONLY_ENABLED("contextNamespaceOnly"),
    
    LOG_LINES_TO_FETCH("logLines"),
    
    SHELL_EXECUTOR_LAUNCH_COMMAND("shellExecutorLaunchCommand"), 
    
    SHELL_EXECUTOR_INTERACTIVE_SHELL_COMMAND("shellExecutorInteractiveShellCommandV2"),
    
    SHELL_EXECUTOR_INTERACTIVE_LOGVIEWER_COMMAND("shellExecutorInteractiveLogViewerCommandV2"),

    SHELL_EXECUTOR_SET_TITLE_COMMAND("shellExecutorSetTitleCommand"),
    ;

    private String id;

    private EKubePreferenceConstants(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
