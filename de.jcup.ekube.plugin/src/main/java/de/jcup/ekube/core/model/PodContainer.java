package de.jcup.ekube.core.model;

public class PodContainer extends AbstractEKubeContainer{

	public void add(DockerElement docker){
		children.add(docker);
	}
}
