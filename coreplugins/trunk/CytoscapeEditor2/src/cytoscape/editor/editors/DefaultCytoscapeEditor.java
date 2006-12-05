/*
 * Created on Oct 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package cytoscape.editor.editors;

import cytoscape.Cytoscape;

import cytoscape.editor.CytoscapeEditorFactory;
import cytoscape.editor.CytoscapeEditorManager;
import cytoscape.editor.ShapePaletteInfo;
import cytoscape.editor.ShapePaletteInfoGenerator;

import cytoscape.editor.event.PaletteNetworkEditEventHandler;

import cytoscape.editor.impl.CytoShapeIcon;
import cytoscape.editor.impl.ShapePalette;

import cytoscape.visual.Arrow;
import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.NodeAppearanceCalculator;

import cytoscape.visual.ui.VizMapUI;

import java.awt.Color;
import java.awt.Dimension;

import java.util.Iterator;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * /** An example editor that extends the basic Cytoscape editor and is based
 * upon a drag-and-drop and palette framework into which developers plug in
 * semantics. The framework consists of
 * <ul>
 * <li> a palette, from which the user drags and drops shapes onto the canvas
 * <li> an extensible shape class for the palette,
 * <li> a drawing canvas upon which shapes are dropped, and
 * <li> event handlers which respond to drop events generated by the canvas.
 * </ul>
 * <p>
 * The dropping of shapes onto the canvas results in the addition of nodes and
 * edges to the current Cytoscape network, as defined by the behavior of the
 * event handler that responds to the drop events.
 * <p>
 *
 *
 * @author Allan Kuchinsky
 * @version 1.0
 * @see PaletteNetworkEditEventHandler
 *
 */
public class DefaultCytoscapeEditor extends BasicCytoscapeEditor
    implements ChangeListener {
    // MLC 12/02/06:
    //    public static final String NODE_TYPE = "NODE_TYPE";
    // MLC 12/02/06:
    //    public static final String EDGE_TYPE = "EDGE_TYPE";

    /**
     * main data structures for all node and edge attributes
     */
    public static cytoscape.data.CyAttributes nodeAttribs = Cytoscape.getNodeAttributes();
    public static cytoscape.data.CyAttributes edgeAttribs  = Cytoscape.getEdgeAttributes();
    private ShapePalette                      shapePalette;

    /**
     *
     */
    public DefaultCytoscapeEditor() {
        super();
    }

    // MLC 12/02/06 BEGIN:
    /**
         * specialized initialization code for editor, called by
         * CytoscapeEditorManager when a new editor is built.
         * draws shapes on the palette, based upon the visual style
         *
         * @param args
         *            an arbitrary list of arguments passed to initialization
         *            routine. Not used in this editor
         */
    public void initializeControls(List args) {
        shapePalette = CytoscapeEditorManager.getCurrentShapePalette();
        shapePalette.clear();
        generatePaletteEntries();
        shapePalette.showPalette();
        super.initializeControls(null);
    }

    protected void generatePaletteEntries() {
        generateEdgePaletteEntries(getControllingEdgeAttribute());
        generateNodePaletteEntries(getControllingNodeAttribute());
    }

    protected void generateEdgePaletteEntries(String controllingAttribute) {
        EdgeAppearanceCalculator eac = Cytoscape.getVisualMappingManager()
                                                .getVisualStyle()
                                                .getEdgeAppearanceCalculator();

        if (eac == null) {
            return;
        }

        // System.out.println("Got edgeAppearanceCalculator: " + eac);		
        ShapePaletteInfoGenerator palGen = CytoscapeEditorFactory.INSTANCE.createShapePaletteInfoGenerator();

        // System.out.println("Got edge target arrow calculator: " + edgeCalc);
        byte[] calcsToUse = new byte[] { VizMapUI.EDGE_TGTARROW };

        Iterator<ShapePaletteInfo> spEntries = palGen.buildShapePaletteInfo(eac,
                                                                                 calcsToUse,
                                                                                 controllingAttribute,
                                                                                 this,
                                                                                 null);

        if (!spEntries.hasNext()) {
            shapePalette.addShape(controllingAttribute,
                                  "DirectedEdge",
                                  new CytoShapeIcon(Arrow.BLACK_DELTA),
                                  "Directed Edge");
        } else {
            while (spEntries.hasNext()) {
                ShapePaletteInfo spi = spEntries.next();
                // System.out.println("   edge palette info = " + spi);
                shapePalette.addShape(spi.getControllingAttributeName(),
                                      spi.getKey(),
                                      new CytoShapeIcon((Arrow) spi.getValue(VizMapUI.EDGE_TGTARROW)),
                                      spi.getKey());
            }
        }
    }

    protected void generateNodePaletteEntries(String controllingAttribute) {
        NodeAppearanceCalculator nac = Cytoscape.getVisualMappingManager()
                                                .getVisualStyle()
                                                .getNodeAppearanceCalculator();

        if (nac == null) {
            return;
        }

        ShapePaletteInfoGenerator  palGen     = CytoscapeEditorFactory.INSTANCE.createShapePaletteInfoGenerator();
        byte[]                     calcsToUse = new byte[] {
                                                    VizMapUI.NODE_COLOR,
                                                    VizMapUI.NODE_SHAPE,
                                                    VizMapUI.NODE_SIZE
                                                };
        Iterator<ShapePaletteInfo> spEntries  = palGen.buildShapePaletteInfo(nac,
                                                                                  calcsToUse,
                                                                                  controllingAttribute,
                                                                                  this,
                                                                                  null);

        if (!spEntries.hasNext()) {
            shapePalette.addShape(controllingAttribute,
                                  "DefaultNode",
                                  new CytoShapeIcon(nac.getDefaultAppearance()
                                                       .getShape(),
                                                    nac.getDefaultAppearance()
                                                       .getFillColor()),
                                  "Add a Node");
        } else {
            while (spEntries.hasNext()) {
                ShapePaletteInfo spi = spEntries.next();

                // System.out.println("   node palette entry = " + spi);
                Color nodeColor = (Color) spi.getValue(VizMapUI.NODE_COLOR);
                byte  nodeShape = (Byte) spi.getValue(VizMapUI.NODE_SHAPE);
                int   nodeSize  = (int) ((Double) spi.getValue(VizMapUI.NODE_SIZE)).longValue();
                shapePalette.addShape(spi.getControllingAttributeName(),
                                      spi.getKey(),
                                      new CytoShapeIcon(
                                                        nodeShape,
                                                        nodeColor,
                                                        new Dimension(nodeSize,
                                                                      nodeSize)),
                                      spi.getKey());
            }
        }
    }

    protected ShapePalette getShapePalette() {
        return shapePalette;
    }

    //    /**
    //     * specialized initialization code for editor, called by
    //     * CytoscapeEditorManager when a new editor is built.
    //     * draws shapes on the palette, based upon the visual style
    //     *
    //     * @param args
    //     *            an arbitrary list of arguments passed to initialization
    //     *            routine. Not used in this editor
    //     */
    //    public void initializeControls(List args) {
    //        shapePalette = CytoscapeEditorManager.getCurrentShapePalette();
    //        shapePalette.clear();
    //
    //        VisualMappingManager manager = Cytoscape.getVisualMappingManager();
    //
    //        VisualStyle          vizStyle = manager.getVisualStyle();
    //        System.out.println("Got visual style: " + vizStyle);
    //
    //        // first do edges
    //        EdgeAppearanceCalculator eac = vizStyle.getEdgeAppearanceCalculator();
    //        System.out.println("Got edgeAppearanceCalculator: " + eac);
    //
    //        // TODO: MLC: Place in a method for handling edge arrows:
    //        GenericEdgeArrowCalculator edgeCalc = null;
    //
    //        if (eac != null) {
    //            edgeCalc = (GenericEdgeArrowCalculator) eac.getEdgeTargetArrowCalculator();
    //            System.out.println("Got edge target arrow calculator: " + edgeCalc);
    //
    //            // TODO: MLC: Remove this?:
    //            if (edgeCalc == null) {
    //            }
    //        }
    //
    //        DiscreteMapping dArrow = null;
    //
    //        if (edgeCalc != null) {
    //            Vector edgeMappings = edgeCalc.getMappings();
    //
    //            // TODO: MLC: This code is duplicated 3
    //            //       times--make a method called to compute the
    //            //       mapping:
    //            for (int i = 0; i < edgeMappings.size(); i++) {
    //                if (edgeMappings.get(i) instanceof DiscreteMapping) {
    //                    DiscreteMapping dArrowCandidate = (DiscreteMapping) edgeMappings.get(i);
    //                    // AJK: 07/06/06 default editor shouldn't set NODE_TYPE as controlling attribute
    //                    //					if (attr.equals(controllingEdgeAttribute)) {
    //                    dArrow = dArrowCandidate;
    //
    //                    break;
    //                    //					}
    //                }
    //            }
    //        }
    //
    //        if (dArrow == null) {
    //            shapePalette.addShape(EDGE_TYPE,
    //                                  "DirectedEdge",
    //                                  new CytoShapeIcon(Arrow.BLACK_DELTA),
    //                                  "Directed Edge");
    //        } else {
    //            Arrow arrowType;
    //            Map   edgeTargetArrows = dArrow.getAll();
    //
    //            Set      mapKeys = edgeTargetArrows.keySet();
    //            Iterator it = mapKeys.iterator();
    //
    //            while (it.hasNext()) {
    //                Object arrowKey = it.next();
    //                String keyName = arrowKey.toString();
    //                arrowType = (Arrow) dArrow.getMapValue(arrowKey);
    //                shapePalette.addShape(EDGE_TYPE,
    //                                      keyName,
    //                                      new CytoShapeIcon(arrowType),
    //                                      keyName);
    //
    //                // add this as a change listener. Make sure it's unique by
    //                // removing
    //                // any past listeners
    //                // TODO: MLC: Shouldn't this be moved out of the loop?:
    //                dArrow.removeChangeListener(this);
    //                dArrow.addChangeListener(this);
    //            }
    //        }
    //
    //        // then add nodes
    //        Color           nodeColor = null;
    //        byte            nodeShape;
    //        DiscreteMapping dfill  = null;
    //        DiscreteMapping dshape = null;
    //
    //        NodeAppearanceCalculator nac = vizStyle.getNodeAppearanceCalculator();
    //
    //        //		System.out.println("Got NodeAppearanceCalculator: " + nac);
    //        GenericNodeColorCalculator nfill = null;
    //
    //        if (nac != null) {
    //            nfill = (GenericNodeColorCalculator) nac.getNodeFillColorCalculator();
    //        }
    //
    //        if (nfill == null) {
    //            nodeColor = nac.getDefaultNodeFillColor();
    //        } else {
    //            // TODO: MLC: This code is duplicated 3
    //            //       times--make a method called to compute the
    //            //       mapping:
    //            Vector mappings = nfill.getMappings();
    //            dfill = null;
    //
    //            for (int i = 0; i < mappings.size(); i++) {
    //                if (mappings.get(i) instanceof DiscreteMapping) {
    //                    DiscreteMapping dfillCandidate = (DiscreteMapping) mappings.get(i);
    //                    // AJK: 07/06/06 default editor shouldn't set NODE_TYPE as controlling attribute
    //                    //					if (attr.equals(controllingNodeAttribute)) {
    //                    dfill = dfillCandidate;
    //
    //                    break;
    //                    //					}
    //                }
    //            }
    //
    //            if (dfill == null) {
    //                nodeColor = nac.getDefaultNodeFillColor();
    //            } else {
    //                // add this as a change listener. Make sure it's unique by
    //                // removing
    //                // any past listeners
    //                System.out.println("NODE COLOR: real controlling attribute = " +
    //                                   getControllingNodeAttribute() +
    //                                   " color mapping controlling attribute = " +
    //                                   dfill.getControllingAttributeName());
    //                nfill.removeChangeListener(this);
    //                nfill.addChangeListener(this);
    //            }
    //        }
    //
    //        GenericNodeShapeCalculator nshape = null;
    //
    //        if (nac != null) {
    //            nshape = (GenericNodeShapeCalculator) nac.getNodeShapeCalculator();
    //        }
    //
    //        if (nshape == null) {
    //            nodeShape = nac.getDefaultNodeShape();
    //        } else {
    //            // TODO: MLC: This code is duplicated 3
    //            //       times--make a method called to compute the
    //            //       mapping:
    //            Vector mappings = nshape.getMappings();
    //
    //            for (int i = 0; i < mappings.size(); i++) {
    //                if (mappings.get(i) instanceof DiscreteMapping) {
    //                    DiscreteMapping dshapeCandidate = (DiscreteMapping) mappings.get(i);
    //                    // MLC 07/27/06:
    //                    //					String attr = dshapeCandidate.getControllingAttributeName();
    //                    //					if (attr.equals(controllingNodeAttribute)) {
    //                    dshape = dshapeCandidate;
    //
    //                    break;
    //                    //					}
    //                }
    //            }
    //
    //            if (dshape == null) {
    //                nodeShape = nac.getDefaultNodeShape();
    //            } else {
    //                System.out.println("NODE SHAPE: real controlling attribute = " +
    //                                   getControllingNodeAttribute() +
    //                                   " shape mapping controlling attribute = " +
    //                                   dshape.getControllingAttributeName());
    //                nshape.removeChangeListener(this);
    //                nshape.addChangeListener(this);
    //            }
    //        }
    //
    //        Color defaultNodeColor = nac.getDefaultNodeFillColor();
    //        byte  defaultNodeShape = nac.getDefaultNodeShape();
    //
    //        // TODO: MLC: remove this if and handle each case in following else for when
    //        //            nodeShapeIt==null and nodeColorIt==null.
    //        //            Separate code into a node shape and node color shape creater.
    //        if ((dshape == null) && (dfill == null)) {
    //            shapePalette.addShape("NODE_TYPE",
    //                                  "DefaultNode",
    //                                  new CytoShapeIcon(defaultNodeShape,
    //                                                    defaultNodeColor),
    //                                  "Add a Node");
    //        } else {
    //            // TODO: MLC: This else is complicated and needs comments:
    //            Map      nodeColorValues = (dfill == null) ? null : dfill.getAll();
    //            Map      nodeShapeValues = (dshape == null) ? null : dshape.getAll();
    //            Set      nodeColorKeys   = (nodeColorValues == null) ? null
    //                                       : nodeColorValues.keySet();
    //            Set      nodeShapeKeys   = (nodeShapeValues == null) ? null
    //                                       : nodeShapeValues.keySet();
    //            Iterator nodeShapeIt     = (nodeShapeKeys == null) ? null
    //                                       : nodeShapeKeys.iterator();
    //            Iterator nodeColorIt     = (nodeColorKeys == null) ? null
    //                                       : nodeColorKeys.iterator();
    //            List     keysVisited     = new ArrayList();
    //
    //            // MLC: TODO: Is this code correct in that if I have 6 different colors and
    //            //            5 different shapes, shouldn't a palette entry be made for
    //            //            each combination (30 entries) versus one for each color and one
    //            //            one for each shape (the color ones created use the default shape):
    //            if (nodeShapeIt != null) {
    //                while (nodeShapeIt.hasNext()) {
    //                    Object shapeKey = nodeShapeIt.next();
    //
    //                    if (!keysVisited.contains(shapeKey)) {
    //                        keysVisited.add(shapeKey);
    //
    //                        String shapeKeyName = shapeKey.toString();
    //                        nodeShape = ((Byte) dshape.getMapValue(shapeKey)).byteValue();
    //
    //                        if (dfill == null) {
    //                            nodeColor = nac.getDefaultNodeFillColor();
    //                        } else {
    //                            nodeColor = (Color) dfill.getMapValue(shapeKey);
    //
    //                            if (nodeColor == null) {
    //                                nodeColor = nac.getDefaultNodeFillColor();
    //                            }
    //                        }
    //
    //                        shapePalette.addShape("NODE_TYPE",
    //                                              shapeKeyName,
    //                                              new CytoShapeIcon(nodeShape,
    //                                                                nodeColor),
    //                                              shapeKeyName);
    //                    }
    //                }
    //            }
    //
    //            if (nodeColorIt != null) {
    //                while (nodeColorIt.hasNext()) {
    //                    Object colorKey = nodeColorIt.next();
    //
    //                    if (!keysVisited.contains(colorKey)) {
    //                        keysVisited.add(colorKey);
    //
    //                        String colorKeyName = colorKey.toString();
    //                        nodeColor = (Color) dfill.getMapValue(colorKey);
    //                        // at this point, we will have visited all shape keys,
    //                        // so shape key for this
    //                        // color key would be null
    //                        shapePalette.addShape("NODE_TYPE",
    //                                              colorKeyName,
    //                                              new CytoShapeIcon(defaultNodeShape,
    //                                                                nodeColor),
    //                                              colorKeyName);
    //                    }
    //                }
    //            }
    //        }
    //
    //        shapePalette.showPalette();
    //
    //        super.initializeControls(null);
    //    }
    //
    // MLC 12/02/06 END.
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

    /**
     * redraw palette when a shape, color, or arrow mapping changes
     *
     * @param e
     */
    public void stateChanged(ChangeEvent e) {
        initializeControls(null);
    }
}
