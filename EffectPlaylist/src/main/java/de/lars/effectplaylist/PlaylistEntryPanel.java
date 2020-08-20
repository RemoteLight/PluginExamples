package de.lars.effectplaylist;

import de.lars.effectplaylist.ui.PlaylistPanel;
import de.lars.remotelightclient.ui.panels.tools.ToolsPanel;
import de.lars.remotelightclient.ui.panels.tools.ToolsPanelEntry;

import javax.swing.*;

public class PlaylistEntryPanel extends ToolsPanelEntry {

    public PlaylistEntryPanel() {
    }

    public String getName() {
        return "Effect Playlist";
    }

    @Override
    public JPanel getMenuPanel(ToolsPanel context) {
        return new PlaylistPanel(context, EffectPlaylist.getInstance());
    }

}
