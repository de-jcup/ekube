package de.jcup.ekube.core.access;

import java.util.List;

public interface Namespace  extends EKubeObject{
	
	public List<Pod> getPods();

}
