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
    
    SHELL_EXECUTOR_INTERACTIVE_SHELL_COMMAND("shellExecutorInteractiveShellCommand"),
    
    SHELL_EXECUTOR_INTERACTIVE_LOGVIEWER_COMMAND("shellExecutorInteractiveLogViewerCommand"),

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
