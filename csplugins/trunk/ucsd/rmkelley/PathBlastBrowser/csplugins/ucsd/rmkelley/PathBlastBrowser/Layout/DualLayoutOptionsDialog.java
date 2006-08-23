package csplugins.ucsd.rmkelley.PathBlastBrowser.Layout;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;



public class DualLayoutOptionsDialog extends JDialog {

	JCheckBox edges;
	DualLayoutCommandLineParser parser;
	protected static int HEIGHT = 100;
	protected static int WIDTH = 300;
	public DualLayoutOptionsDialog(DualLayoutCommandLineParser parser){
		setModal(true);
		this.parser = parser;
		edges = new JCheckBox("Create edges for homology?",parser.addEdges());
		edges.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				DualLayoutOptionsDialog.this.parser.setEdges(edges.isSelected());
			}
		});
		JPanel centerPanel = new JPanel();
		centerPanel.add(edges);
		
		JPanel southPanel = new JPanel();
		JButton dismiss = new JButton("Dismiss");
		dismiss.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				DualLayoutOptionsDialog.this.hide();
			}
		});
		southPanel.add(dismiss);
		
		getContentPane().add(centerPanel,BorderLayout.CENTER);
		getContentPane().add(southPanel,BorderLayout.SOUTH);
		setSize(WIDTH,HEIGHT);
		
	}
}
