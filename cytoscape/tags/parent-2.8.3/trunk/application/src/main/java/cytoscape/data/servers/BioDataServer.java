/*
 File: BioDataServer.java

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

// BioDataServer
// -----------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
// -----------------------------------------------------------------------------------------
package cytoscape.data.servers;

import cytoscape.CytoscapeInit;
import cytoscape.logger.CyLogger;

import cytoscape.cruft.obo.BiologicalProcessAnnotationReader;
import cytoscape.cruft.obo.CellularComponentAnnotationReader;
import cytoscape.cruft.obo.MolecularFunctionAnnotationReader;
import cytoscape.cruft.obo.OboOntologyReader;
import cytoscape.cruft.obo.OboOntologyReader2;
import cytoscape.cruft.obo.SynonymReader;

import cytoscape.data.annotation.Annotation;
import cytoscape.data.annotation.AnnotationDescription;
import cytoscape.data.annotation.Ontology;
import cytoscape.data.annotation.readers.AnnotationFlatFileReader;
import cytoscape.data.annotation.readers.AnnotationXmlReader;
import cytoscape.data.annotation.readers.OntologyFlatFileReader;

import cytoscape.data.readers.TextFileReader;
import cytoscape.data.readers.TextHttpReader;
import cytoscape.data.readers.TextJarReader;

import cytoscape.data.synonyms.Thesaurus;
import cytoscape.data.synonyms.readers.ThesaurusFlatFileReader;

import cytoscape.util.BioDataServerUtil;
import cytoscape.util.URLUtil;

// -----------------------------------------------------------------------------------------
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.URL;

import java.rmi.Naming;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;


// ----------------------------------------------------------------------------------------
/**
 *
 */
public class BioDataServer {
	private static String GENE_ASSOCIATION_FILE = "gene_association";
	private static String OBO_FILE = "obo";
	protected BioDataServerInterface server;
	private static CyLogger logger = CyLogger.getLogger(BioDataServer.class);

	// Flip the file content (names) or not.
	private boolean flip;

	// This is for taxon name-number conversion over the net
	private static final String TAXON_RESOURCE_FILE = "/cytoscape/resources/tax_report.txt";
	String taxonName;
	String taxonNumber;
	String absPath; // apparently not used for anything
	String taxonFileName; // Filename of taxonomy table
	File taxonFile; // Table for the NCBI Taxonomy number <-> Taxonomy Name
	File start; // Start dir of the Cytoscape
	Thesaurus thesaurus; // for flipping the names
	HashMap attributeMap; // Manage which ontoligies are mapped as
	                      // CyAttributes.
	HashMap ontologyTypeMap;
	BioDataServerUtil bdsu; // Utilities for the Biodataserver
	BufferedReader taxonFileReader;

	// ----------------------------------------------------------------------------------------
	/**
	 * serverName is either an RMI URI, or a manifest file which says what files
	 * to load into an in-process server; the manifest, the annotations, and the
	 * ontologies, may be files on a filesystem, files in a jar, or files
	 * retrieved by HTTP
	 *
	 * Mod. by Kei (9/10/2005): 1. Read new obo file and gene_association file
	 * format. 2. Wrote a taxon number <-> taxon name converter. This function
	 * is based on the list in the file, users need to put the file in the dir.
	 *
	 * <New for Cytoscape 2.3> Bio Data Server manages ontologies mapped onto
	 * CyAttributes This can be managed through GO Mapper GUI. kono(4/12/2006)
	 */
	public BioDataServer(String serverName) throws Exception {
		attributeMap = new HashMap();
		ontologyTypeMap = new HashMap();

		bdsu = new BioDataServerUtil();

		taxonFileReader = null;

		// Flip the names or not. Will be given from the Wizard.
		flip = false;

		thesaurus = new Thesaurus(CytoscapeInit.getProperties().getProperty("defaultSpeciesName"));

		taxonName = null;
		taxonNumber = null;
		start = CytoscapeInit.getMRUD();

		if (serverName.indexOf("rmi://") >= 0)
			server = (BioDataServerInterface) Naming.lookup(serverName);
		else {
			// look for a readable file
			server = new BioDataServerRmi(); // actually runs in process

			File fileTester = new File(serverName);

			if ((serverName.startsWith("jar://")) || (serverName.startsWith("http://"))
			    || (!fileTester.isDirectory() && fileTester.canRead())) {
				boolean fileFlag = false;

				// Read manifest file. First, try to read new format manifest
				// file which includes ".obo" and gene_association file.
				BufferedReader manFileIn = null;

				try {
					manFileIn =  new BufferedReader(new FileReader(serverName));
					// Check file type.
					// If the manifest contains obo or gene_association entry,
					// handle it as a new manifest file format.
					fileFlag = checkFileType(manFileIn);
				}
				finally {
					if (manFileIn != null) {
						manFileIn.close();
					}
				}

				// New File Type found.
				if (fileFlag) {
					// Extract file names and flip state from the manifest file
					String[] flags = parseLoadFile(serverName, "flip");

					if (flags[0].endsWith("true")) {
						flip = true;
						logger.info("Cannonical and common names will be fliped...");
					} else {
						flip = false;
					}

					// Extract obo file name
					String[] oboFile = parseLoadFile(serverName, OBO_FILE);

					// Extract gene association file name
					String[] geneAssociationFile = parseLoadFile(serverName, GENE_ASSOCIATION_FILE);

					try {
						loadObo(geneAssociationFile, oboFile);
					} catch (Exception e) {
						logger.warn("Unable to load OBO file: "+e.getMessage(), e);
						throw e;
					}
				} else {
					/*
					 * Load old-style manifest file (for backword
					 * compatibility).
					 */
					String[] thesaurusFilenames = parseLoadFile(serverName, "synonyms");
					loadThesaurusFiles(thesaurusFilenames);

					String[] ontologyFiles = parseLoadFile(serverName, "ontology");
					String[] annotationFilenames = parseLoadFile(serverName, "annotation");
					loadAnnotationFiles(annotationFilenames, ontologyFiles);

					//					String[] thesaurusFilenames = parseLoadFile(serverName,
					//							"synonyms");
					//					loadThesaurusFiles(thesaurusFilenames);
				}
			} // if a plausible candidate load file
			else {
				logger.warn("Could not read BioDataServer load file '" + serverName + "'");
			}
		} // else: look for a readable file
	} // ctor

	//
	// Determine whether given file is new format or old.
	//
	protected boolean checkFileType(final BufferedReader br) throws IOException {
		String curLine = null;
        boolean rv;

        rv = false;
        try {
    		while (!rv && (null != (curLine = br.readLine()))) {
        		if (curLine.startsWith(OBO_FILE) || curLine.startsWith(GENE_ASSOCIATION_FILE)) {
            		rv = true;
                }
            }
        }
        finally {
            br.close();
        }

		return rv;
	}

	// ----------------------------------------------------------------------------------------

	// Mod by Keiichiro Ono (09/09/2005)
	// 1. support for the new manifest file
	// 2. support for the internet files
	//
	// TODO:
	// get rid of manifest file idea...
	//
	protected String[] parseLoadFile(String filename, String key) // todo (pshannon 2003/07/01): there is some ugly special casing here, as we
	                                                              // figure
	                                                              // todo: out if the manifest file path is jar, http, or a regular file
	                                                              // system
	                                                              // todo: file. this should be refactored.
	 {
		String rawText;

		// when annotation files are loaded from a filesystem, the manifest will
		// probably
		// use names which are relative to the manifest. so (for this case, but
		// not when
		// reading from jar files) get the absolute path to the manifest, and
		// prepend
		// that path to the names found in the manifest. somewhat clunkily,
		// track that
		// information with these next two variables
		File absoluteDirectory = null;
		String httpUrlPrefix = null;
		boolean readingFromFileSystem = false;
		boolean readingFromWeb = false;

		try {
			if (filename.trim().startsWith("jar://")) {
				TextJarReader reader = new TextJarReader(filename);
				reader.read();
				rawText = reader.getText();
			} // if
			else if (filename.trim().startsWith("http://")) {
				TextHttpReader reader = new TextHttpReader(filename);
				reader.read();
				rawText = reader.getText();
				readingFromWeb = true;

				try {
					URL url = new URL(filename);
					String fullUrlString = url.toString();
					httpUrlPrefix = fullUrlString.substring(0, fullUrlString.lastIndexOf("/"));
				} catch (Exception e) {
					httpUrlPrefix = "url parsing error!";
				}
			} // else: http
			else {
				File file = new File(filename);
				readingFromFileSystem = true;
				absoluteDirectory = file.getAbsoluteFile().getParentFile();

				TextFileReader reader = new TextFileReader(filename);
				reader.read();
				rawText = reader.getText();
			} // else: regular filesystem file
		} catch (Exception e0) {
			logger.warn("-- Exception while reading annotation server load file " + filename, e0);

			return new String[0];
		}

		String[] lines = rawText.split("\n");

		List<String> list = new ArrayList<String>();

		for (int i = 0; i < lines.length; i++) {
			String line = lines[i].trim();

			if (line.trim().startsWith("#"))
				continue;

			if (line.startsWith(key)) {
				String fileToRead = line.substring(line.indexOf("=") + 1);

				if (readingFromFileSystem)
					fileToRead = (new File(absoluteDirectory, fileToRead)).getPath();
				else if (readingFromWeb)
					fileToRead = httpUrlPrefix + "/" + fileToRead;

				list.add(fileToRead);
			} // if
		} // for i

		if (list.size() == 0) {
			return null;
		} else

			return (String[]) list.toArray(new String[0]);
	} // parseLoadFile

	// ----------------------------------------------------------------------------------------
	/**
	 * Creates a new BioDataServer object.
	 *
	 * @throws Exception  DOCUMENT ME!
	 */
	public BioDataServer() throws Exception {
		server = new BioDataServerRmi();
	} // ctor

	// ----------------------------------------------------------------------------------------
	/**
	 *  DOCUMENT ME!
	 *
	 * @param ontologyFilenames DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public Ontology[] readOntologyFlatFiles(String[] ontologyFilenames)
	    throws Exception // a quick hack. this is called only if annotation & ontology are each flat
	                     // files,
	                     // which means they must be read separately. and xml annotation file names
	                     // its own
	                     // ontology file, and the annotationXmlReader is responsible for loading its
	                     // ontology.
	 {
		Vector list = new Vector();

		for (int i = 0; i < ontologyFilenames.length; i++) {
			String filename = ontologyFilenames[i];

			if (!filename.endsWith(".xml")) {
				OntologyFlatFileReader reader = new OntologyFlatFileReader(filename);
				list.add(reader.getOntology());
			}
		} // for i

		return (Ontology[]) list.toArray(new Ontology[0]);
	} // loadOntologyFiles

	// Read new obo file format.
	/**
	 *  DOCUMENT ME!
	 *
	 * @param ontologyFilenames DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public Ontology[] readOntologyFlatFiles2(String[] ontologyFilenames) throws Exception {
		// logger.info("Reading Ontology flat file...");
		Vector list = new Vector();

		for (int i = 0; i < ontologyFilenames.length; i++) {
			String filename = ontologyFilenames[i];

			// Reader for the ".obo" file
			BufferedReader oboReader = null;
			BufferedReader oboReader2 = null;

			try {
				oboReader = new BufferedReader(new OboOntologyReader(new FileReader(filename)));
				OntologyFlatFileReader reader = new OntologyFlatFileReader(oboReader);
				list.add(reader.getOntology());
			}
			finally {
				if (oboReader != null) {
					oboReader.close();
				}
			}

			try {
				oboReader2 = new BufferedReader(new OboOntologyReader2(new FileReader(filename)));
				String line;
				String[] parts = null;

				while ((line = oboReader2.readLine()) != null) {
					parts = line.split("=");

					if (parts.length == 2) {
						//logger.info("ID = " + parts[0] + ", " + parts[1]);
						if (parts[1].equals("biological_process")) {
							ontologyTypeMap.put(parts[0], "P");
						} else if (parts[1].equals("molecular_function")) {
							ontologyTypeMap.put(parts[0], "F");
						} else if (parts[1].equals("cellular_component")) {
							ontologyTypeMap.put(parts[0], "C");
						}
					}
				}
			}
			finally {
				if (oboReader2 != null) {
					oboReader2.close();
				}
			}
		}

		return (Ontology[]) list.toArray(new Ontology[0]);
	} // loadOntologyFiles

	// ----------------------------------------------------------------------------------------
	protected Ontology pickOntology(Ontology[] ontologies, Annotation annotation) {
		for (int i = 0; i < ontologies.length; i++)
			if (ontologies[i].getCurator().equalsIgnoreCase(annotation.getCurator()))
				return ontologies[i];

		return null;
	} // pickOntology

	// ----------------------------------------------------------------------------------------
	/**
	 *  DOCUMENT ME!
	 *
	 * @param annotationFilenames DOCUMENT ME!
	 * @param ontologyFilenames DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void loadObo(String[] annotationFilenames, String[] ontologyFilenames)
	    throws Exception {
		// logger.info("Loading OBO file...");
//The following commented out code appears to replicate what's done in readOntologyFlatFiles2
//		BufferedReader[] oboReaders = new BufferedReader[ontologyFilenames.length];
//
//		for (int i = 0; i < ontologyFilenames.length; i++) {
//			oboReaders[i] = new BufferedReader(new OboOntologyReader(new FileReader(ontologyFilenames[i])));
//		}

		Ontology[] ontologies = readOntologyFlatFiles2(ontologyFilenames);

		URL taxURL = getClass().getResource(TAXON_RESOURCE_FILE);
		/*
		 * Since one Gene Association file is equal to the follwing:
		 *
		 * Biological Process Annotation Cellar Component Annotation Molecular
		 * Function Annotation
		 *
		 * we need 3 separate readers.
		 */
		for (int i = 0; i < annotationFilenames.length; i++) {
			Annotation bpAnnotation = null;
			Annotation ccAnnotation = null;
			Annotation mfAnnotation = null;
			String filename = annotationFilenames[i];

			String[] thFileName = new String[1];
			thFileName[0] = annotationFilenames[i];

			// Extract taxon name.
			BufferedReader gaFileReader = null;
			try {

				gaFileReader = new BufferedReader(new FileReader(filename));

				// Create tax_report file reader
				// taxonFileReader = new BufferedReader(new InputStreamReader(taxURL.openStream()));
				// Even though taxURL is probably a local URL, error on the
				// side of caution and use URLUtil to get the input stream (which
				// handles proxy servers and cached pages):
				taxonFileReader = new BufferedReader(new InputStreamReader(URLUtil.getBasicInputStream(taxURL)));

				// taxonName = checkSpecies(gaFileReader);
				taxonName = bdsu.checkSpecies(gaFileReader, taxonFileReader);
			}
			finally {
				if (gaFileReader != null) {
					gaFileReader.close();
				}
			}

			loadThesaurusFiles2(thFileName);

			logger.info("Loading: " + annotationFilenames[i] + " (Species = " + taxonName + ")");

			// Reader for the "gene_association" file
			BufferedReader bpRd = null;
			try {
				bpRd = new BufferedReader(
						   new BiologicalProcessAnnotationReader(taxonName,
																 ontologyTypeMap,
																 new FileReader(filename)));
				AnnotationFlatFileReader bpReader = new AnnotationFlatFileReader(bpRd, thesaurus, flip);
				bpAnnotation = bpReader.getAnnotation();
			}
			finally {
				if (bpRd != null) {
					bpRd.close();
				}
			}

			BufferedReader ccRd = null;
			try {
				ccRd = new BufferedReader(
						   new CellularComponentAnnotationReader(taxonName,
																 ontologyTypeMap,
																 new FileReader(filename)));
				AnnotationFlatFileReader ccReader = new AnnotationFlatFileReader(ccRd, thesaurus, flip);
				ccAnnotation = ccReader.getAnnotation();
			}
			finally {
				if (ccRd != null) {
					ccRd.close();
				}
			}

			BufferedReader mfRd = null;

			try {
				mfRd = new BufferedReader(
						   new MolecularFunctionAnnotationReader(taxonName,
																 ontologyTypeMap,
																 new FileReader(filename)));
				AnnotationFlatFileReader mfReader = new AnnotationFlatFileReader(mfRd, thesaurus, flip);
				mfAnnotation = mfReader.getAnnotation();
			}
			finally {
				if (mfRd != null) {
					mfRd.close();
				}
			}

			bpAnnotation.setOntology(pickOntology(ontologies, bpAnnotation));
			ccAnnotation.setOntology(pickOntology(ontologies, ccAnnotation));
			mfAnnotation.setOntology(pickOntology(ontologies, mfAnnotation));

			server.addAnnotation(bpAnnotation);
			server.addAnnotation(ccAnnotation);
			server.addAnnotation(mfAnnotation);
		}
	} // loadAnnotationFiles2

	// Read the content of the annotation and ontology file
	/**
	 *  DOCUMENT ME!
	 *
	 * @param annotationFilenames DOCUMENT ME!
	 * @param ontologyFilenames DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void loadAnnotationFiles(String[] annotationFilenames, String[] ontologyFilenames)
	    throws Exception {
		Ontology[] ontologies = readOntologyFlatFiles(ontologyFilenames);

		for (int i = 0; i < annotationFilenames.length; i++) {
			Annotation annotation;
			String filename = annotationFilenames[i];

			if (!filename.endsWith(".xml")) {
				AnnotationFlatFileReader reader = new AnnotationFlatFileReader(filename, thesaurus);
				annotation = reader.getAnnotation();
				annotation.setOntology(pickOntology(ontologies, annotation));
			} else {
				File xmlFile = new File(annotationFilenames[i]);
				AnnotationXmlReader reader = new AnnotationXmlReader(xmlFile);
				annotation = reader.getAnnotation();
			}

			server.addAnnotation(annotation);
		} // for i
	} // loadAnnotationFiles

	// ----------------------------------------------------------------------------------------
	/**
	 *  DOCUMENT ME!
	 *
	 * @param thesaurusFilenames DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void loadThesaurusFiles(String[] thesaurusFilenames) throws Exception {
		for (int i = 0; i < thesaurusFilenames.length; i++) {
			String filename = thesaurusFilenames[i];

			ThesaurusFlatFileReader reader = new ThesaurusFlatFileReader(filename);
			thesaurus = reader.getThesaurus();

			//thesaurus.dump();
			server.addThesaurus(thesaurus.getSpecies(), thesaurus);
		}
	} // loadThesaurusFiles

	/*
	 * Accept new gene association file. And create Thesaurus file from it.
	 */

	/**
	 *  DOCUMENT ME!
	 *
	 * @param thesaurusFilenames DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void loadThesaurusFiles2(String[] thesaurusFilenames) throws Exception {
		for (int i = 0; i < thesaurusFilenames.length; i++) {
			String filename = thesaurusFilenames[i];

			BufferedReader thRd = null;

			try {
				thRd = new BufferedReader(new SynonymReader(taxonName, new FileReader(filename)));
				ThesaurusFlatFileReader reader = new ThesaurusFlatFileReader(thRd, flip);
				thesaurus = reader.getThesaurus();

				//thesaurus.dump();
				server.addThesaurus(thesaurus.getSpecies(), thesaurus);
			}
			finally {
				if (thRd != null) {
					thRd.close();
				}
			}
		}
	} // loadThesaurusFiles

	// ----------------------------------------------------------------------------------------
	/**
	 *  DOCUMENT ME!
	 */
	public void clear() {
		try {
			server.clear();
		} catch (Exception e) {
			logger.warn("Error!  failed to clear", e);
		}
	}

	// ----------------------------------------------------------------------------------------
	/**
	 *  DOCUMENT ME!
	 *
	 * @param annotation DOCUMENT ME!
	 */
	public void addAnnotation(Annotation annotation) {
		try {
			server.addAnnotation(annotation);
		} catch (Exception e) {
			logger.warn("Error!  failed to add annotation " + annotation, e);
		}
	}

	// ----------------------------------------------------------------------------------------
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getAnnotationCount() {
		try {
			int count = server.getAnnotationCount();

			return count;
		} catch (Exception e) {
			return 0;
		}
	}

	// ----------------------------------------------------------------------------------------
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public AnnotationDescription[] getAnnotationDescriptions() {
		try {
			return server.getAnnotationDescriptions();
		} catch (Exception e) {
			return null;
		}
	}

	// ----------------------------------------------------------------------------------------
	/**
	 *  DOCUMENT ME!
	 *
	 * @param species DOCUMENT ME!
	 * @param curator DOCUMENT ME!
	 * @param type DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Annotation getAnnotation(String species, String curator, String type) {
		try {
			return server.getAnnotation(species, curator, type);
		} catch (Exception e) {
			return null;
		}
	}

	// ----------------------------------------------------------------------------------------
	/**
	 *  DOCUMENT ME!
	 *
	 * @param description DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Annotation getAnnotation(AnnotationDescription description) {
		try {
			return server.getAnnotation(description);
		} catch (Exception e) {
			return null;
		}
	}

	// ----------------------------------------------------------------------------------------
	/**
	 *  DOCUMENT ME!
	 *
	 * @param species DOCUMENT ME!
	 * @param curator DOCUMENT ME!
	 * @param type DOCUMENT ME!
	 * @param entity DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int[] getClassifications(String species, String curator, String type, String entity) {
		try {
			return server.getClassifications(species, curator, type, entity);
		} catch (Exception e) {
			return null;
		}
	}

	// ----------------------------------------------------------------------------------------
	/**
	 *  DOCUMENT ME!
	 *
	 * @param description DOCUMENT ME!
	 * @param entity DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int[] getClassifications(AnnotationDescription description, String entity) {
		try {
			return server.getClassifications(description, entity);
		} catch (Exception e) {
			return null;
		}
	}

	// ----------------------------------------------------------------------------------------
	/**
	 *  DOCUMENT ME!
	 *
	 * @param description DOCUMENT ME!
	 * @param entity DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String[][] getAllAnnotations(AnnotationDescription description, String entity) {
		try {
			return server.getAllAnnotations(description, entity);
		} catch (Exception e) {
			return null;
		}
	}

	// ----------------------------------------------------------------------------------------
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String describe() {
		try {
			return server.describe();
		} catch (Exception e) {
			return "error connecting to data server";
		}
	}

	// ----------------------------------------------------------------------------------------
	/**
	 *  DOCUMENT ME!
	 *
	 * @param species DOCUMENT ME!
	 * @param thesaurus DOCUMENT ME!
	 */
	public void addThesaurus(String species, Thesaurus thesaurus) {
		try {
			server.addThesaurus(species, thesaurus);
		} catch (Exception e) {
			return;
		}
	}

	// ----------------------------------------------------------------------------------------
	/**
	 *  DOCUMENT ME!
	 *
	 * @param species DOCUMENT ME!
	 * @param commonName DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getCanonicalName(String species, String commonName) {
		try {
			return server.getCanonicalName(species, commonName);
		} catch (Exception e) {
			return null;
		}
	}

	// ----------------------------------------------------------------------------------------
	/**
	 *  DOCUMENT ME!
	 *
	 * @param species DOCUMENT ME!
	 * @param commonName DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String[] getAllCommonNames(String species, String commonName) {
		try {
			return server.getAllCommonNames(species, commonName);
		} catch (Exception e) {
			return null;
		}
	}

	// ----------------------------------------------------------------------------------------
	/**
	 *  DOCUMENT ME!
	 *
	 * @param species DOCUMENT ME!
	 * @param canonicalName DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getCommonName(String species, String canonicalName) {
		try {
			return server.getCommonName(species, canonicalName);
		} catch (Exception e) {
			return null;
		}
	}
} // BioDataServer
