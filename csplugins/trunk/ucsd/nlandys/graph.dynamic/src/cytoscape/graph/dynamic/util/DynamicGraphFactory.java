package cytoscape.graph.dynamic.util;

import cytoscape.graph.dynamic.DynamicGraph;

/**
 * A factory for getting cytoscape.graph.dynamic.DynamicGraph instances.
 * This DynamicGraph implementation requires a bare minimum of roughly 64
 * metabytes for a graph with one million edges and one hundred thousand nodes.
 * That is, the memory requirements are roughly 64 bytes per node and edge.<p>
 * The returned DynamicGraph does not free up memory.  That is, if a
 * DynamicGraph contains one million edges and one hundred thousand nodes,
 * it consumes roughly 64 megabytes.  If all nodes and edges are then removed
 * from the DynamicGraph, it will still consume roughly 64 megabytes of
 * memory.  If one hundred thousand nodes and one million edges are
 * then created, the DynamicGraph will still consume roughly 64 megabytes of
 * memory.  If all nodes and edges are removed and then re-created, repeatedly
 * over an infinite number of iterations, the overall memory consumption
 * required by this DynamicGraph will remain constant.<p>
 * Below are time complexities of DynamicGraph methods:
 * <blockquote><table border=1 cellspacing=0 cellpadding=5>
 * <tr><th>DynamicGraph method</th><th>time complexity</th></tr>
 * <tr>
 * <td>nodes()</td>
 * <td>An IntEnumerator is returned in constant time.  The enumerator
 *     returns each successive element in constant time.  The enumerator
 *     reports the number of elements remaining in constant time.</td>
 * </tr><tr>
 * <td>edges()</td>
 * <td>An IntEnumerator is returned in constant time.  The enumerator
 *     returns each successive element in constant time.  The enumerator
 *     reports the number of elements remaining in constant time.</td>
 * </tr><tr>
 * <td>nodeCreate()</td>
 * <td>A node is created in amortized constant time.  Amortized because
 *     doubling the size of an underlying array will be necessary as
 *     the DynamicGraph increases in its total node count.</td>
 * </tr><tr>
 * <td>nodeRemove(int)</td>
 * <td>A node is removed in amortized O(E) time, where E is the number of
 *     edges touching the node being removed.  Amortized because a data
 *     structure is used to keep track of free node slots; doubling
 *     the size of this data structure is necessary at certain points.</td>
 * </tr><tr>
 * <td>edgeCreate(int, int, boolean)</td>
 * <td>An edge is created in amortized constant time.  Amortized because
 *     doubling the size of an underlying array will be necessary as the
 *     DynamicGraph increases in its total edge count.</td>
 * </tr><tr>
 * <td>edgeRemove(int)</td>
 * <td>An edge is removed in amortized constant time.  Amortized because
 *     a data structure is used to keep track of free edge slots; doubling
 *     the size of this data structure is necessary at certain points.</td>
 * </tr><tr>
 * <td>nodeExists(int)</td>
 * <td>The existence of a node is determined in constant time.</td>
 * </tr><tr>
 * <td>edgeType(int)</td>
 * <td>The existence/type of an edge is determined in constant time.</td>
 * </tr><tr>
 * <td>edgeSource(int)</td>
 * <td>The source node of an edge is determined in constant time.</td>
 * </tr><tr>
 * <td>edgeTarget(int)</td>
 * <td>The target node of an edge is determined in constant time.</td>
 * </tr><tr>
 * <td>edgesAdjacent(int, boolean, boolean, boolean)</td>
 * <td>An IntEnumerator is returned in constant time.  The enumerator
 *     always reports the number of elements remaining in constant time.
 *     The enumeration can be completely traversed in O(E) time, where E is
 *     the number of edges touching the node in question.  Each individual
 *     successive element is returned by the enumeration in average
 *     O(E/F) time, where F is the total number of elements in this
 *     enumeration.</td>
 * </tr><tr>
 * <td>edgesConnecting(int, int, boolean, boolean, boolean)</td>
 * <td>An IntIterator is returned in constant time.  The iteration can be
 *     completely traversed in O(min(E, F)) time, where E is the total number
 *     of edges touching one node and F is the total number of edges touching
 *     the other node.</td>
 * </tr></table></blockquote>
 */
public final class DynamicGraphFactory
{

  // "No constructor".
  private DynamicGraphFactory() { }

  /**
   * Nodes and edges created by the returned DynamicGraph are strictly less
   * than Integer.MAX_VALUE.
   */
  public static DynamicGraph instantiateDynamicGraph()
  {
    return new DynamicGraphRepresentation();
  }

}
