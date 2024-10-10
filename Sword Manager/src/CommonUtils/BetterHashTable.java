package CommonUtils;

import CommonUtils.Interfaces.BetterHashTableInterface;

import java.awt.*;
import java.util.Objects;

/**
 * Implements our {@link BetterHashTableInterface} and adds two constructors.
 *
 * @param <K> Type of key the hash table is holding
 * @param <V> Type of value the hash table is holding
 * @implNote Implements a hash table using an array with initial capacity 19.
 */
public class BetterHashTable<K, V> implements BetterHashTableInterface<K, V> {
    /**
     * Initial size of hash table.
     */
    private final int INIT_CAPACITY = 29;
    /**
     * Determines the maximum ratio of number of elements to array size.
     * <p>
     * If the number of elements stored is > LOAD_FACTOR, it should increase
     * the capacity of the array according to INCREASE_FACTOR.
     * <p>
     * Our hash table will not decrease its size for the duration of its lifetime.
     */
    private final double LOAD_FACTOR = 0.75;
    /**
     * Determines how much to increase the array's capacity.
     * <p>
     * If the array needs to increase in size (see load factor), it should prefer to
     * increase by the old capacity * INCREASE_FACTOR.  If it cannot increase by
     * that much (max int), it should increase by some small quantity (<100).
     */
    private final int INCREASE_FACTOR = 2;
    /**
     * "some small quantity (<100)"
     */
    private final int CAPACITY_INCREMENT = 1 << 5;//32

    int size;
    int capacity;

    /**
     * Simple storage unit for our hash table
     */
    private static class Node<K, V> {
        final K key;
        V value;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }

        //default intellij-generated tostring
        @Override
        public String toString() {
            return "Node{" + "key=" + key + ", value=" + value + '}';
        }

        //default intellij-generated equals
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Node)) return false;
            Node<?, ?> node = (Node<?, ?>) o;
            return Objects.equals(key, node.key) && Objects.equals(value, node.value);
        }
    }

    /**
     * Default entry to use when marking a slot as deleted when using quadratic probing
     */
    private final Node<K, V> DELETED = new Node<>(null, null);

    /**
     * Array to store elements  (according to the implementation
     * note in the class header comment).
     */
    Node<K, V>[] table;

    /**
     * Constructs the hash table with a default size
     */
    @SuppressWarnings("unchecked")
    public BetterHashTable() {
        this.table = (Node<K, V>[]) new Node[INIT_CAPACITY];
        size = 0;
        capacity = INIT_CAPACITY;
    }

    /**
     * Constructor that initializes the hash table with an initial capacity
     *
     * @param initialCapacity initial table capacity
     * @throws IllegalArgumentException if the initial capacity is negative
     */
    @SuppressWarnings("unchecked")
    public BetterHashTable(int initialCapacity) throws IllegalArgumentException {
        if (initialCapacity < 0) throw new IllegalArgumentException();

        this.table = (Node<K, V>[]) new Node[initialCapacity];
        size = 0;
        capacity = initialCapacity;
    }

    /**
     * Returns a positive hash of the given thing. Should be useful for a hash table.
     *
     * @param thing thing to hash
     * @return positive hashed value
     */
    private int usefulHash(K thing) {
        return Math.abs(Objects.hash(thing));
    }

    private int hashFunction(int key, int i) {
        long probe = i * i;
        long asdf = key + probe;
        long index = asdf % capacity;
        return (int) index;
    }


    /**
     * Places the item in the hash table. Passing key=null will not change the state of the table.
     * Passing a key already in the table will modify the original entry in the table.
     *
     * @param key   key to associate with the value
     * @param value item to store in the hash table
     */
    @Override
    public void insert(K key, V value) {
        if (key == null) {
            return;
        }

        if (containsKey(key)) {
            int probe = 0;

            while (probe < capacity) {
                int index = hashFunction(usefulHash(key), probe);
                if (table[index].key != null) {
                    if (table[index].key.equals(key)) {
                        table[index] = new Node<>(key, value);
                        return;
                    }
                }
                probe++;
            }
        }

        /* increase the capacity if necessary */

        double ratio = (double) size / (double) capacity;
        if (ratio > LOAD_FACTOR) {
            if (capacity * INCREASE_FACTOR > Integer.MAX_VALUE) {
                int oldCapacity = capacity;
                capacity = capacity + CAPACITY_INCREMENT;

                Node<K, V>[] list = new Node[capacity];


                for (int i = 0; i < oldCapacity; i++) {
                    int probe = 0;
                    if (table[i] != null) {
                        boolean stored = false;
                        while (probe < capacity && stored == false) {
                            int index = hashFunction(usefulHash(table[i].key), probe);
                            if (list[index] == null) {
                                list[index] = table[i];
                                stored = true;
                            }
                            probe++;
                        }
                    }
                }
                this.table = list;
            } else {
                int oldCapacity = capacity;
                capacity = capacity * INCREASE_FACTOR;

                Node<K, V>[] list = new Node[capacity];

                for (int i = 0; i < oldCapacity; i++) { /* rehashing with the new capacity */
                    int probe = 0;
                    if (table[i] != null) {
                        boolean stored = false;
                        while (probe < capacity && stored == false) {
                            int index = hashFunction(usefulHash(table[i].key), probe);
                            if (list[index] == null) {
                                list[index] = table[i];
                                stored = true;
                            }
                            probe++;
                        }
                    }
                }
                this.table = list;
            }
        }

        int probe = 0;

        while (probe < capacity) {
            int index = hashFunction(usefulHash(key), probe);
            if (table[index] == null) {
                table[index] = new Node<>(key, value);
                size++;
                return;
            } else if (table[index].equals(DELETED)) {
                table[index] = new Node<>(key, value);
                size++;
                return;
            }
            probe++;
        }
    }

    /**
     * Removes the key, value pair associated with the given key
     *
     * @param key key/value to remove
     */
    @Override
    public void remove(K key) {
        int probe = 0;
        int index = hashFunction(usefulHash(key), probe);

        while (table[index] != null && probe < capacity) {
            if (table[index].key != null) {
                if (table[index].key.equals(key)) {
                    table[index] = DELETED;
                    size--;
                    return;
                }

            }
            probe++;
            index = hashFunction(usefulHash(key), probe);
        }
    }

    /**
     * Retrieves a value based on the given key
     *
     * @param key key to search by
     * @return value associated with the key, or <code>null</code> if it does not exist
     */
    @Override
    public V get(K key) {
        int probe = 0;
        int index = hashFunction(usefulHash(key), probe);

        while (table[index] != null && probe < capacity) {
            if (table[index].key != null) {
                if (table[index].key.equals(key)) {
                    return table[index].value;
                }
            }
            probe++;
            index = hashFunction(usefulHash(key), probe);
        }
        return null;
    }

    /**
     * Returns <code>true</code> if this hash table contains the given key.
     *
     * @param key key to check for
     * @return true iff the hash table contains a mapping for the specified key,
     * as ultimately determined by the equals method
     */
    @Override
    public boolean containsKey(K key) { /*DOESNT WORK*/
        int probe = 0;
        int index = hashFunction(usefulHash(key), probe);

        while (table[index] != null && probe < capacity) {
            if (table[index].key != null) {
                if (table[index].key.equals(key)) {
                    return true;
                }
            }
            probe++;
            index = hashFunction(usefulHash(key), probe);
        }
        return false;
    }

    /**
     * Empties the hash table.
     */
    @Override
    @SuppressWarnings("unchecked")
    public void clear() {
        for (int i = 0; i < capacity; i++) {
            table[i] = null; /* pretty sure we can do null here because its all empty */
        }
        size = 0;
    }

    /**
     * Returns the number of items in the hash table
     *
     * @return integer representing the number of elements in the hash table
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * DO NOT MODIFY NOR IMPLEMENT THIS FUNCTION
     *
     * @param g graphics object to draw on
     */
    @Override
    public void draw(Graphics g) {
        if (g != null) g.getColor();
        //todo GRAPHICS DEVELOPER:: draw the hash table how we discussed
    }

    /**
     * DO NOT MODIFY NOR IMPLEMENT THIS FUNCTION
     *
     * @param g graphics object to draw on
     */
    @Override
    public void visualize(Graphics g) {
        if (g != null) g.getColor();
        //todo GRAPHICS DEVELOPER:: visualization is to be time-based -- how we discussed
    }
}
