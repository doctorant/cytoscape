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

import csplugins.dataviewer.model.MaxHitsOption;
import csplugins.dataviewer.model.OrganismOption;
import csplugins.dataviewer.model.SearchRequest;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * Updates the SearchRequest Object based on User Input.
 *
 * @author Ethan Cerami
 */
public class UpdateSearchRequest extends FocusAdapter
        implements ActionListener {

    /**
     * Search Request Object.
     */
    private SearchRequest searchRequest;

    /**
     * Constructor.
     *
     * @param searchRequest Search Request Object.
     */
    public UpdateSearchRequest(SearchRequest searchRequest) {
        this.searchRequest = searchRequest;
    }

    /**
     * User has selected an item from the pull-down menu.
     *
     * @param e ActionEvent.
     */
    public void actionPerformed(ActionEvent e) {
        JComboBox cb = (JComboBox) e.getSource();

        Object object = cb.getSelectedItem();
        if (object instanceof MaxHitsOption) {
            MaxHitsOption maxHitsOption = (MaxHitsOption) object;
            searchRequest.setMaxHits(maxHitsOption);
        } else if (object instanceof OrganismOption) {
            OrganismOption organismOption = (OrganismOption) object;
            searchRequest.setOrganism(organismOption);
        }
    }

    /**
     * Users has Entered New Text in the Search Text Box.
     *
     * @param e Focus Event.
     */
    public void focusLost(FocusEvent e) {
        JTextField textField = (JTextField) e.getSource();
        searchRequest.setQuery(textField.getText());
    }
}