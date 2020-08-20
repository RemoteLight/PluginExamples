package de.lars.effectplaylist;

import de.lars.remotelightclient.ui.panels.tools.ToolsPanel;
import de.lars.remotelightplugins.Plugin;
import de.lars.remotelightplugins.properties.DefaultProperties;

public class EffectPlaylist extends Plugin {

    /** setting key prefix */
    public final static String SETTING_PRE = "effectplaylist.";
    public final static String PREFIX = "[EffectPlaylist] ";

    private static EffectPlaylist instance;
    private PlaylistHandler handler;

    public static EffectPlaylist getInstance() {
        return instance;
    }

    public PlaylistHandler getHandler() {
        return handler;
    }

    @Override
    public void onEnable() {
        instance = this;
        // create entry of tools panel
        PlaylistEntryPanel entry = new PlaylistEntryPanel();
        // register entry
        ToolsPanel.getEntryList().add(entry);

        // create playlist handler
        handler = new PlaylistHandler(getInterface().getSettingsManager());
        // load stored playlist (if previous stored)
        handler.loadALl();
        System.out.println(PREFIX + "Enabled EffectPlaylist " + instance.getPluginInfo().getValue(DefaultProperties.VERSION));
    }

    @Override
    public void onDisable() {
        System.out.println(PREFIX + "Saving all playlists...");
        handler.stopPlaylist();
        handler.saveAll();
    }

    public boolean isLoaded() {
        return true;
    }

}
