package de.jcup.ekube.core;

import java.io.File;

public class EKubeFiles {
    
    public static File getUserHome() {
        File userHomeFile = new File(System.getProperty("user.home"));
        return userHomeFile;
    }
    
    public static File getEKubeHome() {
        File userHomeFile = new File(getUserHome(), "./ekube");
        return userHomeFile;
    }
    
    
    public static File getEKubeTempFolder() {
        File userHomeFile = new File(getEKubeHome(), "./ekube");
        return userHomeFile;
    }

    public static File getDefaultKubeConfigFile() {
        return new File(getUserHome(), ".kube/config");
    }
    
}
