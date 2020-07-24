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
package de.jcup.ekube.core.process;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class ShellExecutor {
    public static final String SHELL_CMD_VARIABLE = "${SHELL_CMD}";
    public static final String POD_ID_VARIABLE = "${POD_ID}";
    public static final String NAMESPACE_NAME_VARIABLE = "${NAMESPACE_NAME}";
    public static final String CONTEXT_NAME_VARIABLE = "${CONTEXT_NAME}";
    public static final String TITLE_VARIABLE = "${TITLE}";

    private static final String DEFAULT_LAUNCHER_LINUX = "x-terminal-emulator -e bash --login -c " + SHELL_CMD_VARIABLE;
    private static final String DEFAULT_LAUNCHER_WINDOWS = " cmd.exe /C start cmd.exe /C \"bash --login -c '" + SHELL_CMD_VARIABLE + "'";

    private static final String DEFAULT_KUBECTL_INTERACTIVE_SHELL_CMD = "kubectl exec --context " + CONTEXT_NAME_VARIABLE + " --namespace " + NAMESPACE_NAME_VARIABLE + " -it " + POD_ID_VARIABLE
            + " -- " + "/bin/sh";
    private static final String DEFAULT_KUBECTL_LOGVIEW_CMD = "kubectl logs --context " + CONTEXT_NAME_VARIABLE + " --namespace " + NAMESPACE_NAME_VARIABLE + " -f --tail=20 " + POD_ID_VARIABLE;

    private static final String DEFAULT_TITLE_COMMAND = "printf \\\"\\\\e]2;" + TITLE_VARIABLE + "\\\\a\"";

    private List<String> processLauncherCommandList;

    private String launcherCommand;
    private String interactiveShellCommand;
    private String interactiveLogViewerCommand;
    private String titleCommand;

    public static ShellExecutor INSTANCE = new ShellExecutor();

    public static String resolveOSDefaultLaunchCommand() {
        if (OsUtil.isWindows()) {
            return DEFAULT_LAUNCHER_WINDOWS;
        } else {
            return DEFAULT_LAUNCHER_LINUX;
        }
    }

    private ShellExecutor() {
        setLauncherCommand(resolveOSDefaultLaunchCommand());
        setInteractiveShellCommand(resolveDefaultInteractiveShellCommand());
        setInteractiveLogViewerCommand(resolveDefaultInteractiveLogViewerCommand());
        setTitleCommand(resolveDefaultTitleCommand());
    }

    public static String resolveDefaultTitleCommand() {
        return DEFAULT_TITLE_COMMAND;
    }

    public void setTitleCommand(String titleCommand) {
        this.titleCommand = titleCommand;
    }

    public String getTitleCommand() {
        return titleCommand;
    }

    public static String resolveDefaultInteractiveShellCommand() {
        return DEFAULT_KUBECTL_INTERACTIVE_SHELL_CMD;
    }

    public static String resolveDefaultInteractiveLogViewerCommand() {
        return DEFAULT_KUBECTL_LOGVIEW_CMD;
    }

    public void setInteractiveLogViewerCommand(String interactiveLogViewerCommand) {
        this.interactiveLogViewerCommand = interactiveLogViewerCommand;
    }

    public void setInteractiveShellCommand(String interactiveShellCommand) {
        this.interactiveShellCommand = interactiveShellCommand;
    }

    public void setLauncherCommand(String launcherCommand) {
        if (launcherCommand == null) {
            launcherCommand = "";
        }
        this.launcherCommand = launcherCommand;
        String[] commands = launcherCommand.split(" ");
        processLauncherCommandList = Arrays.asList(commands);
    }

    public String getLauncherCommand() {
        return launcherCommand;
    }

    private String createInteractiveShellCommand(String podId, String namespace, String contextName) {
        String command = interactiveShellCommand;
        command = buildReplacedPODIDCommand(command, podId);
        command = buildReplacedContextNameCommand(command, contextName);
        command = buildReplacedNamespaceNameCommand(command, namespace);
        return command;
    }

    private String buildReplacedNamespaceNameCommand(String command, String namespaceName) {
        return buildReplacedCommand(command, namespaceName, NAMESPACE_NAME_VARIABLE);
    }

    private String buildReplacedContextNameCommand(String command, String contextName) {
        return buildReplacedCommand(command, contextName, CONTEXT_NAME_VARIABLE);
    }

    private String createInteractiveLogViewerCommand(String podId, String namespace, String contextName) {
        String command = interactiveLogViewerCommand;
        command = buildReplacedPODIDCommand(interactiveLogViewerCommand, podId);
        command = buildReplacedContextNameCommand(command, contextName);
        command = buildReplacedNamespaceNameCommand(command, namespace);
        return command;
    }

    private String buildReplacedPODIDCommand(String command, String podId) {
        return buildReplacedCommand(command, podId, POD_ID_VARIABLE);
    }

    private String buildReplacedCommand(String command, String replacement, String unEscapedIdentifier) {
        if (command == null || command.isEmpty()) {
            return "";
        }
        if (replacement == null) {
            replacement = "";
        }
        try {
            String escapedIdentifier = Pattern.quote(unEscapedIdentifier);
            return command.replaceAll(escapedIdentifier, replacement);
        } catch (RuntimeException e) {
            throw new IllegalStateException("Was not able to replace command: '" + command + "' with replacment:'" + replacement + "' for identifier:'" + unEscapedIdentifier + "'", e);
        }
    }

    public void interactiveRunFirstContainerInPod(String podId, String namespace, String contextName) throws IOException {
        executeShellCommand(createInteractiveShellCommand(podId, namespace, contextName), "Interactive shell to pod:" + podId + " (" + contextName + ":" + namespace + ")");
    }

    public void interactiveLogViewerInPod(String podId, String namespace, String contextName) throws IOException {
        executeShellCommand(createInteractiveLogViewerCommand(podId, namespace, contextName), "Logs of pod:" + podId + " (" + contextName + ":" + namespace + ")");
    }

    private void executeShellCommand(String command, String title) throws IOException {
        List<String> commandList = new ArrayList<>();
        try {

            String createdTitleCommand = createTitleCommand(title);
            if (createdTitleCommand != null && createdTitleCommand.length() > 0) {
                command = createdTitleCommand + ";" + command;
            }
            for (String launcherCMD : this.processLauncherCommandList) {
                if (launcherCMD.indexOf(SHELL_CMD_VARIABLE) != -1) {
                    launcherCMD = buildReplacedCommand(launcherCMD, command, SHELL_CMD_VARIABLE);
                }
                commandList.add(launcherCMD);
            }
            ProcessBuilder pb = new ProcessBuilder(commandList);
            pb.start();

        } catch (Exception e) {
            String message = "Was not able to execute command:" + commandList;
            throw new IOException(message, e);
        }
    }

    private String createTitleCommand(String title) {
        return buildReplacedCommand(titleCommand, title, TITLE_VARIABLE);
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.err.println("Usage: shellExecutor ${podId} ${contextname} ${namespaceName}");
            System.exit(1);
        }
        String podId = args[0];
        String contextName = args[1];
        String namespaceName = args[2];
//      x-terminal-emulator -e bash --login -c 'cd /tmp;./terminallaunch2107228848119865518.sh -a 1 -b 2;_exit_status=$?;echo "Exit code=$_exit_status";if [ $_exit_status -ne 0 ]; then read -p "Unexpected exit code:$_exit_status , press enter to continue";fi'
//      cmd.exe /C start "Bash Editor DEBUG Session: terminallaunch7722797850802661988.sh" cmd.exe /C "bash --login -c './terminallaunch7722797850802661988.sh -a 1 -b 2;_exit_status=$?;echo "Exit code=$_exit_status";read -p "Press enter to continue..."'"

        ShellExecutor.INSTANCE.interactiveRunFirstContainerInPod(podId, namespaceName, contextName);
        ShellExecutor.INSTANCE.interactiveLogViewerInPod(podId, namespaceName, contextName);
        System.out.println("after run...");
    }

}
