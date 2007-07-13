
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

package edu.ucsd.bioeng.coreplugin.tableImport.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cytoscape.util.URLUtil;


/**
 * Basic text table reader for attributes.<br>
 *
 * <p>
 * based on the given parameters, map the text table to CyAttributes.
 * </p>
 *
 * @author kono
 *
 */
public class DefaultAttributeTableReader implements TextTableReader {
	/**
	 * Lines begin with this charactor will be considered as comment lines.
	 */
	private static final int DEF_KEY_COLUMN = 0;
	private final URL source;
	private AttributeMappingParameters mapping;
	private final AttributeLineParser parser;

	// Number of mapped attributes.
	private int globalCounter = 0;

	/*
	 * Reader will read entries from this line.
	 */
	private final int startLineNumber;
	private String commentChar = null;

	/**
	 * Constructor.<br>
	 *
	 * @param source
	 * @param objectType
	 * @param delimiters
	 * @throws Exception
	 */
	public DefaultAttributeTableReader(final URL source, final ObjectType objectType,
	                                   final List<String> delimiters) throws Exception {
		this(source, objectType, delimiters, null, DEF_KEY_COLUMN, null, null, null, null, null, 0);
	}

	/**
	 * Creates a new DefaultAttributeTableReader object.
	 *
	 * @param source  DOCUMENT ME!
	 * @param objectType  DOCUMENT ME!
	 * @param delimiters  DOCUMENT ME!
	 * @param key  DOCUMENT ME!
	 * @param columnNames  DOCUMENT ME!
	 *
	 * @throws Exception  DOCUMENT ME!
	 */
	public DefaultAttributeTableReader(final URL source, final ObjectType objectType,
	                                   final List<String> delimiters, final int key,
	                                   final String[] columnNames) throws Exception {
		this(source, objectType, delimiters, null, DEF_KEY_COLUMN, null, null, columnNames, null,
		     null, 0);
	}

	/**
	 * Constructor with full options.<br>
	 *
	 * @param source
	 *            Source file URL (can be remote or local)
	 * @param objectType
	 * @param delimiter
	 * @param listDelimiter
	 * @param key
	 * @param aliases
	 * @param columnNames
	 * @param toBeImported
	 * @throws Exception
	 */
	public DefaultAttributeTableReader(final URL source, final ObjectType objectType,
	                                   final List<String> delimiters, final String listDelimiter,
	                                   final int keyIndex, final String mappingAttribute,
	                                   final List<Integer> aliasIndexList,
	                                   final String[] attributeNames, final Byte[] attributeTypes,
	                                   final boolean[] importFlag, final int startLineNumber)
	    throws Exception {
		this.source = source;
		this.startLineNumber = startLineNumber;
		this.mapping = new AttributeMappingParameters(objectType, delimiters, listDelimiter,
		                                              keyIndex, mappingAttribute, aliasIndexList,
		                                              attributeNames, attributeTypes, null,
		                                              importFlag);
		this.parser = new AttributeLineParser(mapping);
	}

	/**
	 * Creates a new DefaultAttributeTableReader object.
	 *
	 * @param source  DOCUMENT ME!
	 * @param mapping  DOCUMENT ME!
	 * @param startLineNumber  DOCUMENT ME!
	 * @param commentChar  DOCUMENT ME!
	 */
	public DefaultAttributeTableReader(final URL source, AttributeMappingParameters mapping,
	                                   final int startLineNumber, final String commentChar) {
		this.source = source;
		this.mapping = mapping;
		this.startLineNumber = startLineNumber;
		this.parser = new AttributeLineParser(mapping);
		this.commentChar = commentChar;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public List getColumnNames() {
		List<String> colNamesList = new ArrayList<String>();

		for (String name : mapping.getAttributeNames()) {
			colNamesList.add(name);
		}

		return colNamesList;
	}

	/**
	 * Read table from the data source.
	 */
	public void readTable() throws IOException {
		final InputStream is = URLUtil.getInputStream(source);
		final BufferedReader bufRd = new BufferedReader(new InputStreamReader(is));
		String line;
		int lineCount = 0;

		/*
		 * Read & extract one line at a time. The line can be Tab delimited,
		 */
		String[] parts = null;
		while ((line = bufRd.readLine()) != null) {
			/*
			 * Ignore Empty & Commnet lines.
			 */
			if ((commentChar != null) && line.startsWith(commentChar)) {
				// Do nothing
			} else if ((lineCount >= startLineNumber) && (line.trim().length() > 0)) {
				parts = line.split(mapping.getDelimiterRegEx());
				parser.parseEntry(parts);
				globalCounter++;
			}

			lineCount++;
		}

		is.close();
		bufRd.close();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getReport() {
		final StringBuffer sb = new StringBuffer();
		sb.append(globalCounter + " entries are loaded and mapped onto\n");
		sb.append(mapping.getObjectType().toString() + " attributes");

		return sb.toString();
	}
}
