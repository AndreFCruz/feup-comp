package worker;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.CtScanner;

import java.util.*;

/**
 * Custom CtElement iterator that returns the elements in the tree of a given node, the root, in DFS
 */
public class CtIterator extends CtScanner implements Iterator {
    /**
     * A deque containing the elements the iterator has seen but not expanded
     */
    private ArrayDeque<CtElement> deque = new ArrayDeque<CtElement>() {
        /**
         * add a collection of elements with addFirst instead of default add() which defaults to addLast()
         * @param c Collection of CtElements
         * @return true if this deque has changed, in accordance with original method
         */
        @Override
        public boolean addAll(Collection c) {
            for (Object aC : c) this.addFirst((CtElement) aC);
            return c.size() > 0;
        }
    };

    /**
     * A deque to be used when scanning an element so that @deque preserves the elements in dfs without complete expansion
     */
    private ArrayDeque<CtElement> current_children = new ArrayDeque<>();

    /**
     * CtIterator constructor, prepares the iterator from the @root node
     *
     * @param root the initial node to expand
     */
    public CtIterator(CtElement root) { if (root != null) deque.add(root); }

    /**
     * prevent scanner from going down the tree, instead save with other CtElement children of the current node
     *
     * @param element the next direct child of the current node being expanded
     */
    @Override
    public void scan(CtElement element) {
        if (element != null) current_children.addFirst(element);
    }

    @Override
    public boolean hasNext() { return deque.size() > 0; }

    /**
     * Dereference the "iterator"
     *
     * @return the next element in DFS order without going down the tree
     */
    @Override
    public Object next() {
        CtElement next = deque.pollFirst(); // get the element to expand from the deque
        current_children.clear(); // clear for this scan
        next.accept(this); // call @scan for each direct child of the node
        deque.addAll(current_children); // overridden method to add all to first
        return next;
    }
}