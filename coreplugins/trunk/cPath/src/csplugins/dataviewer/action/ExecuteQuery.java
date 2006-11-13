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
package csplugins.dataviewer.action;

import csplugins.dataviewer.model.OrganismOption;
import csplugins.dataviewer.model.SearchBundleList;
import csplugins.dataviewer.model.SearchRequest;
import csplugins.dataviewer.task.QueryCPathTask;
import csplugins.dataviewer.ui.Console;
import csplugins.task.ui.TaskMonitorUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;

/**
 * Executes cPath Searches.
 *
 * @author Ethan Cerami
 */
public class ExecuteQuery extends KeyAdapter implements ActionListener {
    private HashMap cyMap;
    private SearchRequest searchRequest;
    private JFrame parent;
    private QueryCPathTask task;
    private SearchBundleList searchBundleList;
    private JButton searchButton;
    private Console console;

    /**
     * Constructor.
     *
     * @param cyMap        CyMap Object.
     * @param request      SearchRequest Object.
     * @param searchList   List of Searches.
     * @param console      Console Panel.
     * @param searchButton Search Button.
     * @param parent       Parent Component.
     */
    public ExecuteQuery(HashMap cyMap, SearchRequest request,
            SearchBundleList searchList,
            Console console, JButton searchButton, JFrame parent) {
        this.cyMap = cyMap;
        this.searchRequest = request;
        this.parent = parent;
        this.searchBundleList = searchList;
        this.searchButton = searchButton;
        this.console = console;
    }

    /**
     * Execute cPath Query.
     *
     * @param e ActionEvent Object.
     */
    public void actionPerformed(ActionEvent e) {
        executeQuery();
    }

    /**
     * Listen to Key Press Events in Search Box.
     *
     * @param e Key Event.
     */
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == 10) {
            JTextField textField = (JTextField) e.getSource();
            searchRequest.setQuery(textField.getText());
            executeQuery();
        }
    }

    /**
     * Execute Query Against cPath.
     */
    private void executeQuery() {
        if (searchRequest.getQuery().length() == 0
                && searchRequest.getOrganism().equals
                (OrganismOption.ALL_ORGANISMS)) {
            JOptionPane.showMessageDialog(parent,
                    "Please Specify a Keyword and/or an Organism, and "
                            + "try again.", "cPath PlugIn",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Disable search button so that user cannot initiate
            // multiple concurrent searches.
            searchButton.setEnabled(false);
            console.clear();
            //  Instantiate QueryCPathTask
            //  Task runs in a new thread, so that GUI remains responsive.
            task = new QueryCPathTask(cyMap, searchRequest,
                    searchBundleList, console);
            new TaskMonitorUI(task, true,
                    true, true, 0, (Component) parent);
            task.start();
        }
    }
}
