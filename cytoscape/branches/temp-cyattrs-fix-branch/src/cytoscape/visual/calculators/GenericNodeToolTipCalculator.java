//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.calculators;
//----------------------------------------------------------------------------
import java.util.Map;
import java.util.Properties;
import javax.swing.JPanel;

import giny.model.Node;

import cytoscape.CyNetwork;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.parsers.StringParser;
//----------------------------------------------------------------------------
public class GenericNodeToolTipCalculator extends NodeCalculator implements NodeToolTipCalculator {
    
    public GenericNodeToolTipCalculator(String name, ObjectMapping m) {
	super(name, m);

        String sc = new String();
        Class c = sc.getClass();
        if (!c.isAssignableFrom(m.getRangeClass()) ) {
            String s = "Invalid Calculator: Expected class " + c.toString()
                    + ", got " + m.getRangeClass().toString();
            throw new ClassCastException(s);
        }
    }
    /**
     * Constructor for dynamic creation via properties.
     */
    public GenericNodeToolTipCalculator(String name, Properties props, String baseKey) {
        super(name, props, baseKey, new StringParser(), new String());
    }
    
    public String calculateNodeToolTip(Node node, CyNetwork network) {
        String canonicalName = node.getIdentifier();
        Map attrBundle = getAttrBundle(canonicalName);
        return (String)super.getMapping().calculateRangeValue(attrBundle);
    }
}

