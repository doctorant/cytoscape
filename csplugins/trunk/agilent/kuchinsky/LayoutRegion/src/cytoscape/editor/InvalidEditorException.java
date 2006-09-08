/*
 * Created on Jul 5, 2005
 *
 */
package cytoscape.editor;

/**
 * 
 * Create a new exception when there is no registered editor corresponding
 * to the editor type suppied to the CytoscapeEditorFactory.getEditor() method.
 * 
 * @author Allan Kuchinsky, Agilent Technologies
 * @version 1.0
 *
 */
public class InvalidEditorException extends Exception {
	
	/**
	 * Create a new exception when there is no registered editor corresponding
	 * to the editor type suppied to the CytoscapeEditorFactory.getEditor() method.
	 * @param msg	message, may be null
	 * @param cause	cause, may be null
	 * @see CytoscapeEditorFactory
	 */
	public InvalidEditorException(String msg, Throwable cause) {
		super(msg,cause);
	}
	

}
