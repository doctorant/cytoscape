package cytoscape.graph.dynamic.util;

import cytoscape.graph.dynamic.DynamicGraph;
import cytoscape.util.intr.IntEnumerator;

class DynamicGraphRepresentation implements DynamicGraph
{

  private int m_nodeCount;
  private Node m_firstNode;
  private int m_edgeCount;

  DynamicGraphRepresentation()
  {
    m_nodeCount = 0;
    m_firstNode = null;
    m_edgeCount = 0;
  }

  public IntEnumerator nodes()
  {
    final int nodeCount = m_nodeCount;
    final Node firstNode = m_firstNode;
    return new IntEnumerator() {
        private int numRemaining = nodeCount;
        private Node node = firstNode;
        public int numRemaining() { return numRemaining; }
        public int nextInt() {
          final int returnThis = node.m_nodeId;
          node = node.m_nextNode;
          numRemaining--;
          return returnThis; } };
  }

  public IntEnumerator edges()
  {
    final int edgeCount = m_edgeCount;
    final Node firstNode = m_firstNode;
    return new IntEnumerator() {
        private int numRemaining = edgeCount;
        private Node node = firstNode;
        private Edge edge = null;
        public int numRemaining() { return numRemaining; }
        public int nextInt() {
          int returnThis;
          try { returnThis = edge.m_edgeId; }
          catch (NullPointerException e) {
            for (edge = node.m_firstOutEdge;
                 edge == null;
                 node = node.m_nextNode, edge = node.m_firstOutEdge) { }
            node = node.m_nextNode;
            returnThis = edge.m_edgeId; }
          edge = edge.m_nextAdjEdge;
          numRemaining--;
          return returnThis; } };
  }

  public boolean removeNode(int node)
  {
    return false;
  }

  public int createNode()
  {
    return -1;
  }

  public boolean removeEdge(int edge)
  {
    return false;
  }

  public int createEdge(int sourceNode, int targetNode, boolean directed)
  {
    return -1;
  }

  public boolean containsNode(int node)
  {
    return false;
  }

  public boolean containsEdge(int edge)
  {
    return false;
  }

  public IntEnumerator adjacentEdges(int node, boolean undirected,
                                     boolean incoming, boolean outgoing)
  {
    return null;
  }

  public int sourceNode(int edge)
  {
    return -1;
  }

  public int targetNode(int edge)
  {
    return -1;
  }

  public byte isDirectedEdge(int edge)
  {
    return (byte) -1;
  }

}
