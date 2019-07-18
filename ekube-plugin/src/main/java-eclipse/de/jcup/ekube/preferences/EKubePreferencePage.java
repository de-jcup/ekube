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

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.jcup.ekube.Activator;
import de.jcup.ekube.core.process.ShellExecutor;

public class EKubePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    private StringFieldEditor shellExecutorLaunchCommand;
    private StringFieldEditor shellExecutorInteractiveShellCommand;
    private StringFieldEditor shellExecutorInteractiveLogviewerCommand;
    private StringFieldEditor shellExecutorTitleCommand;

    public EKubePreferencePage() {
        super(GRID);
        setPreferenceStore(Activator.getDefault().getPreferenceStore());
        setDescription("Preferences for kubernetes communication");
    }

    @Override
    public boolean performOk() {

        boolean ok = super.performOk();
        if (ok) {
            String launcherCommand = shellExecutorLaunchCommand.getStringValue();
            String interactiveShellCommand = shellExecutorInteractiveShellCommand.getStringValue();
            String interactiveLogViewerCommand = shellExecutorInteractiveLogviewerCommand.getStringValue();
            String setTitleCommand = shellExecutorTitleCommand.getStringValue();

            ShellExecutor executor = ShellExecutor.INSTANCE;
            executor.setLauncherCommand(launcherCommand);
            executor.setInteractiveShellCommand(interactiveShellCommand);
            executor.setInteractiveLogViewerCommand(interactiveLogViewerCommand);
            executor.setTitleCommand(setTitleCommand);
        }
        return ok;
    }

    @Override
    protected void performDefaults() {
        super.performDefaults();
    }
    
    /**
     * Creates the field editors. Field editors are abstractions of the common GUI
     * blocks needed to manipulate various types of preferences. Each field editor
     * knows how to save and restore itself.
     */
    public void createFieldEditors() {
        addField(new FileFieldEditor(EKubePreferenceConstants.KUBE_CONFIGFILE_PATH.getId(), "&KubeConfig:", getFieldEditorParent()));
        addField(new BooleanFieldEditor(EKubePreferenceConstants.FILTER_NAMESPACES_ENABLED.getId(), "&Filter namespaces enabled", getFieldEditorParent()));

        // addField(new RadioGroupFieldEditor(EKubePreferenceConstants.P_CHOICE,
        // "An example of a multiple-choice preference",
        // 1, new String[][] { { "&Choice 1", "choice1" }, { "C&hoice 2",
        // "choice2" } }, getFieldEditorParent()));
        StringFieldEditor filteredNamespacesEditor = new StringFieldEditor(EKubePreferenceConstants.FILTERED_NAMESPACES.getId(), "Filtered namespaces", getFieldEditorParent());
        addField(filteredNamespacesEditor);
        addField(new BooleanFieldEditor(EKubePreferenceConstants.CONTEXT_NAMESPACE_ONLY_ENABLED.getId(), "&If defined use context namespace only", getFieldEditorParent()));

        IntegerFieldEditor logLinesFieldEditor = new IntegerFieldEditor(EKubePreferenceConstants.LOG_LINES_TO_FETCH.getId(), "Fetch log lines", getFieldEditorParent());
        logLinesFieldEditor.setValidRange(10, 20000);
        addField(logLinesFieldEditor);
        
        /* --------------- shell script execution (kubectl normally ) ---------------*/
        
        shellExecutorLaunchCommand = new StringFieldEditor(EKubePreferenceConstants.SHELL_EXECUTOR_LAUNCH_COMMAND.getId(), "Shell executor launch command", getFieldEditorParent());
        shellExecutorLaunchCommand.getTextControl(getFieldEditorParent())
                .setToolTipText("Used when kubectl is called. You can customize this. Use " + ShellExecutor.SHELL_CMD_VARIABLE + " as a place holder for executed shell command!");
        addField(shellExecutorLaunchCommand);
        
        shellExecutorTitleCommand= new StringFieldEditor(EKubePreferenceConstants.SHELL_EXECUTOR_SET_TITLE_COMMAND.getId(), "Set title command", getFieldEditorParent());
        shellExecutorTitleCommand.getTextControl(getFieldEditorParent())
                .setToolTipText("You can customize the command. Use " + ShellExecutor.TITLE_VARIABLE+ " as a place holder for given title message");
        addField(shellExecutorTitleCommand);
        
        shellExecutorInteractiveShellCommand = new StringFieldEditor(EKubePreferenceConstants.SHELL_EXECUTOR_INTERACTIVE_SHELL_COMMAND.getId(), "Interactive shell command", getFieldEditorParent());
        shellExecutorInteractiveShellCommand.getTextControl(getFieldEditorParent())
                .setToolTipText("You can customize the command. Use " + ShellExecutor.POD_ID_VARIABLE+ " as a place holder for given pod id. Use "+ShellExecutor.NAMESPACE_NAME_VARIABLE+" as place holder for namespace");
        addField(shellExecutorInteractiveShellCommand);
        
        shellExecutorInteractiveLogviewerCommand = new StringFieldEditor(EKubePreferenceConstants.SHELL_EXECUTOR_INTERACTIVE_LOGVIEWER_COMMAND.getId(), "Interactive log viewer command", getFieldEditorParent());
        shellExecutorInteractiveLogviewerCommand.getTextControl(getFieldEditorParent())
                .setToolTipText("You can customize the command. Use " + ShellExecutor.POD_ID_VARIABLE+ " as a place holder for given pod id. Use "+ShellExecutor.NAMESPACE_NAME_VARIABLE+" as place holder for namespace");
        addField(shellExecutorInteractiveLogviewerCommand);

    }

    public void init(IWorkbench workbench) {
    }

}