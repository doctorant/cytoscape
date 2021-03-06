/*
 File: PopupStringChooser.java

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
package cytoscape.visual.ui;

import java.awt.Component;

import javax.swing.JOptionPane;


/** 
 * An input dialog for strings, ints, and doubles. 
 */
public class PopupStringChooser {

	// from ValueDisplayer
    private static final byte DOUBLE = 4;
    private static final byte INT = 6;

    /**
     *  DOCUMENT ME!
     *
     * @param parent DOCUMENT ME!
     * @param title DOCUMENT ME!
     * @param prompt DOCUMENT ME!
     * @param input DOCUMENT ME!
     * @param type DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Object showDialog(Component parent, String title,
        String prompt, Object input, byte type) {
// keep prompting for input until a valid input is received
inputLoop: 
        while (true) {
            String ret = (String) JOptionPane.showInputDialog(parent, prompt,
                    title, JOptionPane.QUESTION_MESSAGE, null, null, input);

            if (ret == null)
                return null;
            else {
                switch (type) {
                case DOUBLE:

                    try {
                        input = new Double(Double.parseDouble(ret));

                        break inputLoop;
                    } catch (NumberFormatException exc) {
                        JOptionPane.showMessageDialog(parent,
                            "That is not a valid double", "Bad Input",
                            JOptionPane.ERROR_MESSAGE);

                        continue inputLoop;
                    }

                case INT:

                    try {
                        input = new Integer(Integer.parseInt(ret));

                        break inputLoop;
                    } catch (NumberFormatException exc) {
                        JOptionPane.showMessageDialog(parent,
                            "That is not a valid integer", "Bad Input",
                            JOptionPane.ERROR_MESSAGE);

                        continue inputLoop;
                    }

                default: // simple string assignment
                    input = ret;

                    break inputLoop;
                }
            }
        }

        return input;
    }
}
