/*
  File: MaxTest.java

  Copyright (c) 2010, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.equations.internal.builtins;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.*;


public class MaxTest extends TestCase {
	public void testAll() throws Exception {
                final List<Object> numbers = new ArrayList<Object>();
                numbers.add(Double.valueOf(1.0));
                numbers.add(Integer.valueOf(2));
                numbers.add(Double.valueOf(3.0));
                numbers.add("4.0");
                numbers.add(Double.valueOf(5.0));
		final Map<String, Object> variablesAndValues = new HashMap<String, Object>();
		variablesAndValues.put("numbers", numbers);
		assertTrue(Framework.executeTest("=MAX($numbers)", variablesAndValues, Double.valueOf(5.0)));
		assertTrue(Framework.executeTest("=MAX(-2,-3,-4.35)", Double.valueOf(-2)));
		assertTrue(Framework.executeTest("=MAX(-1.3)", Double.valueOf(-1.3)));
		assertTrue(Framework.executeTest("=MAX(0.0)", Double.valueOf(0.0)));
		assertTrue(Framework.executeTestExpectFailure("=MAX()"));
	}
}
