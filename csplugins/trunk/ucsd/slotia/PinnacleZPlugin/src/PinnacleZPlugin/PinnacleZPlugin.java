package PinnacleZPlugin;

import java.awt.event.*;
import javax.swing.*;
import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.task.util.TaskManager;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;

public class PinnacleZPlugin extends CytoscapePlugin
{
	SearchPanel searchPanel = null;
	public PinnacleZPlugin()
	{
		JMenuItem menuItem = new JMenuItem("PinnacleZ...");
		menuItem.addActionListener(new PluginAction());
		Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("Plugins").add(menuItem);
	}

	class PluginAction implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if (searchPanel == null)
			{
				searchPanel = new SearchPanel();
				searchPanel.addSearchActionListener(new SearchAction());
			}

			CytoPanel cytoPanel = Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST);
			int index = cytoPanel.indexOfComponent(searchPanel);
			if (index < 0)
			{
				searchPanel.setVisible(true);
				cytoPanel.add("PinnacleZ", searchPanel);
				index = cytoPanel.indexOfComponent(searchPanel);
			}
			cytoPanel.setSelectedIndex(index);
			cytoPanel.setState(CytoPanelState.DOCK);
		}
	}

	class SearchAction implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			JTaskConfig jTaskConfig = new JTaskConfig();
			jTaskConfig.displayCancelButton(true);
			jTaskConfig.displayCloseButton(false);
			jTaskConfig.displayStatus(true);
			jTaskConfig.displayTimeElapsed(true);
			jTaskConfig.displayTimeRemaining(false);
			jTaskConfig.setAutoDispose(true);
			jTaskConfig.setModal(true);
			jTaskConfig.setOwner(Cytoscape.getDesktop());
			TaskManager.executeTask(new SearchTask(searchPanel), jTaskConfig);
		}
	}
}
