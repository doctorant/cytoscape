/** Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center 
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center 
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package csplugins.dataviewer.plugin;

import csplugins.dataviewer.ui.CPathDesktop;
import csplugins.dataviewer.util.SynchronizeProperties;
import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.CyMenus;
import cytoscape.view.CytoscapeDesktop;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * PlugIn For exchanging Data with CPath.
 *
 * @author Robert Sheridan, Ethan Cerami
 */
public class CPathPlugIn extends CytoscapePlugin implements ActionListener {
    private CPathDesktop cPathDesktop;

    /**
     * Constructor.
     * This method is called by the main Cytoscape Application upon startup.
     */
    public CPathPlugIn() {
        SynchronizeProperties.synchProperties();
        CytoscapeDesktop desktop = Cytoscape.getDesktop();
        CyMenus cyMenus = desktop.getCyMenus();
        JMenu plugInMenu = cyMenus.getNewNetworkMenu();
        JMenuItem menuItem = new JMenuItem("Construct network using cPath...");
        plugInMenu.add(menuItem);
        menuItem.addActionListener(this);
        cPathDesktop = new CPathDesktop((JFrame) desktop);
    }

    /**
     * User Has Selected the cPath Plug from the PlugIn Menu.
     *
     * @param e ActionEvent Object.
     */
    public void actionPerformed(ActionEvent e) {
        if (cPathDesktop == null) {
            CytoscapeDesktop desktop = Cytoscape.getDesktop();
            cPathDesktop = new CPathDesktop((JFrame) desktop);
        }
        cPathDesktop.setVisible(true);
    }
}
