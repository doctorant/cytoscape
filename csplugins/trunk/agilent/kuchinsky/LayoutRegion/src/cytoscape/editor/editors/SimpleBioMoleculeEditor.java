/*
 * Created on Jul 16, 2005
 *
 */
package cytoscape.editor.editors;

import java.awt.Color;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;

import cytoscape.Cytoscape;
import cytoscape.editor.CytoscapeEditorManager;
import cytoscape.editor.event.PaletteNetworkEditEventHandler;
import cytoscape.editor.impl.CytoShapeIcon;
import cytoscape.editor.impl.ShapePalette;
import cytoscape.visual.Arrow;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.calculators.GenericEdgeArrowCalculator;
import cytoscape.visual.calculators.GenericNodeColorCalculator;
import cytoscape.visual.calculators.GenericNodeShapeCalculator;
import cytoscape.visual.mappings.DiscreteMapping;

/**
 * An example editor that extends the basic Cytoscape editor and is based upon a
 * drag-and-drop and palette framework into which developers plug in semantics.
 * The framework consists of
 * <ul>
 * <li> a palette, from which the user drags and drops shapes onto the canvas
 * <li> an extensible shape class for the palette,
 * <li> a drawing canvas upon which shapes are dropped, and
 * <li> event handlers which respond to drop events generated by the canvas.
 * </ul>
 * <p>
 * The dropping of shapes onto the canvas results in the addition of nodes and
 * edges to the current Cytoscape network, as defined by the behavior of the
 * event handler that responds to the drop events. In the simple "BioMolecule"
 * editor, there are node types for proteins, catalysis, small molecules, and
 * biochemical reactions, as well as a directed edge types for activation, 
 * inhibition, and catalysis.
 * <p>
 * 
 * 
 * @author Allan Kuchinsky
 * @version 1.0
 * @see PaletteNetworkEditEventHandler
 * 
 */
public class SimpleBioMoleculeEditor extends BasicCytoscapeEditor {

	private ShapePalette shapePalette;

	public static final String NODE_TYPE = "NODE_TYPE";

	public static final String EDGE_TYPE = "EDGE_TYPE";

	public static final String ACTIVATION = "Activation";

	public static final String INHIBITION = "Inhibition";

	public static final String CATALYSIS = "Catalysis";

	public static final String BIOMOLECULE_VISUAL_STYLE = "SimpleBioMoleculeEditor";

	/**
	 * class used to construct visual style used by the SimpleBioMoleculeEditor
	 */
	public static MapBioMoleculeEditorToVisualStyle mpbv = null;

	/**
	 * flag used to determine when to construct a visual style vs. use an existing one
	 */
	private static boolean regeneratedVizStyle = false;

	public SimpleBioMoleculeEditor() {
		super();

	}
	
	/**
	 * 
	 */
	public void buildVisualStyle()
	{
		// do visual style creation at time that editor is created, to
		// accommodate the current visual style potentially being clobbered by other plugins

		VisualMappingManager manager = Cytoscape.getVisualMappingManager();

		CalculatorCatalog catalog = manager.getCalculatorCatalog();

		VisualStyle vizStyle = catalog.getVisualStyle(BIOMOLECULE_VISUAL_STYLE);
//		System.out.println ("Got visual Style from catalog: " + catalog 
//				+ " = " + vizStyle);
		if (mpbv == null) {
			mpbv = new MapBioMoleculeEditorToVisualStyle();
		}
		if (vizStyle == null) {
			vizStyle = mpbv.createVizMapper();
		}

		else {

//			System.out.println("Calling defineVisualStyle for: " + vizStyle);
			mpbv.defineVisualStyle(vizStyle, manager, catalog);
		}		
	}

	/**
	 * specialized initialization code for editor, called by
	 * CytoscapeEditorManager when a new editor is built.
	 * gets the mappings from the visual style and uses them to construct
	 * shapes for the palette
	 * 
	 * @param args
	 *            an arbitrary list of arguments passed to initialization
	 *            routine. Not used in this editor
	 */
	public void initializeControls(List args) {

		// AJK: 09/29/06 BEGIN
		// do visual style creation at time that editor is created, to
		// accommodated the current
		// visual style potentially being clobbered by other plugins

		VisualMappingManager manager = Cytoscape.getVisualMappingManager();

		CalculatorCatalog catalog = manager.getCalculatorCatalog();

		// VisualStyle vizStyle = manager.getVisualStyle();
		VisualStyle vizStyle = catalog.getVisualStyle(BIOMOLECULE_VISUAL_STYLE);
//		System.out.println ("Got visual Style from catalog: " + catalog 
//				+ " = " + vizStyle);
		if (vizStyle == null) {
			if (mpbv == null) {
				mpbv = new MapBioMoleculeEditorToVisualStyle();
			}
			vizStyle = mpbv.createVizMapper();
		}

		else {

			System.out.println("Calling defineVisualStyle for: " + vizStyle);
			mpbv.defineVisualStyle(vizStyle, manager, catalog);
		}

		// AJK: 09/29/06 END

		// AJK: 06/10/06 BEGIN
		// no longer rebuilding shape palette, just its shape pane
		// shapePalette = new ShapePalette();
		shapePalette = CytoscapeEditorManager.getCurrentShapePalette();
		shapePalette.clear();
		// AJK: 06/10/06 END

		String controllingEdgeAttribute = this.getControllingEdgeAttribute();

		if (vizStyle == null) {
			String expDescript = "Cannot find SimpleBioMoleculeEditor Visual Style.";
			String title = "Cannot build palette for SimpleBioMoleculeEditor";
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), expDescript,
					title, JOptionPane.PLAIN_MESSAGE);
			return;
		} else {
			if (!regeneratedVizStyle) {
				regeneratedVizStyle = true;
				mpbv.defineVisualStyle(vizStyle, manager, catalog);
			}
		}

		NodeAppearanceCalculator nac = vizStyle.getNodeAppearanceCalculator();

//		System.out.println("NodeAppearanceCalculator for visual style: "
//				+ vizStyle + " is " + nac);
		if (nac == null) {
			String expDescript = "Cannot build palette.  You need to set up a Visual Style that maps Node Color to NODE_TYPE attribute.";
			String title = "Cannot build palette for SimpleBioMoleculeEditor";
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), expDescript,
					title, JOptionPane.PLAIN_MESSAGE);
			return;
		}
		GenericNodeColorCalculator nfill = (GenericNodeColorCalculator) nac
				.getNodeFillColorCalculator();
		System.out.println("NodeColorCalculator for visual style: " + vizStyle
				+ " is " + nfill);
		if (nfill == null) {
			String expDescript = "Cannot build palette.  You need to set up a Visual Style that maps Node Color to NODE_TYPE attribute.";
			String title = "Cannot build palette for SimpleBioMoleculeEditor";
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), expDescript,
					title, JOptionPane.PLAIN_MESSAGE);
			return;
		}
		Vector mappings = nfill.getMappings();
		DiscreteMapping dfill = null;
		for (int i = 0; i < mappings.size(); i++) {
			DiscreteMapping dfillCandidate = (DiscreteMapping) mappings.get(i);
			String attr = dfillCandidate.getControllingAttributeName();
			if (attr.equals(NODE_TYPE)) {
				dfill = dfillCandidate;
				break;
			}
		}
		System.out.println("DiscreteMapping for visual style: " + vizStyle
				+ " is " + dfill);
		if (dfill == null) {
			String expDescript = "Cannot build palette.  You need to set up a Visual Style that maps Node Shape to NODE_TYPE attribute.";
			String title = "Cannot build palette for SimpleBioMoleculeEditor";
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), expDescript,
					title, JOptionPane.PLAIN_MESSAGE);
			return;
		}

		GenericNodeShapeCalculator nshape = (GenericNodeShapeCalculator) nac
				.getNodeShapeCalculator();
		if (nshape == null) {
			String expDescript = "Cannot build palette.  You need to set up a Visual Style that maps Node Color to NODE_TYPE attribute.";
			String title = "Cannot build palette for SimpleBioMoleculeEditor";
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), expDescript,
					title, JOptionPane.PLAIN_MESSAGE);
			return;
		}
		mappings = nshape.getMappings();
		DiscreteMapping dshape = null;
		for (int i = 0; i < mappings.size(); i++) {
			DiscreteMapping dshapeCandidate = (DiscreteMapping) mappings.get(i);
			String attr = dshapeCandidate.getControllingAttributeName();
			if (attr.equals(NODE_TYPE)) {
				dshape = dshapeCandidate;
				break;
			}
		}
		if (dshape == null) {
			String expDescript = "Cannot build palette.  You need to set up a Visual Style that maps Node Shape to NODE_TYPE attribute.";
			String title = "Cannot build palette for SimpleBioMoleculeEditor";
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), expDescript,
					title, JOptionPane.PLAIN_MESSAGE);
			return;
		}

		Color nodeColor;
		byte nodeShape;

		EdgeAppearanceCalculator eac = vizStyle.getEdgeAppearanceCalculator();
		System.out.println("Got edgeAppearanceCalculator: " + eac);
		if (eac == null) {
			String expDescript = "Cannot build palette.  You need to set up a Visual Style that maps Edge Target Arrow to an attribute.";
			String title = "Cannot build palette for SimpleBioMoleculeEditor: no edge appearance calculator";
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), expDescript,
					title, JOptionPane.PLAIN_MESSAGE);
			return;
		}
		GenericEdgeArrowCalculator edgeCalc = (GenericEdgeArrowCalculator) eac
				.getEdgeTargetArrowCalculator();
		System.out.println("Got edge target arrow calculator: " + edgeCalc);
		if (edgeCalc == null) {
			String expDescript = "Cannot build palette.  You need to set up a Visual Style that maps Edge Target Arrow to an attribute.";
			String title = "Cannot build palette for SimpleBioMoleculeEditor: no edge arrow calculator";
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), expDescript,
					title, JOptionPane.PLAIN_MESSAGE);
			return;
		}
		Vector edgeMappings = edgeCalc.getMappings();

		DiscreteMapping dArrow = null;
		for (int i = 0; i < edgeMappings.size(); i++) {
			DiscreteMapping dArrowCandidate = (DiscreteMapping) edgeMappings
					.get(i);
			String attr = dArrowCandidate.getControllingAttributeName();
//			System.out.println("checking attribute: " + attr
//					+ " against controlling attribute: "
//					+ controllingEdgeAttribute);
			if (attr.equals(controllingEdgeAttribute)) {
				dArrow = dArrowCandidate;
				System.out.println("Got edge mapping: " + dArrow);
				break;
			}
		}
		if (dArrow == null) {
			String expDescript = "Cannot build palette.  You need to set up a Visual Style that maps Edge Target Arrow to an attribute.";
			String title = "Cannot build palette for SimpleBioMoleculeEditor";
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), expDescript,
					title, JOptionPane.PLAIN_MESSAGE);
			return;
		}
//		System.out.println("adding edge arrows to palette");
		Arrow edgeTargetArrow;
		String[] EdgeTypes = new String[] { ACTIVATION, INHIBITION, CATALYSIS };
		for (int i = 0; i < EdgeTypes.length; i++) {
//			System.out.println("getting map value for edge type: "
//					+ EdgeTypes[i]);
			if (dArrow.getMapValue(EdgeTypes[i]) != null) {
				edgeTargetArrow = (Arrow) dArrow.getMapValue(EdgeTypes[i]);
			} else {
				edgeTargetArrow = eac.getDefaultEdgeTargetArrow();
			}
//			System.out.println("Addng shape for EdgeType " + EdgeTypes[i]
//					+ " = " + edgeTargetArrow);

			shapePalette.addShape(EDGE_TYPE, EdgeTypes[i], new CytoShapeIcon(
					edgeTargetArrow), EdgeTypes[i]);
		}

		nodeShape = ((Byte) dshape.getMapValue("protein")).byteValue();

		nodeColor = (Color) dfill.getMapValue("protein");
		shapePalette.addShape("NODE_TYPE", "protein",
				new CytoShapeIcon(nodeShape, nodeColor), "Protein");

		nodeShape = ((Byte) dshape.getMapValue("smallMolecule")).byteValue();
		nodeColor = (Color) dfill.getMapValue("smallMolecule");
		shapePalette.addShape("NODE_TYPE", "smallMolecule",
				new CytoShapeIcon(nodeShape, nodeColor), "Small Molecule");

		nodeShape = ((Byte) dshape.getMapValue("biochemicalReaction"))
				.byteValue();
		nodeColor = (Color) dfill.getMapValue("biochemicalReaction");
		shapePalette
				.addShape("NODE_TYPE", "biochemicalReaction",
						new CytoShapeIcon(nodeShape, nodeColor),
						"Biochemical Reaction");

		if (CytoscapeEditorManager.isEditingEnabled())
		{
			shapePalette.showPalette();
		}
		
		super.initializeControls(null);

	}

	/**
	 * sets controls invisible when editor type is switched
	 * 
	 * @param args
	 *            args an arbitrary list of arguments (not used in this editor)
	 */
	public void disableControls(List args) {
		// super.disableControls(args);
		if (shapePalette != null) {
			shapePalette.setVisible(false);
		}
	}

	/**
	 * sets controls visible when editor type is switched back to this editor
	 * 
	 * @param args
	 *            args an arbitrary list of arguments (not used in this editor) *
	 */
	public void enableControls(List args) {
		// super.enableControls(args);
		shapePalette.showPalette();
		shapePalette.setVisible(true);

	}

}