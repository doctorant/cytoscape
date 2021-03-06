/*
 Copyright (c) 2006, 2007, 2010, The Cytoscape Consortium (www.cytoscape.org)

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
package cytoscape.dialogs.preferences;

import cytoscape.Cytoscape;
import cytoscape.logger.CyLogger;

import cytoscape.bookmarks.Bookmarks;
import cytoscape.bookmarks.DataSource;

import cytoscape.util.BookmarksUtil;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.io.IOException;

import java.net.URL;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import javax.xml.bind.JAXBException;


/**
 *
 */
public class BookmarkDialog extends JDialog implements ActionListener, ListSelectionListener,
                                                       ItemListener {
	private String bookmarkCategory;
	private Bookmarks theBookmarks = null;

	// private Category theCategory = new Category();;
	private String[] bookmarkCategories = { "network", "annotation", "plugins" };
	
	// private URL bookmarkURL;

	/**
	 * Creates new BookmarkDialog
	 *
	 * @throws IOException
	 * @throws JAXBException
	 */
	public BookmarkDialog(JFrame pParent) throws JAXBException, IOException {
		super(pParent, true);
		this.setTitle("Bookmark manager");

		initComponents();
		bookmarkCategory = cmbCategory.getSelectedItem().toString();
		theBookmarks = Cytoscape.getBookmarks();
		loadBookmarks();

		setSize(new Dimension(500, 250));
		this.setLocationRelativeTo(pParent);
	}

	/**
	 * Creates new BookmarkDialog, set the selection on the given category
	 *
	 * @throws IOException
	 * @throws JAXBException
	 */
	public BookmarkDialog(JFrame pParent, String pCategoryName) throws JAXBException, IOException {
		this(pParent);

		Dimension winSize = this.getSize();
		
		// Set the given category the selected item in comboBox
		for (int i=0; i< cmbCategory.getItemCount(); i++) {
			if (cmbCategory.getItemAt(i).toString().equalsIgnoreCase(pCategoryName)) {
				cmbCategory.setSelectedIndex(i);
				this.setPreferredSize(winSize);
				this.pack();
				break;
			}
		}
	}
		
	
	// Variables declaration - do not modify
	private javax.swing.JButton btnAddBookmark;
	private javax.swing.JButton btnDeleteBookmark;
	private javax.swing.JButton btnEditBookmark;
	private javax.swing.JButton btnOK;
	private javax.swing.JComboBox cmbCategory;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JScrollPane jScrollPane1;

	// private javax.swing.JLabel lbTitle;
	private javax.swing.JList listBookmark;

	// End of variables declaration
	private void initComponents() {
		java.awt.GridBagConstraints gridBagConstraints;

		// lbTitle = new javax.swing.JLabel();
		cmbCategory = new javax.swing.JComboBox();
		jScrollPane1 = new javax.swing.JScrollPane();
		listBookmark = new javax.swing.JList();
		jPanel1 = new javax.swing.JPanel();
		btnAddBookmark = new javax.swing.JButton();
		btnEditBookmark = new javax.swing.JButton();
		btnDeleteBookmark = new javax.swing.JButton();
		btnOK = new javax.swing.JButton();

		getContentPane().setLayout(new java.awt.GridBagLayout());

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		// lbTitle.setText("Title");
		// getContentPane().add(lbTitle, new java.awt.GridBagConstraints());
		cmbCategory.setToolTipText("Bookmark category");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 0);
		getContentPane().add(cmbCategory, gridBagConstraints);

		jScrollPane1.setViewportView(listBookmark);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
		getContentPane().add(jScrollPane1, gridBagConstraints);

		jPanel1.setLayout(new java.awt.GridBagLayout());

		btnAddBookmark.setText("Add");
		btnAddBookmark.setToolTipText("Add a new bookmark");
		//btnAddBookmark.setPreferredSize(new java.awt.Dimension(75, 25));
		jPanel1.add(btnAddBookmark, new java.awt.GridBagConstraints());

		btnEditBookmark.setText("Edit");
		btnEditBookmark.setToolTipText("Edit a bookmark");
/*
		btnEditBookmark.setMaximumSize(new java.awt.Dimension(75, 25));
		btnEditBookmark.setMinimumSize(new java.awt.Dimension(75, 25));
		btnEditBookmark.setPreferredSize(new java.awt.Dimension(75, 25));
*/
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridy = 1;
		gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
		jPanel1.add(btnEditBookmark, gridBagConstraints);

		btnDeleteBookmark.setText("Delete");
		btnDeleteBookmark.setToolTipText("Delete a bookmark");
		// btnDeleteBookmark.setMaximumSize(new java.awt.Dimension(63, 25));
		// btnDeleteBookmark.setMinimumSize(new java.awt.Dimension(63, 25));
		//btnDeleteBookmark.setPreferredSize(new java.awt.Dimension(, 25));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridy = 2;
		jPanel1.add(btnDeleteBookmark, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
		getContentPane().add(jPanel1, gridBagConstraints);

		btnOK.setText("OK");
		btnOK.setToolTipText("Close Bookmark dialog");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridy = 3;
		gridBagConstraints.insets = new java.awt.Insets(20, 0, 20, 0);
		getContentPane().add(btnOK, gridBagConstraints);

		for (String AnItem : bookmarkCategories) {
			cmbCategory.addItem(AnItem);
		}

		cmbCategory.addItemListener(this);

		btnEditBookmark.setEnabled(false);
		btnDeleteBookmark.setEnabled(false);

		// add event listeners
		btnOK.addActionListener(this);
		btnAddBookmark.addActionListener(this);
		btnEditBookmark.addActionListener(this);
		btnDeleteBookmark.addActionListener(this);

		listBookmark.addListSelectionListener(this);

		listBookmark.setCellRenderer(new MyListCellRenderer());
		listBookmark.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// pack();
	} // </editor-fold>

	private void loadBookmarks() {
		List<DataSource> theDataSourceList = BookmarksUtil.getDataSourceList(bookmarkCategory,
		                                                                     theBookmarks
		                                                                                                                                                                                                                  .getCategory());

		MyListModel theModel = new MyListModel(theDataSourceList);
		listBookmark.setModel(theModel);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void itemStateChanged(ItemEvent e) {
		bookmarkCategory = cmbCategory.getSelectedItem().toString();
		loadBookmarks();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		Object _actionObject = e.getSource();

		// handle Button events
		if (_actionObject instanceof JButton) {
			JButton _btn = (JButton) _actionObject;

			if (_btn == btnOK) {
				this.dispose();
			} else if (_btn == btnAddBookmark) {
				EditBookmarkDialog theNewDialog = new EditBookmarkDialog(this, true, theBookmarks,
				                                                         bookmarkCategory, "new",
				                                                         null);
				theNewDialog.setLocationRelativeTo(this);

				theNewDialog.setVisible(true);
				loadBookmarks(); // reload is required to update the GUI
			} else if (_btn == btnEditBookmark) {
				DataSource theDataSource = (DataSource) listBookmark.getSelectedValue();
				EditBookmarkDialog theEditDialog = new EditBookmarkDialog(this, true, theBookmarks,
				                                                          bookmarkCategory, "edit",
				                                                          theDataSource);
				theEditDialog.setLocationRelativeTo(this);

				theEditDialog.setVisible(true);
				loadBookmarks(); // reload is required to update the GUI
			} else if (_btn == btnDeleteBookmark) {
				DataSource theDataSource = (DataSource) listBookmark.getSelectedValue();

				MyListModel theModel = (MyListModel) listBookmark.getModel();
				theModel.removeElement(listBookmark.getSelectedIndex());

				BookmarksUtil.deleteBookmark(theBookmarks, bookmarkCategory, theDataSource);

				if (theModel.getSize() == 0) {
					btnEditBookmark.setEnabled(false);
					btnDeleteBookmark.setEnabled(false);
				}
			}
		}
	}

	/**
	 * Called by ListSelectionListener interface when a table item is selected.
	 *
	 * @param pListSelectionEvent
	 */
	public void valueChanged(ListSelectionEvent pListSelectionEvent) {
		if (listBookmark.getSelectedIndex() == -1) { // nothing is selected
			btnEditBookmark.setEnabled(false);
			btnDeleteBookmark.setEnabled(false);
		} else {
			// enable buttons
			btnEditBookmark.setEnabled(true);
			btnDeleteBookmark.setEnabled(true);
		}
	}

	class MyListModel extends javax.swing.AbstractListModel {
		List<DataSource> theDataSourceList = new ArrayList<DataSource>(0);

		public MyListModel(List<DataSource> pDataSourceList) {
			theDataSourceList = pDataSourceList;
		}

		public int getSize() {
			if (theDataSourceList == null) {
				return 0;
			}

			return theDataSourceList.size();
		}

		public Object getElementAt(int i) {
			if (theDataSourceList == null) {
				return null;
			}

			return theDataSourceList.get(i);
		}

		public void addElement(DataSource pDataSource) {
			theDataSourceList.add(pDataSource);
		}

		public void removeElement(int pIndex) {
			theDataSourceList.remove(pIndex);
			fireContentsChanged(this, pIndex, pIndex);
		}
	} // MyListModel

	// class MyListCellrenderer
	class MyListCellRenderer extends JLabel implements ListCellRenderer {
		public MyListCellRenderer() {
			setOpaque(true);
		}

		public Component getListCellRendererComponent(JList list, Object value, int index,
		                                              boolean isSelected, boolean cellHasFocus) {
			DataSource theDataSource = (DataSource) value;
			setText(theDataSource.getName());
			setToolTipText(theDataSource.getHref());
			setBackground(isSelected ? Color.red : Color.white);
			setForeground(isSelected ? Color.white : Color.black);

			return this;
		}
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JDialog theDialog = new BookmarkDialog();
		theDialog.setPreferredSize(new Dimension(350, 400));
		theDialog.pack();
		theDialog.setVisible(true);
	}

	// for test only
	/**
	 * Creates a new BookmarkDialog object.
	 */
	public BookmarkDialog() {
		this.setTitle("Bookmark manager");

		theBookmarks = getTestBookmarks();
		initComponents();
		bookmarkCategory = cmbCategory.getSelectedItem().toString();
		// theBookmarks = Cytoscape.getBookmarks();
		loadBookmarks();
	}

	// For test only, remove after test pass
	private Bookmarks getTestBookmarks() {
		Bookmarks tmpBookmarks = null;

		java.io.File tmpBookmarkFile = new java.io.File("bookmarks_kei.xml");
		CyLogger.getLogger().info("tmpBookmarkFile =" + tmpBookmarkFile.getAbsolutePath());

		// Load the Bookmarks object from given xml file
		try {
			tmpBookmarks = BookmarksUtil.getBookmarks(tmpBookmarkFile.toURL());
		} catch (IOException e) {
			CyLogger.getLogger().info("IOException -- bookmarkSource");
		} catch (JAXBException e) {
			CyLogger.getLogger().info("JAXBException -- bookmarkSource");
		} catch (Exception e) {
			CyLogger.getLogger().info("Can not read the bookmark file, the bookmark file may not exist!");
		}

		return tmpBookmarks;
	}
}
