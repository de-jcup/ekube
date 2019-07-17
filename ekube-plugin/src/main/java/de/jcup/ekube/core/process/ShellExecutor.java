package de.jcup.ekube.core.process;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShellExecutor {

    
    public void interactiveRunFirstContainerInPod(String podId) throws IOException{
        String command = "echo 'Connecting to first container inside pod:"+podId+"';kubectl exec -it "+podId+" -- "+" /bin/bash";
        executeShellCommand(command, "Interactive shell to pod:"+podId);
        
    }
    
    public void interactiveLogViewerInPod(String podId) throws IOException{
     
        
        String command = "echo 'Show logs inside pod:"+podId+"';kubectl logs -f --tail=20 "+podId;
        executeShellCommand(command,"Logs of pod:"+podId);
        
    }

    private void executeShellCommand(String command, String title) throws IOException {
        List<String> commandList = new ArrayList<>();
        /* customiztion of this could be copied from bash debugger parts!*/
        
        String gnomeTitleSetter="printf \"\\e]2;"+title+"\\a\"";
        
        commandList.add("x-terminal-emulator");
        commandList.add("-e");
        commandList.add("bash");
        commandList.add("--login");
        commandList.add("-c");
        command=gnomeTitleSetter+";"+command;
        commandList.add(command);
        
        ProcessBuilder pb = new ProcessBuilder(commandList);
        pb.start();
    }
    
    public static void main(String[] args) throws IOException {
//      x-terminal-emulator -e bash --login -c 'cd /tmp;./terminallaunch2107228848119865518.sh -a 1 -b 2;_exit_status=$?;echo "Exit code=$_exit_status";if [ $_exit_status -ne 0 ]; then read -p "Unexpected exit code:$_exit_status , press enter to continue";fi'
      new ShellExecutor().interactiveRunFirstContainerInPod("sechub-server-557bfd98d5-mlxq2");
//        new ShellExecutor().interactiveLogViewerInPod("sechub-server-557bfd98d5-mlxq2");
      System.out.println("after run...");
  }

}
