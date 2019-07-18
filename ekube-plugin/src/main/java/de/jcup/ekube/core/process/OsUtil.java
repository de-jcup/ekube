package de.jcup.ekube.core.process;

public class OsUtil {
    private final static boolean isWindows;
    
    static {
        String os = System.getProperty("os.name");
        isWindows= os.toLowerCase().indexOf("windows")!=-1;
    }
    
    public static boolean isWindows() {
        return isWindows;
    }
}
