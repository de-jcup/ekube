package de.jcup.ekube.core.model;

public class VolumesContainer extends AbstractEKubeContainer{

	public VolumesContainer(){
		label="Volumes";
	}
	
	public void add(PersistentVolumeClaimElement persistentVolumeClaim){
		addChild(persistentVolumeClaim);
	}
}
