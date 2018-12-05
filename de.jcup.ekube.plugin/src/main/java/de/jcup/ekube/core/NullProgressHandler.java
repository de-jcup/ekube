package de.jcup.ekube.core;

public class NullProgressHandler implements EKubeProgressHandler {

    @Override
    public void beginTask(String name, int totalWork) {

    }

    @Override
    public void beginSubTask(String name) {

    }

    @Override
    public void worked(int summary) {

    }

}
