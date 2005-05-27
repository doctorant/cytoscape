package cytoscape.util.intr;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An insert-only hashtable that has non-negative 32 bit integer keys and
 * non-null object values.<p>
 * In the underlying implementation, this hashtable increases in size to adapt
 * to key/value pairs being added (the underlying size of the hashtable is
 * invisible to the programmer).  In the underlying implementation, this
 * hashtable never decreases in size.  As a hashtable increases in size,
 * it takes at most four times as much memory as it would take
 * to store the hashtable's keys and values in perfectly-sized arrays.
 * Underlying size expansions are implemented such that the operation of
 * expanding in size is amortized over the contstant time complexity needed to
 * insert new elements.<p>
 */
public final class IntObjHash
{
  
  private static final int[] PRIMES = { 11, 23, 47, 97, 197, 397, 797, 1597,
                                        3203, 6421, 12853, 25717, 51437,
                                        102877, 205759, 411527, 823117,
                                        1646237, 3292489, 6584983, 13169977,
                                        26339969, 52679969, 105359939,
                                        210719881, 421439783, 842879579,
                                        1685759167, Integer.MAX_VALUE };
  private static final double THRESHOLD_FACTOR = 0.785;

  /**
   * For a hashtable that currently holds exactly num keys [or zero keys if
   * num is negative], returns the
   * maximum number of keys that that hashtable can hold without undergoing an
   * underlying size expansion.  Size expansions are expensive computationally
   * and result in a doubling of the amount of memory consumed; this function
   * is a hook for users of hashtables to do smart things when an underlying
   * size expansion is about to happen.  Returns -1 if a hashtable cannot
   * hold the number of keys specified.
   */
  public final static int maxCapacity(final int num)
  {
    int inx = 0;
    while (inx < PRIMES.length && num >= PRIMES[inx]) inx++;
    final int thresholdSize = (int) (THRESHOLD_FACTOR * (double) PRIMES[inx]);
    // THRESHOLD_FACTOR * Integer.MAX_VALUE is greater than than 1685759167.
    if (thresholdSize >= num) return thresholdSize;
    else if (++inx == PRIMES.length) return -1;
    return (int) (THRESHOLD_FACTOR * (double) PRIMES[inx]);
  }

  private final static int maxCapacityIndex(final int num)
  {
    int inx = 0;
    while (inx < PRIMES.length && num >= PRIMES[inx]) inx++;
    final int thresholdSize = (int) (THRESHOLD_FACTOR * (double) PRIMES[inx]);
    // THRESHOLD_FACTOR * Integer.MAX_VALUE is greater than than 1685759167.
    if (thresholdSize >= num) return inx;
    else if (++inx == PRIMES.length) return 0;
    return inx;
  }

  private int[] m_keys;
  private Object[] m_vals;
  private int m_elements;
  private int m_thresholdSize;

  // These are caching variables.  The idea is that programmers will
  // frequently first do a get(), and based on that result, will perform
  // some other operations and then maybe do a put() operation with the same
  // key as the previous get() operation.
  private int m_prevKey;
  private int m_prevInx;

  /**
   * Creates a new hashtable.
   */
  public IntObjHash()
  {
    m_keys = new int[PRIMES[0]];
    m_vals = new Object[PRIMES[0]];
    m_elements = 0;
    m_thresholdSize = (int) (THRESHOLD_FACTOR * (double) m_keys.length);
    for (int i = 0; i < m_keys.length; i++) m_keys[i] = -1;
    m_prevKey = -1;
    m_prevInx = -1;
  }

  /**
   * Creates a new hashtable large enough to hold maxCapacity(capacity)
   * key/value pairs.  Calling this constructor with a negative argument
   * is equivalent to calling the default constructor.  If
   * maxCapacity(capacity) evaluates to -1 then this is identical to calling
   * the default constructor.<p>
   * There is no reason to use this constructor as opposed
   * to the default constructor except for the case where one is trying to
   * squeeze out every last drop of performance gains.  Note that this
   * constructor has time complexity linear to the specified capacity,
   * whereas the default constructor has constant time complexity.
   */
  public IntObjHash(final int capacity)
  {
    final int primesIndex = maxCapacityIndex(capacity);
    m_keys = new int[PRIMES[primesIndex]];
    m_vals = new Object[PRIMES[primesIndex]];
    m_elements = 0;
    m_thresholdSize = (int) (THRESHOLD_FACTOR * (double) m_keys.length);
    for (int i = 0; i < m_keys.length; i++) m_keys[i] = -1;
    m_prevKey = -1;
    m_prevInx = -1;
  }

  /**
   * Returns the number of key/value pairs currently in this hashtable.
   */
  public final int size()
  {
    return m_elements;
  }

  /**
   * Puts a new key/value pair into this hashtable, potentially overwriting
   * an existing value whose key is the same as the one specified.
   * Returns the old value associated with the specified key or null if no
   * value is associated with specified key at the time of this call.<p>
   * Insertions into the hashtable are performed in [amortized] time
   * complexity O(1).
   * @exception IllegalArgumentException if key is negative.
   * @exception NullPointerException of value is null.
   */
  public final Object put(final int key, final Object value)
  {
    if (key < 0) throw new IllegalArgumentException("key is negative");
    if (value == null) throw new IllegalArgumentException("value is null");
    if (key != m_prevKey) {
      int incr = 0;
      for (m_prevInx = key % m_keys.length;
           m_keys[m_prevInx] >= 0 && m_keys[m_prevInx] != key;
           m_prevInx = (m_prevInx + incr) % m_keys.length)
        if (incr == 0) incr = 1 + (key % (m_keys.length - 1));
      m_prevKey = key; }
    final Object returnVal = m_vals[m_prevInx];
    if (returnVal == null) {
      if (m_elements == m_thresholdSize) {
        incrSize(); return put(key, value); }
      m_elements++; }
    m_vals[m_prevInx] = value;
    m_keys[m_prevInx] = key;
    return returnVal;
  }

  /**
   * Returns the value bound to the specified key or null if no value is
   * currently bound to the specified key.<p>
   * Searches in this hashtable are performed in [amortized] time
   * complexity O(1).
   * @exception IllegalArgumentException if key is negative.
   */
  public final Object get(final int key)
  {
    if (key < 0) throw new IllegalArgumentException("key is negative");
    if (key != m_prevKey) {
      int incr = 0;
      for (m_prevInx = key % m_keys.length;
           m_keys[m_prevInx] >= 0 && m_keys[m_prevInx] != key;
           m_prevInx = (m_prevInx + incr) % m_keys.length)
        if (incr == 0) incr = 1 + (key % (m_keys.length - 1));
      m_prevKey = key; }
    return m_vals[m_prevInx];
  }

  /**
   * Returns an enumeration of keys in this hashtable, ordered
   * arbitrarily.<p>
   * The returned enumeration becomes invalid as soon as put(int, Object)
   * is called on this hashtable; calling methods on an invalid
   * enumeration will cause undefined behavior in the enumerator.
   * The returned enumerator has absolutely no effect on the underlying
   * hashtable.<p>
   * This method returns in constant time.  The returned enumerator
   * returns successive keys in [amortized] time complexity O(1).
   */
  public final IntEnumerator keys()
  {
    final int numElements = m_elements;
    return new IntEnumerator() {
        int elements = numElements;
        int index = -1;
        public final int numRemaining() { return elements; }
        public final int nextInt() {
          while (m_keys[++index] < 0);
          elements--;
          return m_keys[index]; } };
  }

  /**
   * Returns an iteration of values in this hashtable, ordered
   * arbitrarily.<p>
   * The returned iteration becomes invalid as soon as put(int, Object)
   * is called on this hashtable; calling methods on an invalid
   * iteration will cause undefined behavior in the iterator.
   * The returned iterator has absolutely no effect on the underlying
   * hashtable (the remove() operation on the returned iterator is not
   * supported).<p>
   * This method returns in constant time.  The returned iterator
   * returns successive values in [amortized] time complexity O(1).<p>
   * NOTE: The order of values returned corresponds to the order of keys
   * returned by the enumeration from keys() - that is, the nth key returned
   * by keys() is the key into the nth value returned by values().
   */
  public final Iterator values()
  {
    final int numElements = m_elements;
    return new Iterator() {
        int elements = numElements;
        int index = -1;
        public final boolean hasNext() { return elements == 0; }
        public final Object next() {
          try {
            while (m_vals[++index] == null);
            elements--;
            return m_vals[index]; }
          catch (ArrayIndexOutOfBoundsException e) {
            throw new NoSuchElementException(); } }
        public final void remove() {
          throw new UnsupportedOperationException(); } };
  }

  private final void incrSize()
  {
    final int newSize;
    try {
      int primesInx = 0;
      while (m_keys.length != PRIMES[primesInx++]);
      newSize = PRIMES[primesInx]; }
    catch (ArrayIndexOutOfBoundsException e) {
      throw new IllegalStateException
        ("too many elements in this hashtable"); }
    final int[] newKeys = new int[newSize];
    final Object[] newVals = new Object[newSize];
    for (int i = 0; i < newKeys.length; i++) newKeys[i] = -1;
    m_thresholdSize = (int) (THRESHOLD_FACTOR * (double) newKeys.length);
    int incr;
    int newIndex;
    int oldIndex = -1;
    for (int i = 0; i < m_elements; i++) {
      while (m_keys[++oldIndex] < 0);
      incr = 0;
      for (newIndex = m_keys[oldIndex] % newKeys.length;
           newKeys[newIndex] >= 0;
           newIndex = (newIndex + incr) % newKeys.length)
        if (incr == 0) incr = 1 + (m_keys[oldIndex] % (newKeys.length - 1));
      newKeys[newIndex] = m_keys[oldIndex];
      newVals[newIndex] = m_vals[oldIndex]; }
    m_keys = newKeys;
    m_vals = newVals;
    m_prevKey = -1;
    m_prevInx = -1;
  }

}
