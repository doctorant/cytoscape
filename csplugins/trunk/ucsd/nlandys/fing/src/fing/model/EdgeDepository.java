package fing.model;

final class EdgeDepository
{

  private final static int INITIAL_CAPACITY = 11; // Must be non-negative.

  private FEdge[] m_edgeStack;
  private int m_size;

  EdgeDepository()
  {
    m_edgeStack = new FEdge[INITIAL_CAPACITY];
    m_size = 0;
  }

  // Gimme an edge, darnit!
  // Don't forget to initialize the edge's member variables!
  FEdge getEdge()
  {
    if (m_size == 0) { return new FEdge(); }
    else { return m_edgeStack[--m_size]; }
  }

  // Deinitialize the object's members yourself if you need or want to.
  void recycleEdge(FEdge edge)
  {
    if (m_size == m_edgeStack.length) {
      final int newArrSize = (int)
        Math.min((long) Integer.MAX_VALUE,
                 ((long) m_edgeStack.length) * 2l + 1l);
      if (newArrSize == m_edgeStack.length)
        throw new IllegalStateException
          ("unable to allocate large enough array");
      FEdge[] newArr = new FEdge[newArrSize];
      System.arraycopy(m_edgeStack, 0, newArr, 0, m_edgeStack.length);
      m_edgeStack = newArr; }
    m_edgeStack[m_size++] = edge;
  }

}
