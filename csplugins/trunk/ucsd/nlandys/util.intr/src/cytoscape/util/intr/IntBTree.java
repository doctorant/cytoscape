package cytoscape.util.intr;

/**
 * This is a B+-tree that stores integers.
 */
public final class IntBTree
{

  // This quantity must be at least 3.
  // The author prefers that this quantity be odd because that way nodes
  // are split evenly when they get full.
  private final static int MAX_BRANCHES = 3;

  private Node m_root;

  public IntBTree()
  {
    m_root = new Node(MAX_BRANCHES, true);
  }

  /**
   * Empties this structure of all elements.
   */
  public final void empty()
  {
    m_root = new Node(MAX_BRANCHES, true);
  }

  /**
   * Returns the number of elements currently in this structure.  Duplicate
   * entries are counted however many times they are present.
   */
  public final int size()
  {
    return isLeafNode(m_root) ? m_root.sliceCount : m_root.data.deepCount;
  }

  /*
   * Perhaps this should be inlined later for performance.
   */
  private final boolean isLeafNode(Node n)
  {
    return n.data == null;
  }

  /**
   * Inserts a new entry into this structure.  Duplicate entries are allowed.
   * @param x the new entry to insert.
   */
  public final void insert(int x)
  {
    Node newSibling = insert(m_root, x);
    if (newSibling != null) { // The root has been split into two.
      int newSplitVal;
      int newDeepCount;
      if (isLeafNode(newSibling)) {
        newSplitVal = newSibling.values[0];
        newDeepCount = m_root.sliceCount + newSibling.sliceCount; }
      else {
        newSplitVal = m_root.data.splitVals[m_root.sliceCount - 1];
        newDeepCount = m_root.data.deepCount + newSibling.data.deepCount; }
      Node newRoot = new Node(MAX_BRANCHES, false);
      newRoot.sliceCount = 2;
      newRoot.data.deepCount = newDeepCount;
      newRoot.data.splitVals[0] = newSplitVal;
      newRoot.data.children[0] = m_root;
      newRoot.data.children[1] = newSibling;
      m_root = newRoot; }
  }

  /*
   * Returns a node being the newly created node if a split was performed;
   * the node returned is the right sibling of node n.  If the returned node
   * is a leaf node then the first value of the node is to be the new split
   * index; if return value is internal node, then the split index to be used
   * is n.data.splitVals[n.sliceCount - 1].  (This is something that this
   * method sets; it's this method saying "use this index in the higher
   * levels".)
   */
  private final Node insert(Node n, int x)
  {
    if (isLeafNode(n))
    {
      if (n.sliceCount < MAX_BRANCHES) { // There's room for a value.
        boolean found = false;
        for (int i = 0; i < n.sliceCount; i++) {
          if (x <= n.values[i]) {
            for (int j = n.sliceCount; j > i;) n.values[j] = n.values[--j];
            n.values[i] = x; found = true; break; } }
        if (!found) n.values[n.sliceCount] = x;
        n.sliceCount++;
        return null; }
      else { // No room for another value in this leaf node; perform split.
        Node newNode = new Node(MAX_BRANCHES, true);
        int combinedCount = MAX_BRANCHES + 1;
        n.sliceCount = combinedCount >> 1; // Divide by two.
        newNode.sliceCount = combinedCount - n.sliceCount;
        split(x, n.values, newNode.values, newNode.sliceCount);
        return newNode; }
    }
    else
    { // Not a leaf node.
      int foundPath = 0;
      for (int i = n.sliceCount - 2; i >= 0; i--) {
        if (x >= n.data.splitVals[i]) {
          foundPath = i + 1;
          break; } }
      Node oldChild = n.data.children[foundPath];
      Node newChild = insert(oldChild, x);
      if (newChild == null) {
        n.data.deepCount++;
        return null; }
      else
      { // A split was performed at one level deeper.
        int newSplit;
        if (isLeafNode(newChild)) newSplit = newChild.values[0];
        else newSplit = oldChild.data.splitVals[oldChild.sliceCount - 1];
        if (n.sliceCount < MAX_BRANCHES) { // There's room here.
          for (int j = n.sliceCount - 1; j > foundPath;) {
            n.data.children[j + 1] = n.data.children[j];
            n.data.splitVals[j] = n.data.splitVals[--j]; }
          n.sliceCount++;
          n.data.deepCount++;
          n.data.children[foundPath + 1] = newChild;
          n.data.splitVals[foundPath] = newSplit;
          return null; }
        else { // No room in this internal node; perform split.
          Node newNode = new Node(MAX_BRANCHES, false);
          int combinedCount = MAX_BRANCHES + 1;
          n.sliceCount = combinedCount >> 1; // Divide by two.
          newNode.sliceCount = combinedCount - n.sliceCount;
          split(newChild, foundPath, n.data.children,
                newNode.data.children, newNode.sliceCount);
          split(newSplit, n.data.splitVals,
                newNode.data.splitVals, newNode.sliceCount - 1);
          n.data.deepCount = 0; // Update the deep count.
          if (isLeafNode(newChild)) {
            for (int i = 0; i < n.sliceCount; i++)
              n.data.deepCount += n.data.children[i].sliceCount;
            for (int i = 0; i < newNode.sliceCount; i++)
              newNode.data.deepCount += newNode.data.children[i].sliceCount; }
          else {
            for (int i = 0; i < n.sliceCount; i++)
              n.data.deepCount += n.data.children[i].data.deepCount;
            for (int i = 0; i < newNode.sliceCount; i++)
              newNode.data.deepCount +=
                newNode.data.children[i].data.deepCount; }
          return newNode; }
      }
    }
  }

  /*
   * It's tedious to rigorously define what this method does.  I give an
   * example:
   *
   *
   *   INPUTS
   *   ======
   *
   *   newVal: 5
   *
   *             +---+---+---+---+---+---+---+
   *   origBuff: | 0 | 2 | 3 | 6 | 6 | 8 | 9 |
   *             +---+---+---+---+---+---+---+
   *
   *                 +---+---+---+---+---+---+---+
   *   overflowBuff: | / | / | / | / | / | / | / |
   *                 +---+---+---+---+---+---+---+
   *
   *   overflowCount: 4
   *
   *
   *   OUTPUTS
   *   =======
   *
   *             +---+---+---+---+---+---+---+
   *   origBuff: | 0 | 2 | 3 | 5 | / | / | / |
   *             +---+---+---+---+---+---+---+
   *
   *                 +---+---+---+---+---+---+---+
   *   overflowBuff: | 6 | 6 | 8 | 9 | / | / | / |
   *                 +---+---+---+---+---+---+---+
   */
  private final void split(int newVal, int[] origBuff,
                           int[] overflowBuff, int overflowCount)
  {
    int[] currentArr = overflowBuff;
    int currentInx = overflowCount;
    boolean found = false;
    for (int i = origBuff.length - 1; i >= 0; i--) {
      if ((!found) && (newVal >= origBuff[i])) {
        currentArr[--currentInx] = newVal;
        found = true;
        if (currentArr == origBuff) break;
        i++; }
      else { currentArr[--currentInx] = origBuff[i]; }
      if (currentInx == 0) {
        if (found) break;
        currentArr = origBuff;
        currentInx = origBuff.length - overflowCount + 1; } }
    if (!found) currentArr[0] = newVal;
  }

  /*
   * It's tedious to rigorously define what this method does.  I give an
   * example:
   *
   *
   *   INPUTS
   *   ======
   *
   *   newNode: Z
   *
   *   newInx: 5
   *
   *              +---+---+---+---+---+---+---+
   *   origNodes: | Q | I | E | A | Y | N | W |
   *              +---+---+---+---+---+---+---+
   *
   *                  +---+---+---+---+---+---+---+
   *   overflowNodes: | / | / | / | / | / | / | / |
   *                  +---+---+---+---+---+---+---+
   *
   *   overflowCount: 4
   *
   *
   *   OUTPUTS
   *   =======
   *
   *             +---+---+---+---+---+---+---+
   *   origBuff: | Q | I | E | A | / | / | / |
   *             +---+---+---+---+---+---+---+
   *
   *                 +---+---+---+---+---+---+---+
   *   overflowBuff: | Y | N | Z | W | / | / | / |
   *                 +---+---+---+---+---+---+---+
   *
   *   In addition, the "unused" entries in origBuff are nulled out (remove
   *   pointers to enable garbage collection).
   *
   *   Note tht newInx means to put the new node after the existing node
   *   at index newInx in the original array.  Placing the new node before
   *   every other node would entail specifying newInx as -1, which is not
   *   allowed.
   */
  private final void split(Node newNode, int newInx, Node[] origNodes,
                           Node[] overflowNodes, int overflowCount)
  {
    Node[] currentNodes = overflowNodes;
    int currentInx = overflowCount;
    for (int i = origNodes.length - 1; i >= 0; i--) {
      if ((newNode != null) && (i == newInx)) {
        currentNodes[--currentInx] = newNode;
        newNode = null;
        if (currentNodes == origNodes) break;
        i++; }
      else { currentNodes[--currentInx] = origNodes[i]; }
      if (currentInx == 0) {
        if (newNode == null) break;
        currentNodes = origNodes;
        currentInx = origNodes.length - overflowCount + 1; } }
    for (int i = origNodes.length - overflowCount + 1;
         i < origNodes.length; i++)
      origNodes[i] = null; // Remove dangling pointers for garbage collection.
  }

//   /**
//    * Deletes at most one entry of the integer x.  To delete all
//    * entries of the integer x, use deleteRange(x, 1).
//    * @param x the integer to try to delete (just one entry).
//    * @return true if and only if an entry was deleted (at most one entry is
//    *   deleted by this method).
//    */
//   public boolean delete(int x)
//   {
//     return false;
//   }

//   /**
//    * Deletes all entries in the range [xStart, xStart + spanSize)
//    * from this structure.
//    * @param xStart specifies the beginning of the range of integers to
//    *   delete from this structure.
//    * @param spanSize specifies the range width of integers to delete; all
//    *   integers greater than or equal to xStart but less than xStart + spanSize
//    *   will be deleted; spanSize cannot be negative
//    *   (if spanSize is zero no action is taken).
//    * @return the number of entries that were deleted from this structure.
//    * @exception IllegalArgumentException if spanSize is negative.
//    */
//   public int deleteRange(int xStart, int spanSize)
//   {
//     return 0;
//   }

  /**
   * Returns the number of entries of the integer x in this tree.
   * This method is superfluous because we can use searchRange() to
   * get the same information.
   * @param x the integer whose count to query.
   * @return the number of entries x currently in this structure.
   */
  public final int count(int x)
  {
    return count(m_root, x, Integer.MIN_VALUE, Integer.MAX_VALUE);
  }

  private final int count(Node n, int x, int minBound, int maxBound)
  {
    int count = 0;
    if (isLeafNode(n)) {
      for (int i = -1; i < n.sliceCount;)
        if (x >= n.values[++i]) {
          if (x == n.values[i]) count++;
          else break; }
      return count; }
    else { // Internal node.
      int currentMax = maxBound;
      for (int i = n.sliceCount - 2; i >= -1; i--) {
        int currentMin;
        if (i < 0) currentMin = minBound;
        else currentMin = n.data.splitVals[i];
        if (x >= currentMin) {
          if (currentMin == currentMax) {
            count += n.data.children[i + 1].data.deepCount; }
          else {
            count += count(n.data.children[i + 1], x,
                           currentMin, currentMax);
            if (currentMin < x) break; } }
        currentMax = currentMin; }
      return count; }
  }

  /**
   * Returns an enumeration of all entries in the range
   * [xStart, xStart + spanSize) currently in this structure; the entries
   * within the enumeration are returned in non-descending order.<p>
   * IMPORTANT: The returned enumeration becomes invalid as soon as any
   * structure-modifying operation (insert or delete) is performed on this
   * tree.  Accessing an invalid enumeration's methods will result in
   * unpredictable and ill-defined behavior in the enumeration, but will
   * have no effect on the integrity of this tree structure.
   * @param xStart specifies the beginning of the range of integers to
   *   search.
   * @param spanSize specifies the range width of integers to search;
   *   all integers (duplicates included) greater than or equal to xStart
   *   but less than xStart + spanSize will be returned; spanSize cannot be
   *   negative (if spanSize is zero no action is taken).
   * @return an enumeration of all entries matching this search query.
   * @exception IllegalArgumentException if spanSize is negative.
   */
  public IntEnumerator searchRange(int xStart, int spanSize)
  {
    return null;
  }

  public void debugPrint()
  {
    java.util.Vector v = new java.util.Vector();
    v.add(m_root);
    while (true) {
      v = debugPrint_level(v);
      if (v.size() == 0) break; }
    System.out.print("total count: ");
    if (isLeafNode(m_root))
      System.out.println(m_root.sliceCount);
    else
      System.out.println(m_root.data.deepCount);
  }

  private java.util.Vector debugPrint_level(java.util.Vector v)
  {
    java.util.Vector returnThis = new java.util.Vector();
    while (v.size() > 0) {
      Node n = (Node) v.remove(0);
      if (!isLeafNode(n)) {
        for (int i = 0; i < n.sliceCount; i++) {
          returnThis.add(n.data.children[i]); } }
      debugPrint_node(n); }
    System.out.println();
    return returnThis;
  }

  private void debugPrint_node(Node n)
  {
    if (isLeafNode(n)) {
      System.out.print(" [");
      for (int i = 0; i < n.sliceCount - 1; i++) {
        System.out.print(n.values[i] + " "); }
      if (n.sliceCount > 0) System.out.print(n.values[n.sliceCount - 1]);
      System.out.print("]"); }
    else {
      System.out.print(" <.");
      for (int i = 0; i < n.sliceCount - 1; i++) {
        System.out.print(n.data.splitVals[i] + "."); }
      System.out.print(">"); }
  }

  private final static class Node
  {

    private int sliceCount = 0;

    // Exactly one of { values, data } is null, depending on whether or not
    // this is a leaf node.
    private final int[] values;
    private final InternalNodeData data;

    private Node(int maxBranches, boolean leafNode)
    {
      if (leafNode) {
        values = new int[maxBranches];
        data = null; }
      else {
        values = null;
        data = new InternalNodeData(maxBranches); }
    }

  }

  private final static class InternalNodeData
  {

    private int deepCount;
    private final int[] splitVals;
    private final Node[] children;

    private InternalNodeData(int maxBranches)
    {
      splitVals = new int[maxBranches - 1];
      children = new Node[maxBranches];
    }

  }

  public static void main(String[] args)
  {
    IntBTree tree = new IntBTree();
    for (int i = 0; i < args.length; i++) {
      int entry = Integer.parseInt(args[i]);
      tree.insert(entry); }
    tree.debugPrint();
  }

}
