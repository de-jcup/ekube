package de.jcup.ekube.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractEKubeContainer extends AbstractEKubeElement implements EKubeContainer{

	private List<EKubeElement> children = new ArrayList<>();
	
	public AbstractEKubeContainer() {
	}

	/**
	 * Adds kubeElement to children list. Will also set this as parent for the child.
	 * @param kubeElement
	 */
	protected void addChild(EKubeElement kubeElement){
		children.add(kubeElement);
		if (kubeElement instanceof AbstractEKubeElement){
			AbstractEKubeElement abstractElement= (AbstractEKubeElement) kubeElement;
			abstractElement.parent=this;
		}
	}

	@Override
	public List<EKubeElement> getChildren() {
		return Collections.unmodifiableList(children);
	}
	
	@Override
	public boolean hasChildren() {
		return !children.isEmpty();
	}
	
	@SuppressWarnings("unchecked")
	protected <T> List<T> fetchAllChildrenOfType(Class<T> clazz){
		List<T> list = new ArrayList<>();
		for (EKubeElement element: children){
			if (clazz.isAssignableFrom(element.getClass())){
				list.add((T) element);
			}
		}
		return list;
	}
}
