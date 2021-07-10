package com.github.drumber.video;

import javax.swing.JPanel;

import com.github.drumber.video.ui.SettingsPanel;

import de.lars.remotelightclient.ui.panels.tools.ToolsPanel;
import de.lars.remotelightclient.ui.panels.tools.ToolsPanelEntry;

public class VideoEntryPanel extends ToolsPanelEntry {
	
	private final VideoPlugin plugin;

	public VideoEntryPanel(VideoPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public String getName() {
		return "Video Plugin";
	}
	
	@Override
	public JPanel getMenuPanel(ToolsPanel context) {
		return new SettingsPanel(plugin);
	}

}
