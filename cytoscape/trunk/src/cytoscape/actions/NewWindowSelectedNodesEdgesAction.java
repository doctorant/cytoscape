//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.util.*;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import y.base.*;
import y.view.Graph2D;

import giny.model.RootGraph;
import giny.model.GraphPerspective;
import giny.view.*;
import luna.LunaRootGraph; //For instantiating new RootGraph
import phoebe.PGraphView;  //for instanciating new Graph View

import cytoscape.GraphObjAttributes;
import cytoscape.SelectedSubGraphFactory;
import cytoscape.data.CyNetwork;
import cytoscape.view.CyWindow;
//-------------------------------------------------------------------------
public class NewWindowSelectedNodesEdgesAction extends AbstractAction {
    CyWindow cyWindow;
    
    public NewWindowSelectedNodesEdgesAction(CyWindow cyWindow) {
        super("Selected nodes, Selected edges");
        this.cyWindow = cyWindow;
    }

    public void actionPerformed(ActionEvent e) {
	 if (cyWindow.getCytoscapeObj().getConfiguration().isYFiles()) {  
		 performInYFilesMode();
	 }
	 else{
		 performInGinyMode();
	 }
    }//acionPerformed
    
    void performInYFilesMode(){
        //save the vizmapper catalog
        cyWindow.getCytoscapeObj().saveCalculatorCatalog();
        CyNetwork oldNetwork = cyWindow.getNetwork();
        String callerID = "NewWindowSelectedNodesEdgesAction.actionPerformed";
        oldNetwork.beginActivity(callerID);
        Graph2D graph = oldNetwork.getGraph();

        // hide unselected edges
        //needed since the factory ignores selection state of edges
        Set hiddenEdges = new HashSet();
        for (EdgeCursor edges = graph.edges(); edges.ok(); edges.next()) {
            if ( !graph.isSelected( edges.edge() ) ) {
                hiddenEdges.add( edges.edge() );
                cyWindow.getGraphHider().hide( edges.edge() );
            }
        }

        SelectedSubGraphFactory factory
            = new SelectedSubGraphFactory(graph,
                                          oldNetwork.getNodeAttributes(),
                                          oldNetwork.getEdgeAttributes() );
        Graph2D subGraph = factory.getSubGraph();
        GraphObjAttributes newNodeAttributes = factory.getNodeAttributes();
        GraphObjAttributes newEdgeAttributes = factory.getEdgeAttributes();
        CyNetwork newNetwork = new CyNetwork(subGraph, newNodeAttributes,
                newEdgeAttributes, oldNetwork.getExpressionData() );
        newNetwork.setNeedsLayout(true);
        
        // unhide unselected nodes & edges
        for (Iterator i = hiddenEdges.iterator(); i.hasNext(); ) {
            cyWindow.getGraphHider().unhide( (Edge)i.next() );
        }
        oldNetwork.endActivity(callerID);
        
        String title = "selection";
        try {
            //this call creates a WindowOpened event, which is caught by
            //cytoscape.java, enabling that class to manage the set of windows
            //and quit when the last window is closed
            CyWindow newWindow = new CyWindow(cyWindow.getCytoscapeObj(),
                                              newNetwork, title);
            newWindow.showWindow();
        } catch (Exception e00) {
            System.err.println("exception when creating new window");
            e00.printStackTrace();
        }
    }
    
    void performInGinyMode() {
	    
	     //save the vizmapper catalog
        cyWindow.getCytoscapeObj().saveCalculatorCatalog();
        CyNetwork oldNetwork = cyWindow.getNetwork();
        String callerID = "NewWindowSelectedNodesEdgesAction.actionPerformed";
        //oldNetwork.beginActivity(callerID);
        GraphView view = cyWindow.getView();
        // hide unselected edges
        //needed since the factory ignores selection state of edges
       int [] nodes = view.getSelectedNodeIndices();
      int[] edges = view.getSelectedEdgeIndices();
      	GraphPerspective subGraph = view.getGraphPerspective().createGraphPerspective(nodes, edges);
	PGraphView subView = new PGraphView(subGraph);
	//LunaRootGraph rootGraph = new LunaRootGraph (subGraph);
        GraphObjAttributes newNodeAttributes = oldNetwork.getNodeAttributes();
        GraphObjAttributes newEdgeAttributes = oldNetwork.getEdgeAttributes();
	
        CyNetwork newNetwork = new CyNetwork(subGraph, newNodeAttributes,
                newEdgeAttributes, oldNetwork.getExpressionData(), true );
        newNetwork.setNeedsLayout(true);
      
        //oldNetwork.endActivity(callerID);
        
        String title =  " selection";
        try {
            //this call creates a WindowOpened event, which is caught by
            //cytoscape.java, enabling that class to manage the set of windows
            //and quit when the last window is closed
            CyWindow newWindow = new CyWindow(cyWindow.getCytoscapeObj(),
                                              newNetwork, title);
            newWindow.showWindow();
        } catch (Exception e00) {
            System.err.println("exception when creating new window");
            e00.printStackTrace();
        }
    }//end performinginyMode()
    
}

