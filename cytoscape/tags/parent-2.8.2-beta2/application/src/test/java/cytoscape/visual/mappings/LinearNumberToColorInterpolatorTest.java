/*
  File: LinearNumberToColorInterpolatorTest.java

  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

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

// LinearNumberToColorInterpolatorTest.java

//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.mappings;

import cytoscape.visual.mappings.LinearNumberToColorInterpolator;

//----------------------------------------------------------------------------
import junit.framework.*;

import java.awt.Color;

import java.io.*;


//----------------------------------------------------------------------------
/**
 *
 */
public class LinearNumberToColorInterpolatorTest extends TestCase {
	//----------------------------------------------------------------------------
	/**
	 * Creates a new LinearNumberToColorInterpolatorTest object.
	 *
	 * @param name  DOCUMENT ME!
	 */
	public LinearNumberToColorInterpolatorTest(String name) {
		super(name);
	}

	//----------------------------------------------------------------------------
	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void setUp() throws Exception {
	}

	//----------------------------------------------------------------------------
	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void tearDown() throws Exception {
	}

	//----------------------------------------------------------------------------
	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void testFunction() throws Exception {
		LinearNumberToColorInterpolator li = new LinearNumberToColorInterpolator();
		Color c1 = new Color(0, 10, 20, 30);
		Color c2 = new Color(201, 191, 179, 169);

		Object returnVal = li.getRangeValue(0.65, c1, c2);
		assertTrue(returnVal instanceof Color);

		Color cReturn = (Color) returnVal;
		assertTrue(cReturn.getRed() == 131);
		assertTrue(cReturn.getGreen() == 128);
		assertTrue(cReturn.getBlue() == 123);
		assertTrue(cReturn.getAlpha() == 120);

		Object dummy = new Object();
		returnVal = li.getRangeValue(0.65, c1, dummy);
		assertTrue(returnVal == null);
	}

	//---------------------------------------------------------------------------
	/**
	 *  DOCUMENT ME!
	 *
	 * @param args DOCUMENT ME!
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(new TestSuite(LinearNumberToColorInterpolatorTest.class));
	}

	//----------------------------------------------------------------------------
}
