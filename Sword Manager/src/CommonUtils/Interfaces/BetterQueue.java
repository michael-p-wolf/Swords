package CommonUtils.Interfaces;

import CommonUtils.Interfaces.BetterQueueInterface;

import java.awt.*;

/**
 * @param <E> the type of object this queue will be holding
 * @implNote implement a queue using a circular array with initial capacity 8.
 * <p>
 * Implement BetterQueueInterface and add a constructor
 */
public class BetterQueue<E> implements BetterQueueInterface<E> {

    /**
     * Initial size of queue.  Do not decrease capacity below this value.
     */
    private final int INIT_CAPACITY = 8;


    /**
     * If the array needs to increase in size, it should be increased to
     * old capacity * INCREASE_FACTOR.
     * <p>
     * If it cannot increase by that much (old capacity * INCREASE_FACTOR > max int),
     * it should increase by CONSTANT_INCREMENT.
     * <p>
     * If that can't be done either throw OutOfMemoryError()
     */
    private final int INCREASE_FACTOR = 2;
    private final int CONSTANT_INCREMENT = 1 << 5; // 32

    /**
     * If the number of elements stored is < capacity * DECREASE_FACTOR, it should decrease
     * the capacity of the UDS to max(capacity * DECREASE_FACTOR, initial capacity).
     */
    private final double DECREASE_FACTOR = 0.5;

    /**
     * Array to store elements in
     */
    private E[] queue;
    private int first;
    private int last;
    private int capacity;
    private int size;

    /**
     * Constructs an empty queue
     */
    @SuppressWarnings("unchecked")
    public BetterQueue() {
        queue = (E[]) new Object[INIT_CAPACITY];
        first = 0;
        last = 0;
        capacity = INIT_CAPACITY;
        size = 0;
    }

    /**
     * Add an item to the back of the queue
     *
     * @param item item to push
     * @throws NullPointerException if the specified element is null
     */
    @Override
    public void add(E item) {
        if (item == null) {
            throw new NullPointerException();
        }

        /* increasing size if necessary */

        if (this.size == this.capacity) {
            if (this.capacity * INCREASE_FACTOR > Integer.MAX_VALUE) {
                if (this.capacity + CONSTANT_INCREMENT > Integer.MAX_VALUE) {
                    throw new OutOfMemoryError();
                } else {
                    E[] newQueue = (E[]) new Object[this.capacity + CONSTANT_INCREMENT];
                    for (int i = 0; i < this.capacity; i++) {
                        newQueue[i] = queue[i];
                    }
                    queue = newQueue;
                    capacity = capacity + CONSTANT_INCREMENT;
                    last = size;
                }
            } else {
                E[] newQueue = (E[]) new Object[this.capacity * INCREASE_FACTOR];
                for (int i = 0; i < this.capacity; i++) {
                    newQueue[i] = queue[(first + i) % capacity];
                }
                queue = newQueue;
                capacity = capacity * INCREASE_FACTOR;
                first = 0;
                last = size;
            }
        }

        queue[last] = item;
        last++;
        size++;

        last = last % capacity;
    }

    /**
     * Returns the front of the queue (does not remove it) or <code>null</code> if the queue is empty
     *
     * @return front of the queue or <code>null</code> if the queue is empty
     */
    @Override
    public E peek() {
        return queue[first];
    }

    /**
     * Returns and removes the front of the queue
     *
     * @return the head of the queue, or <code>null</code> if this queue is empty
     */
    @Override
    public E remove() {
        if (this.isEmpty()) {
            return null;
        }

        E dequeued;
        dequeued = queue[first];
        queue[first] = null;
        first = (first + 1) % capacity;
        size--;

        /* decrease the size of the array if necessary */

        if ((size <= capacity * DECREASE_FACTOR) && (capacity * DECREASE_FACTOR >= INIT_CAPACITY)) {
            capacity = (int) (this.capacity * DECREASE_FACTOR);
            E[] newQueue = (E[]) new Object[capacity];
            for (int i = 0; i < capacity; i++) {
                newQueue[i] = queue[(first + i) % queue.length];
            }
            last = 0;
            queue = newQueue;
            first = 0;
        }
        return dequeued;
    }

    /**
     * Returns the number of elements in the queue
     *
     * @return integer representing the number of elements in the queue
     */
    @Override
    public int size() {
        return this.size;
    }

    /**
     * Returns whether the queue is empty
     *
     * @return true if the queue is empty, false otherwise
     */
    @Override
    public boolean isEmpty() {
        if (this.size == 0) {
            return true;
        }
        return false;
    }

    /**
     * DO NOT MODIFY NOR IMPLEMENT THIS FUNCTION
     *
     * @param g graphics object to draw on
     */
    @Override
    public void draw(Graphics g) {
        //DO NOT MODIFY NOR IMPLEMENT THIS FUNCTION
        if (g != null) g.getColor();
        //todo GRAPHICS DEVELOPER:: draw the queue how we discussed
    }
}
