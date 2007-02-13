
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

package ding.view;

import giny.view.NodeView;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

import java.util.*;

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoableEdit;


/**
 * A Ding specific undoable edit.
 */
class DingUndoableEdit extends AbstractUndoableEdit {
	private double m_orig_xCenter;
	private double m_orig_yCenter;
	private double m_orig_scaleFactor;
	private Map<NodeView, Point2D.Double> m_orig_points;
	private double m_new_xCenter;
	private double m_new_yCenter;
	private double m_new_scaleFactor;
	private Map<NodeView, Point2D.Double> m_new_points;
	private DGraphView m_view;

	DingUndoableEdit(DGraphView view) {
		super();
		m_view = view;

		m_orig_points = new HashMap<NodeView, Point2D.Double>();
		m_new_points = new HashMap<NodeView, Point2D.Double>();
	}

	void saveOldPositions(double x, double y, double scale, Iterator nvi) {
		//System.out.println("save old positions");
		m_orig_xCenter = x;
		m_orig_yCenter = y;
		m_orig_scaleFactor = scale;
		m_orig_points.clear();

		while (nvi.hasNext()) {
			NodeView nv = (NodeView) nvi.next();
			m_orig_points.put(nv, new Point2D.Double(nv.getXPosition(), nv.getYPosition()));
		}
	}

	void saveNewPositions(double x, double y, double scale, Iterator nvi) {
		//System.out.println("save new positions");
		m_new_xCenter = x;
		m_new_yCenter = y;
		m_new_scaleFactor = scale;
		m_new_points.clear();

		while (nvi.hasNext()) {
			NodeView nv = (NodeView) nvi.next();
			m_new_points.put(nv, new Point2D.Double(nv.getXPosition(), nv.getYPosition()));
		}
	}

	/**
	 * @return Not sure where this is used.
	 */
	public String getPresentationName() {
		return "Move";
	}

	/**
	 * @return Used in the edit menu.
	 */
	public String getRedoPresentationName() {
		return "Redo: Move";
	}

	/**
	 * @return Used in the edit menu.
	 */
	public String getUndoPresentationName() {
		return "Undo: Move";
	}

	/**
	 * Applies the new state to the view after it has been undone.
	 */
	public void redo() {
		super.redo();

		Iterator it = m_view.getNodeViewsIterator();

		while (it.hasNext()) {
			NodeView nv = (NodeView) it.next();
			Point2D.Double p = m_new_points.get(nv);
			nv.setXPosition(p.getX());
			nv.setYPosition(p.getY());
		}

		m_view.setZoom(m_new_scaleFactor);
		m_view.setCenter(m_new_xCenter, m_new_yCenter);
		m_view.getCanvas().repaint();
	}

	/**
	 * Applies the original state to the view.
	 */
	public void undo() {
		super.undo();

		Iterator it = m_view.getNodeViewsIterator();

		while (it.hasNext()) {
			NodeView nv = (NodeView) it.next();
			Point2D.Double p = m_orig_points.get(nv);
			nv.setXPosition(p.getX());
			nv.setYPosition(p.getY());
		}

		m_view.setZoom(m_orig_scaleFactor);
		m_view.setCenter(m_orig_xCenter, m_orig_yCenter);
		m_view.getCanvas().repaint();
	}
}
