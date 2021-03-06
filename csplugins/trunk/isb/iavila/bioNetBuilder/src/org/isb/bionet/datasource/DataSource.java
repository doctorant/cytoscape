
package org.isb.bionet.datasource;

import java.util.*;

/**
 * A source of biological information
 * 
 * @author <a href="mailto:iavila@systemsbiology.org">Iliana Avila-Campillo</a>
 */

public interface DataSource {


  // The types of back-end implementation for the data source
  
  /**
   * examples: SOAP, XML-RPC, CGI, etc.
   */
  public static final String WEB_SERVICE = "web service";
  
  /**
   * example: a local mySQL DB
   */
  public static final String LOCAL_DB = "local DB";
  
  /**
   * example: a remote mySQL DB
   */
  public static final String REMOTE_DB = "remote DB";
  
  /**
   * The data is loaded into volatile memory while the 
   * program is running (only recommended for small data sets)
   */
  public static final String MEMORY = "memory";
  
  public static final String MIXED = "mixed";

  public static final Vector EMPTY_VECTOR = new Vector();
  
  // ------------------------------- methods ----------------------------------//

  /**
   * @return the name of the data source, for example, "KEGG", "Prolinks", etc.
   */
  public String getDataSourceName ();
  
  /**
   * @return the type of backend implementation (how requests to the data source
   * are implemented) one of WEB_SERVICE, LOCAL_DB, REMOTE_DB, MEMORY, MIXED
   */
  public String getBackendType ();

  /**
   * @return a Vector of Strings representing the species for which the data
   * source contains information
   */
  public Vector getSupportedSpecies ();
  
  /**
   * 
   * @param species
   * @return TRUE if the given species is supported by this data source, FALSE otherwise
   */
  public Boolean supportsSpecies (String species);

  /**
   * @return a String denoting the version of the data source (could be a release date,
   * a version number, etc).
   */

  public String getVersion ();

  /**
   * @return Boolean whether or not this data source requires a password from the user
   * in order to access it
   */
  public Boolean requiresPassword ();
  
  /**
   * Returns Boolean.TRUE if the user name and the password are correct, Boolean.FALSE otherwise
   * @param user_name the user name
   * @param password the password
   * @return Boolean.TRUE if successfully authenticated, Boolean.FALSE otherwise
   */
  public Boolean authenticate (String user_name, String password);

  /**
   * Runs tests on the data source
   * @return a vector of results
   */
  public Vector test ();
  
}//DataSource
