package cytoscape.visual.ui;

import static cytoscape.visual.VisualPropertyType.EDGE_COLOR;
import static cytoscape.visual.VisualPropertyType.EDGE_LINETYPE;
import static cytoscape.visual.VisualPropertyType.EDGE_LINE_WIDTH;
import static cytoscape.visual.VisualPropertyType.NODE_BORDER_COLOR;
import static cytoscape.visual.VisualPropertyType.NODE_FILL_COLOR;
import static cytoscape.visual.VisualPropertyType.NODE_FONT_FACE;
import static cytoscape.visual.VisualPropertyType.NODE_FONT_SIZE;
import static cytoscape.visual.VisualPropertyType.NODE_HEIGHT;
import static cytoscape.visual.VisualPropertyType.NODE_LABEL;
import static cytoscape.visual.VisualPropertyType.NODE_LABEL_COLOR;
import static cytoscape.visual.VisualPropertyType.NODE_LABEL_POSITION;
import static cytoscape.visual.VisualPropertyType.NODE_LINETYPE;
import static cytoscape.visual.VisualPropertyType.NODE_LINE_WIDTH;
import static cytoscape.visual.VisualPropertyType.NODE_SHAPE;
import static cytoscape.visual.VisualPropertyType.NODE_TOOLTIP;
import static cytoscape.visual.VisualPropertyType.NODE_WIDTH;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;

import org.jdesktop.swingx.border.DropShadowBorder;
import org.jdesktop.swingx.painter.gradient.BasicGradientPainter;

import cytoscape.Cytoscape;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.ui.icon.VisualPropertyIcon;




/**
 * Dialog for editing default visual property values.<br>
 * This is a modal dialog.
 * 
 * <p>
 * 	Basic idea is the following:
 *  <ul>
 *  	<li>Build dummy network with 2 nodes and 1 edge.</li>
 *  	<li>Edit the default appearence of the dummy network</li>
 *  	<li>Create a image from the dummy.</li>
 *  </ul>
 * </p>
 *
 * @version 0.5
 * @since Cytoscape 2.5
 * @author kono
 */
public class DefaultAppearenceBuilder extends JDialog {
    private static final VisualPropertyType[] orderedList = {
            NODE_SHAPE, NODE_FILL_COLOR, NODE_WIDTH, NODE_HEIGHT,
            NODE_BORDER_COLOR, NODE_LINE_WIDTH, NODE_LINETYPE, NODE_LABEL_COLOR,
            NODE_FONT_FACE, NODE_FONT_SIZE, NODE_LABEL, NODE_LABEL_POSITION,
            NODE_TOOLTIP
        };
    private static final VisualPropertyType[] EDGE_PROP_LIST = {
            EDGE_COLOR, EDGE_LINETYPE, EDGE_LINE_WIDTH
        };
    
    private enum Globals {
    	
    }
    
    /**
     * Creates a new DefaultAppearenceBuilder object.
     *
     * @param parent DOCUMENT ME!
     * @param modal DOCUMENT ME!
     */
    public DefaultAppearenceBuilder(Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        buildList();

        this.addComponentListener(
            new ComponentAdapter() {
                public void componentResized(ComponentEvent e) {
                    mainView.createView();
                }
            });
    }

    /**
     * DOCUMENT ME!
     *
     * @param parent DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static JPanel showDialog(Frame parent) {
        final DefaultAppearenceBuilder dialog = new DefaultAppearenceBuilder(parent,
                true);
        
        dialog.mainView.createDummyNetwork();
        
        dialog.setLocationRelativeTo(parent);
        dialog.setSize(700, 300);
        dialog.mainView.repaint();
        dialog.repaint();
        dialog.repaint(); 
        dialog.mainView.repaint();
       
        dialog.setVisible(true);
        //dialog.mainView.createView();
        dialog.mainView.clean();
        
        return dialog.getPanel();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static JPanel getDefaultView() {
        final DefaultAppearenceBuilder dialog = new DefaultAppearenceBuilder(null,
                true);
        dialog.mainView.createDummyNetwork();
        dialog.mainView.createView();
        dialog.setSize(700, 300);
//        dialog.setVisible(true);
//        dialog.setVisible(false);
        dialog.mainView.clean();
        return dialog.getPanel();
    }
    
    public static Image getDefaultViewAsImage() {
        final DefaultAppearenceBuilder dialog = new DefaultAppearenceBuilder(null,
                true);
        dialog.mainView.createView();
        dialog.mainView.clean();
        return dialog.mainView.createImage(dialog.mainView.getWidth(), dialog.mainView.getHeight());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */

    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">
    private void initComponents() {
        jXPanel1 = new org.jdesktop.swingx.JXPanel();
        mainView = new DefaultViewPanel();
        jXTitledPanel1 = new org.jdesktop.swingx.JXTitledPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jXList1 = new org.jdesktop.swingx.JXList();
        jScrollPane2 = new javax.swing.JScrollPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        jCheckBox1 = new javax.swing.JCheckBox();
        applyButton = new javax.swing.JButton();
        
        cancelButton = new javax.swing.JButton();
        cancelButton.setVisible(false);
        
        jCheckBox1.setOpaque(false);

        //        Point2D start = new Point2D.Float(0, 0);
        //        Point2D end = new Point2D.Float(0, 1);
        //        float[] dist = { 0.0f, 0.8f, 1.0f };
        //        Color c1 = new Color(150, 150, 150, 70);
        //
        //        Color[] colors = { Color.white, c1, Color.white };
        //        LinearGradientPaint p = new LinearGradientPaint(start, end, dist, colors);
        //
        //        org.jdesktop.swingx.painter.gradient.LinearGradientPainter painter2 = new LinearGradientPainter(p);
        //        jXPanel1.setBackgroundPainter(painter2);
        jXList1.addMouseListener(
            new MouseListener() {
                public void mouseClicked(MouseEvent e) {
                    listActionPerformed(e);
                }

                public void mouseEntered(MouseEvent arg0) {
                }

                public void mouseExited(MouseEvent arg0) {
                }

                public void mousePressed(MouseEvent arg0) {
                    // TODO Auto-generated method stub
                }

                public void mouseReleased(MouseEvent arg0) {
                    // TODO Auto-generated method stub
                }
            });

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Default Appearence for " +
            Cytoscape.getVisualMappingManager().getVisualStyle().getName());
        mainView.setBorder(
            new javax.swing.border.LineBorder(java.awt.Color.darkGray, 1, true));

        org.jdesktop.layout.GroupLayout jXPanel2Layout = new org.jdesktop.layout.GroupLayout(mainView);
        mainView.setLayout(jXPanel2Layout);
        jXPanel2Layout.setHorizontalGroup(
            jXPanel2Layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(0, 300,
                Short.MAX_VALUE));
        jXPanel2Layout.setVerticalGroup(
            jXPanel2Layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(0, 237,
                Short.MAX_VALUE));

        jXTitledPanel1.setTitle("Default Node Appearence");
        jXTitledPanel1.setTitlePainter(
            new BasicGradientPainter(
                new Point2D.Double(.2d, 0),
                new Color(
                    Color.gray.getRed(),
                    Color.gray.getGreen(),
                    Color.gray.getBlue(),
                    100),
                new Point2D.Double(.8d, 0),
                Color.WHITE));
        jXTitledPanel1.setTitleFont(new java.awt.Font("SansSerif", 1, 12));
        jXTitledPanel1.setMinimumSize(new java.awt.Dimension(300, 27));
        jXTitledPanel1.setPreferredSize(new java.awt.Dimension(300, 27));
        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);

        jXList1.addListSelectionListener(
            new javax.swing.event.ListSelectionListener() {
                public void valueChanged(
                    javax.swing.event.ListSelectionEvent evt) {
                    jXList1ValueChanged(evt);
                }
            });

        jScrollPane1.setViewportView(jXList1);

        jTabbedPane1.addTab("Node", jScrollPane1);

        jTabbedPane1.addTab("Edge", jScrollPane2);

        jTabbedPane1.addTab("Global", jScrollPane3);

        org.jdesktop.layout.GroupLayout jXTitledPanel1Layout = new org.jdesktop.layout.GroupLayout(
                jXTitledPanel1.getContentContainer());
        jXTitledPanel1.getContentContainer()
                      .setLayout(jXTitledPanel1Layout);
        jXTitledPanel1Layout.setHorizontalGroup(
            jXTitledPanel1Layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(jTabbedPane1,
                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 250,
                Short.MAX_VALUE));
        jXTitledPanel1Layout.setVerticalGroup(
            jXTitledPanel1Layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(jTabbedPane1,
                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 243,
                Short.MAX_VALUE));

        jCheckBox1.setFont(new java.awt.Font("SansSerif", 1, 12));
        jCheckBox1.setText("Keep Aspect Ratio");
        jCheckBox1.setBorder(
            javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBox1.setMargin(new java.awt.Insets(0, 0, 0, 0));

        applyButton.setText("Apply");
        applyButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				dispose();
			}
        	
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				dispose();
			}
        	
        });

        org.jdesktop.layout.GroupLayout jXPanel1Layout = new org.jdesktop.layout.GroupLayout(jXPanel1);
        jXPanel1.setLayout(jXPanel1Layout);
        jXPanel1Layout.setHorizontalGroup(
            jXPanel1Layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                jXPanel1Layout.createSequentialGroup().addContainerGap().add(
                    jXPanel1Layout.createParallelGroup(
                        org.jdesktop.layout.GroupLayout.LEADING).add(
                        jXPanel1Layout.createSequentialGroup().add(jCheckBox1,
                            org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 138,
                            Short.MAX_VALUE).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(cancelButton).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(applyButton)).add(mainView,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(jXTitledPanel1,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 198,
                    Short.MAX_VALUE).add(12, 12, 12)));
        jXPanel1Layout.setVerticalGroup(
            jXPanel1Layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                org.jdesktop.layout.GroupLayout.TRAILING,
                jXPanel1Layout.createSequentialGroup().addContainerGap().add(
                    jXPanel1Layout.createParallelGroup(
                        org.jdesktop.layout.GroupLayout.TRAILING).add(org.jdesktop.layout.GroupLayout.LEADING,
                        jXTitledPanel1,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 270,
                        Short.MAX_VALUE).add(
                        jXPanel1Layout.createSequentialGroup().add(mainView,
                            org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                            org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                            Short.MAX_VALUE).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(
                            jXPanel1Layout.createParallelGroup(
                                org.jdesktop.layout.GroupLayout.BASELINE).add(jCheckBox1).add(cancelButton).add(applyButton)))).addContainerGap()));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(
                getContentPane());
        getContentPane()
            .setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(jXPanel1,
                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(jXPanel1,
                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        pack();
    } // </editor-fold>

    private void jXList1ValueChanged(javax.swing.event.ListSelectionEvent evt) {
        // TODO add your handling code here:
    }

    private void listActionPerformed(MouseEvent e) {
        // TODO add your handling code here:
        if (e.getClickCount() == 1) {
            int selected = jXList1.getSelectedIndex();

            try {
                Object newValue = VizMapperMainPanel.showValueSelectDialog(orderedList[selected],
                        this);
                VizMapperMainPanel.apply(newValue, orderedList[selected]);
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            buildList();
            Cytoscape.getVisualMappingManager()
                     .getNetworkView()
                     .redrawGraph(false, true);
            // Cytoscape.getDesktop().setFocus(Cytoscape.getVisualMappingManager().getNetworkView().getIdentifier());
            mainView.createView();

            mainView.repaint();
        }
    }

    // Variables declaration - do not modify
    private javax.swing.JButton applyButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private org.jdesktop.swingx.JXList jXList1;
    private org.jdesktop.swingx.JXPanel jXPanel1;

    //	private org.jdesktop.swingx.JXPanel jXPanel2;
    private org.jdesktop.swingx.JXTitledPanel jXTitledPanel1;

    // End of variables declaration
    protected DefaultViewPanel mainView;

    //	 End of variables declaration
    private JPanel getPanel() {
        return mainView;
    }

    /**
     * DOCUMENT ME!
     */
    public void buildList() {
        /*
         * Get current default node appearence
         */
//        appearenceMap = new HashMap<VisualPropertyType, Object>();

        List<Icon> icons = new ArrayList<Icon>();
        DefaultListModel model = new DefaultListModel();
        jXList1.setModel(model);

        for (VisualPropertyType type : orderedList) {
            final VisualPropertyIcon nodeIcon = (VisualPropertyIcon)(type.getVisualProperty().getDefaultIcon()); 
            nodeIcon.setLeftPadding(15);
            model.addElement(type.getName());
            icons.add(nodeIcon);
//            appearenceMap.put( type, 
//			                   type.getDefault(  
//			                           Cytoscape.getVisualMappingManager().getVisualStyle()) );
        }

        jXList1.setCellRenderer(new VisualPropCellRenderer(icons));

//        objectShape = ((VisualPropertyIcon)(VisualPropertyType.NODE_SHAPE.getVisualProperty().getDefaultIcon())).getShape();

        mainView.createView();

        mainView.repaint();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static JPanel getDefaultPanel() {
        final DefaultAppearenceBuilder dialog = new DefaultAppearenceBuilder(null,
                true);
        dialog.mainView.createDummyNetwork();
        dialog.mainView.clean();

        return dialog.getPanel();
    }

    class VisualPropCellRenderer extends JLabel
        implements ListCellRenderer {
        private final Font SELECTED_FONT = new Font("SansSerif", Font.ITALIC, 18);
        private final Font NORMAL_FONT = new Font("SansSerif", Font.BOLD, 14);
        private final Color SELECTED_COLOR = new Color(0, 5, 80, 30);
        private final Color SELECTED_FONT_COLOR = new Color(0, 150, 255, 120);
        private final List<Icon> icons;

        public VisualPropCellRenderer(List<Icon> icons) {
            this.icons = icons;
            setOpaque(true);
        }

        public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
            final VisualPropertyIcon icon = (VisualPropertyIcon) icons.get(index);

            setText(value.toString());
            setIcon(icon);
            setFont(isSelected ? SELECTED_FONT : NORMAL_FONT);

            this.setVerticalTextPosition(SwingConstants.CENTER);
            // this.setHorizontalTextPosition(SwingConstants.LEFT);
            this.setVerticalAlignment(SwingConstants.CENTER);
            // this.setHorizontalAlignment(SwingConstants.CENTER);
            this.setIconTextGap(75);
            //this.setAlignmentX(150.0f);
            setBackground(isSelected ? SELECTED_COLOR : list.getBackground());
            setForeground(isSelected ? SELECTED_FONT_COLOR : list.getForeground());
            setPreferredSize(new Dimension(250, icon.getIconHeight() + 12));
            // this.setBorder(new LineBorder(Color.black));
            this.setBorder(new DropShadowBorder());

            return this;
        }
    }
}
