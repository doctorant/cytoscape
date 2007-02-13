
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package fing.model.test;

import fing.model.FingRootGraphFactory;

import giny.model.Edge;
import giny.model.GraphPerspective;
import giny.model.Node;
import giny.model.RootGraph;


/**
 *
 */
public class ARowanBugTest {
	/**
	 *  DOCUMENT ME!
	 *
	 * @param args DOCUMENT ME!
	 */
	public static void main(String[] args) {
		final RootGraph root = FingRootGraphFactory.instantiateRootGraph();
		final int n1 = root.createNode();
		final int n2 = root.createNode();
		final int e1 = root.createEdge(n1, n1, /* directed */
		                               true);
		final GraphPerspective persp = root.createGraphPerspective((int[]) null, (int[]) null);

		if (persp.restoreEdge(e1) == 0)
			throw new IllegalStateException("could not restore valid edge");

		final GraphPerspective persp2 = root.createGraphPerspective(null, new int[] { e1 });

		if (!((persp2.getNodeCount() == 1) && (persp2.getEdgeCount() == 1)))
			throw new IllegalStateException("bad counts in perspective");
	}
}
