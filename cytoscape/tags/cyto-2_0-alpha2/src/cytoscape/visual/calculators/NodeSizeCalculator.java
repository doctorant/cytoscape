//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.calculators;
//----------------------------------------------------------------------------
import java.util.Map;
import javax.swing.JPanel;

import giny.model.Node;

import cytoscape.CyNetwork;
//----------------------------------------------------------------------------
public interface NodeSizeCalculator extends Calculator {
    
    double calculateNodeSize(Node node, CyNetwork network);
}

