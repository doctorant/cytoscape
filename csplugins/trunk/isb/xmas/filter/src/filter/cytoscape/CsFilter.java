package filter.cytoscape;

import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import cytoscape.util.*;
import cytoscape.plugin.*;
import cytoscape.plugin.jar.*;
import filter.model.*;
import filter.view.*;
import cytoscape.CyNetwork;
import javax.swing.*;
import filter.cytoscape.network.*;

public class CsFilter extends AbstractPlugin {
 protected JFrame frame;
  protected CyWindow window;
  protected CyNetwork network;
  protected FilterUsePanel filterUsePanel;

  public CsFilter ( CyWindow window ) {
    this.window = window;
    this.network = window.getNetwork();
    initialize();
  }

  public void initialize () {

    ImageIcon icon = new ImageIcon( JarLoader.getLoader().getObjectIfYouWantIt( "filter36.gif" ) );
    ImageIcon icon2 = new ImageIcon( JarLoader.getLoader().getObjectIfYouWantIt( "filter16.gif" ) );
                                    //getClass().getResource("filter16.gif") );
    FilterPlugin action = new FilterPlugin( network, window, icon, this );
    FilterMenuItem menu_action = new FilterMenuItem(  network, window, icon2, this );
    
    window.getCyMenus().addCytoscapeAction( ( CytoscapeAction )action );
    window.getCyMenus().addCytoscapeAction( ( CytoscapeAction )menu_action );

    CreateAddNetwork can = new CreateAddNetwork( null );
    window.getCyMenus().addCytoscapeAction( can );
    FilterDataView fdv = new FilterDataView( null );
    window.getCyMenus().addCytoscapeAction( fdv );

     
     //FilterManager.defaultManager().addEditor( new DefaultFilterEditor() );
     //FilterManager.defaultManager().addEditor( new FilterTreeEditor() );
     //FilterManager.defaultManager().addEditor( new CsNodeTypeFilterEditor( window.getNetwork() ) );
     //FilterManager.defaultManager().addEditor( new CsEdgeTypeFilterEditor( window.getNetwork() ) );
     //FilterManager.defaultManager().addEditor( new CsNodeInteractionFilterEditor( window.getNetwork() ) );
     //FilterManager.defaultManager().addEditor( new CsAttributeValueFilterEditor( window.getNetwork() ) );
				FilterManager.defaultManager().addEditor( new NumericAttributeFilterEditor( window ) );
				FilterManager.defaultManager().addEditor( new StringPatternFilterEditor (window)); 
			 FilterManager.defaultManager().addEditor( new InteractionFilterEditor( window,FilterManager.defaultManager().getFilters(false)));	
				FilterManager.defaultManager().addEditor( new NodeTopologyFilterEditor(window,FilterManager.defaultManager().getFilters(false))); 
				FilterManager.defaultManager().addEditor( new BooleanMetaFilterEditor (FilterManager.defaultManager().getFilters(false)));
		}

  public String describe () {
    return "New Filters";
  }

  public  FilterUsePanel getFilterUsePanel () {
    if ( filterUsePanel == null ) {
      filterUsePanel = new FilterUsePanel( network, window );
     }
    return filterUsePanel;
  }
                
  public void show () {
    if ( frame == null ) {
      frame = new JFrame( "Use Filters" );
      frame.getContentPane().add( getFilterUsePanel() );
      frame.pack();
    }
    frame.setVisible( true );
  }

}
