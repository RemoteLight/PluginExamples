package de.lars.effectplaylist;

import de.lars.remotelightclient.ui.panels.tools.ToolsPanel;
import de.lars.remotelightplugins.Plugin;
import de.lars.remotelightplugins.properties.DefaultProperties;

public class EffectPlaylist extends Plugin {

    /** setting key prefix */
    public final static String SETTING_PRE = "effectplaylist.";
    public final static String PREFIX = "[EffectPlaylist] ";

    private static EffectPlaylist instance;
    private final PlaylistHandler handler;

    public EffectPlaylist() {
        instance = EffectPlaylist.this;
        // create entry of tools panel
        PlaylistEntryPanel entry = new PlaylistEntryPanel();
        // register entry
        ToolsPanel.getEntryList().add(entry);

        // create playlist handler
        handler = new PlaylistHandler(getInterface().getSettingsManager());
        // load stored playlist (if previous stored)
        handler.loadALl();
    }

    public static EffectPlaylist getInstance() {
        return instance;
    }

    public PlaylistHandler getHandler() {
        return handler;
    }

    @Override
    public void onEnable() {
        new EffectPlaylist();
        System.out.println("Enabled EffectPlaylist " + instance.getPluginInfo().getValue(DefaultProperties.VERSION));
    }

    public boolean isLoaded() {
        return true;
    }

}
