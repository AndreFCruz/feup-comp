/**
 * Copyright (C) 2006-2017 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package spoon.support.visitor.equals;

import spoon.SpoonException;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.visitor.CtScanner;
import spoon.support.util.EmptyClearableList;
import spoon.support.util.EmptyClearableSet;
import spoon.support.visitor.clone.CloneVisitor;

import java.util.*;

/**
 * {@link CloneHelper} is responsible for creating clones of {@link CtElement} AST nodes including the whole subtree.
 *
 * By default, the same instance of {@link CloneHelper} is used for whole clonning process.
 *
 * However, by subclassing this class and overriding method {@link #clone(CtElement)},
 * one can extend and/or modify the cloning behavior.
 *
 * For instance, one can listen to each call to clone and get each pair of `clone source` and `clone target`.
 */
public class CloneHelper {
	public static final CloneHelper INSTANCE = new CloneHelper();

	public <T extends CtElement> T clone(T element) {
		final CloneVisitor cloneVisitor = new CloneVisitor(this);
		cloneVisitor.scan(element);
		return cloneVisitor.getClone();
	}

	public <T extends CtElement> Collection<T> clone(Collection<T> elements) {
		if (elements == null || elements.isEmpty()) {
			return new ArrayList<>();
		}
		Collection<T> others = new ArrayList<>();
		for (T element : elements) {
			addClone(others, element);
		}
		return others;
	}

	public <T extends CtElement> List<T> clone(List<T> elements) {
		if (elements instanceof EmptyClearableList) {
			return elements;
		}
		if (elements == null || elements.isEmpty()) {
			return new ArrayList<>();
		}
		List<T> others = new ArrayList<>();
		for (T element : elements) {
			addClone(others, element);
		}
		return others;
	}

	private <T extends CtElement> Set<T> createRightSet(Set<T> elements) {
		try {
			if (elements instanceof TreeSet) {
				// we copy the set, incl its comparator
				// we may also do this with reflection
				Set s = (Set) ((TreeSet) elements).clone();
				s.clear();
				return s;
			} else {
				return elements.getClass().newInstance();
			}
		} catch (InstantiationException | IllegalAccessException e) {
			throw new SpoonException(e);
		}
	}

	public <T extends CtElement> Set<T> clone(Set<T> elements) {
		if (elements instanceof EmptyClearableSet) {
			return elements;
		}
		if (elements == null || elements.isEmpty()) {
			return EmptyClearableSet.instance();
		}

		Set<T> others = createRightSet(elements);
		for (T element : elements) {
			addClone(others, element);
		}
		return others;
	}

	public <T extends CtElement> Map<String, T> clone(Map<String, T> elements) {
		if (elements == null || elements.isEmpty()) {
			return new HashMap<>();
		}
		Map<String, T> others = new HashMap<>();
		for (Map.Entry<String, T> tEntry : elements.entrySet()) {
			addClone(others, tEntry.getKey(), tEntry.getValue());
		}
		return others;
	}

	/**
	 * clones a element and adds it's clone as value into targetCollection
	 * @param targetCollection - the collection which will receive a clone of element
	 * @param element to be cloned element
	 */
	protected <T extends CtElement> void addClone(Collection<T> targetCollection, T element) {
		targetCollection.add(clone(element));
	}

	/**
	 * clones a value and adds it's clone as value into targetMap under key
	 * @param targetMap - the Map which will receive a clone of value
	 * @param key the target key, which has to be used to add cloned value into targetMap
	 * @param value to be cloned element
	 */
	protected <T extends CtElement> void addClone(Map<String, T> targetMap, String key, T value) {
		targetMap.put(key, clone(value));
	}


	/** Is called by {@link CloneVisitor} at the end of the cloning for each element. */
	public void tailor(final spoon.reflect.declaration.CtElement topLevelElement, final spoon.reflect.declaration.CtElement topLevelClone) {
		// this scanner visit certain nodes to done some additional work after cloning
		new CtScanner() {
			@Override
			public <T> void visitCtExecutableReference(CtExecutableReference<T> clone) {
				// for instance, here we can do additional things
				// after cloning an executable reference
				// we have access here to "topLevelElement" and "topLevelClone"
				// if we want to analyze them as well.

				// super must be called to visit the subelements
				super.visitCtExecutableReference(clone);
			}
		}.scan(topLevelClone);
	}

}
