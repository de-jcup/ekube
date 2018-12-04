package de.jcup.ekube.core.model;

public class VolumesContainer extends AbstractEKubeContainer implements SyntheticKubeElement{

	public VolumesContainer(){
		super(null);// no uid available - because synthetic element which is not existing in kubernetes
		label="Volumes";
	}
	
	public void add(PersistentVolumeClaimElement persistentVolumeClaim){
		addChild(persistentVolumeClaim);
	}
}
