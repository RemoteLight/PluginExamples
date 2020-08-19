package de.lars.effectplaylist;

import de.lars.effectplaylist.ui.PlaylistPanel;
import de.lars.remotelightclient.ui.panels.tools.ToolsPanelEntry;
import de.lars.remotelightcore.settings.SettingsManager;

import javax.swing.*;

public class PlaylistEntryPanel extends ToolsPanelEntry {

    public PlaylistEntryPanel() {
    }

    public String getName() {
        return "Effect Playlist";
    }

    @Override
    public JPanel getMenuPanel() {
        return new PlaylistPanel(EffectPlaylist.getInstance());
    }

}
