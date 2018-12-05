package de.jcup.ekube.core;

public interface EKubeProgressHandler {

    public void beginTask(String name, int totalWork);

    public void beginSubTask(String name);

    public void worked(int summary);
}
