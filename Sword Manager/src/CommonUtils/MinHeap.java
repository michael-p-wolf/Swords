package CommonUtils;

import CommonUtils.Interfaces.MinHeapInterface;

import java.awt.*;
import java.util.Vector;

/**
 * Implements our MinHeapInterface and adds a constructor
 * <p>
 * <b>251 students: You are explicitly forbidden from using java.util.Queue (including any subclass
 *   like PriorityQueue) and any other java.util.* library EXCEPT java.util.Arrays and java.util.Vector.
 *   Write your own implementation of a MinHeap.</b>
 *
 * @param <E> the type of object this heap will be holding
 */
public class MinHeap<E extends Comparable<E>> implements MinHeapInterface<E> {


    /* this should be private */
    Vector<E> list;
    int n;

    /**
     * A recursive method to heapify (sort the root to where it should go) a
     *   subtree with the root at given index
     * Assumes the subtrees are already heapified.
     * (The purpose of this method is to balance tree starting at the root)
     * @param i root of the subtree to heapify
     */

    /* this should be private */
    public void heapify(int i) {
        // i is the root of the subtree that we need to heapify
        //heapify is bottom up

        /* let the base case be when the node at i has no children */
        if ((leftChild(i) > n - 1) && (rightChild(i) > n - 1)) { /* the root has no children, don't need to heapify */
            return;
        }

        heapify(leftChild(i));
        heapify(rightChild(i));
        /* heapifty right child */

        /* get the minimum child of the parent and then swap if necessary */

        sinkDown(i);
    }

    /* maybe write a sink down function */

    private void sinkDown (int i) {
        if (leftChild(i) >= n) { /* there are no children to sink down with */
            return;
        }

        /* check if right and left are valid */

        int minIndex = -1;
        E minValue;

        /* get minimum child */

        minIndex = leftChild(i);

        if (rightChild(i) < n && list.get(minIndex).compareTo(list.get(rightChild(i))) > 0) {
            minIndex = rightChild(i);
        }

        if (list.get(i).compareTo(list.get(minIndex)) > 0) {
            swap(i, minIndex);
            sinkDown(minIndex);
        }
    }

    private void swap (int i, int j) {
        if (i >= n || j >= n) {
            return;
        }
        E one = list.get(i);
        E two = list.get(j);
        list.set(i, two);
        list.set(j, one);
    }

    private int getParent(int i) {
        return Math.floorDiv(i - 1, 2);
    }
    private int leftChild (int i) {
        return 2 * i + 1;
    }

    private int rightChild (int i) {
        return 2 * i + 2;
    }

    /**
     * Constructs an empty min heap
     */
    public MinHeap(){
        this.list = new Vector<>();
        n = 0;
    }

    /**
     * Adds the item to the min heap
     *
     * @param item item to add
     * @throws NullPointerException if the specified element is null
     */
    @Override
    public void add(E item) {
        if (item == null) {
            throw new NullPointerException();
        }

        list.add(item);
        int index = n;
        n++;

        while (index > 0 && item.compareTo(list.get(getParent(index))) < 0) {
            swap(index, getParent(index));
            index = getParent(index);
        }
    }

    /**
     * Empties the heap.
     */
    @Override
    public void clear() {
        for (int i = 0; i < list.size(); i++) {
            list.set(i, null);
        }
        n = 0;
    }

    /**
     * Returns the minimum element without removing it, or returns <code>null</code> if heap is empty
     *
     * @return the minimum element in the heap, or <code>null</code> if heap is empty
     */
    @Override
    public E peekMin() {
        if (n == 0) {
            return null;
        }
        return list.get(0);
    }

    /**
     * Remove and return the minimum element in the heap, or returns <code>null</code> if heap is empty
     *
     * @return the minimum element in the heap, or <code>null</code> if heap is empty
     */
    @Override
    public E removeMin() {
        if (n == 0) {
            return null;
        }
        E item = list.get(0);
        list.set(0, list.get(n - 1));
        list.remove(n - 1);
        n--;
        sinkDown(0);

        return item;
    }

    /**
     * Returns the number of elements in the heap
     *
     * @return integer representing the number of elements in the heap
     */
    @Override
    public int size() {
        return n;
    }

    /* debug */

    /**
     * DO NOT MODIFY NOR IMPLEMENT THIS FUNCTION
     *
     * @param g graphics object to draw on
     */
    @Override
    public void draw(Graphics g) {
        //DO NOT MODIFY NOR IMPLEMENT THIS FUNCTION
        if(g != null) g.getColor();
        //todo GRAPHICS DEVELOPER:: draw the MinHeap how we discussed
        //251 STUDENTS:: YOU ARE NOT THE GRAPHICS DEVELOPER!
    }
}
