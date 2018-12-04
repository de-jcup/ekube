package de.jcup.ekube.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractEKubeContainer extends AbstractEKubeElement implements EKubeContainer {

	public AbstractEKubeContainer(String uid) {
		super(uid);
	}

	private List<EKubeElement> children = new ArrayList<>();

	/**
	 * Adds kubeElement to children list. Will also set this as parent for the
	 * child.
	 * 
	 * @param kubeElement
	 */
	protected void addChild(EKubeElement kubeElement) {
		children.add(kubeElement);
		if (kubeElement instanceof AbstractEKubeElement) {
			AbstractEKubeElement abstractElement = (AbstractEKubeElement) kubeElement;
			abstractElement.parent = this;
		}
	}

	public boolean isAlreadyFoundAndSoNoOrphan(EKubeElement element) {
		boolean contained = children.contains(element);
		if (contained) {
			orphans.remove(element);
		}
		return contained;
	}

	private Set<EKubeElement> orphans = new HashSet<>();

	/**
	 * Marks all children as being an orphan !
	 * Use {@link #isAlreadyFoundAndSoNoOrphan(EKubeElement)} to unmark them  */
	public void startOrphanCheck() {
		orphans.clear();
		for (EKubeElement element : children) {
			orphans.add(element);
		}
	}

	/**
	 * Removes all children being orphans
	 */
	public void removeOrphans() {
		for (EKubeElement element : orphans) {
			children.remove(element);
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
	protected <T> List<T> fetchAllChildrenOfType(Class<T> clazz) {
		List<T> list = new ArrayList<>();
		for (EKubeElement element : children) {
			if (clazz.isAssignableFrom(element.getClass())) {
				list.add((T) element);
			}
		}
		return list;
	}
}
