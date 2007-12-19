package org.cytoscape.coreplugin.cpath2.view;

import javax.swing.*;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.Document;
import javax.swing.text.html.StyleSheet;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

import cytoscape.util.OpenBrowser;

/**
 * Summary Panel.
 *
 * @author Ethan Cerami.
 */
public class PhysicalEntityDetailsPanel extends JPanel implements MouseListener {
    private Document doc;
    private JTextPane textPane;
	private SearchHitsPanel searchHitsPanel; // ref to parent

    /**
     * Constructor.
     */
    public PhysicalEntityDetailsPanel(SearchHitsPanel searchHitsPanel) {
        this.setLayout(new BorderLayout());
		this.searchHitsPanel = searchHitsPanel;
        textPane = createHtmlTextPane();
        doc = textPane.getDocument();
        JScrollPane scrollPane = encloseInJScrollPane (textPane);

        URL url = GradientHeader.class.getResource ("resources/stock_toggle-graphics-16.png");
        ImageIcon detailsIcon = new ImageIcon(url);
        JButton button = new JButton (detailsIcon);
        button.setToolTipText("Hide Gene Details");
        button.setOpaque(false);

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
				PhysicalEntityDetailsPanel.this.searchHitsPanel.togglePopup(); // amazing syntax!!
            }
        });
        GradientHeader header = new GradientHeader("Gene Details", button);
		// we become gradient header mouse listener - see comment below
		header.addMouseListener(this);

		// ok button
		JButton okButton = new JButton("Ok");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
				PhysicalEntityDetailsPanel.this.searchHitsPanel.togglePopup(); // amazing syntax!!
            }
        });

        add (header, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        JPanel okPanel = new JPanel();
        okPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        okPanel.add(okButton);
        add(okPanel, BorderLayout.SOUTH);
		
    }

	// kill mouse events - fixes bug where user can 
	// click on gradient header and select things underneath
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}

    /**
     * Gets the summary document model.
     * @return Document object.
     */
    public Document getDocument() {
        return doc;
    }

    /**
     * Gets the summary text pane object.
     * @return JTextPane Object.
     */
    public JTextPane getTextPane() {
        return textPane;
    }

    /**
     * Encloses the specified JTextPane in a JScrollPane.
     *
     * @param textPane JTextPane Object.
     * @return JScrollPane Object.
     */
    private JScrollPane encloseInJScrollPane(JTextPane textPane) {
        JScrollPane scrollPane = new JScrollPane(textPane);
        return scrollPane;
    }

    /**
     * Creates a JTextPane with correct line wrap settings.
     *
     * @return JTextPane Object.
     */
    public static JTextPane createHtmlTextPane() {
        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setBorder(new EmptyBorder(7,7,7,7));
        textPane.setContentType("text/html");
        textPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        textPane.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
                if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    OpenBrowser.openURL(hyperlinkEvent.getURL().toString());
                }
            }
        });

        StyleSheet styleSheet = new StyleSheet();
        styleSheet.addRule("h2 {color: #663333; font-size: 120%; font-weight: bold; "
                + "margin-bottom:3px}");
        styleSheet.addRule("h3 {color: #663333; font-size: 105%; font-weight: bold;"
                + "margin-bottom:7px}");
        styleSheet.addRule("ul { list-style-type: none; margin-left: 5px; "
                + "padding-left: 1em;	text-indent: -1em;}");
        styleSheet.addRule("h4 {color: #66333; font-weight: bold; margin-bottom:3px;}");
        styleSheet.addRule("b {background-color: #FFFF00;}");
        styleSheet.addRule(".bold {font-weight:bold;}");
        styleSheet.addRule(".link {color:blue; text-decoration: underline;}");
        styleSheet.addRule(".excerpt {font-size: 90%;}");
        HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
        htmlEditorKit.setStyleSheet(styleSheet);
        textPane.setEditorKit(htmlEditorKit);
        return textPane;
    }
}