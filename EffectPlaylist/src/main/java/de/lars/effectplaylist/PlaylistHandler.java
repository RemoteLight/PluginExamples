package de.lars.effectplaylist;

import de.lars.remotelightcore.animation.Animation;
import de.lars.remotelightcore.animation.AnimationManager;
import de.lars.remotelightcore.settings.SettingsManager;
import de.lars.remotelightcore.settings.types.SettingObject;

import java.sql.Time;
import java.util.*;

public class PlaylistHandler {

    /** playlist setting id prefix */
    private final String playlistKeyPre = EffectPlaylist.SETTING_PRE + "playlist.";
    private final SettingsManager sm;
    /** a list of all playlist HashMaps (Animation name, duration) */
    private final List<Playlist> listPlaylist;

    /** current active playlist; null if none active */
    private Playlist activePlaylist;
    private final Timer timer;
    private boolean loopPlaylist = true;

    public PlaylistHandler(SettingsManager sm) {
        this.sm = sm;
        listPlaylist = new ArrayList<Playlist>();
        timer = new Timer();
    }


    /**
     * Load all saved playlists from settings
     */
    @SuppressWarnings("unchecked")
    public void loadALl() {
        SettingObject objData = sm.getSettingObject(EffectPlaylist.SETTING_PRE + ".playlistIds");
        if(objData != null && objData.getValue() instanceof List) {
            List<String> listIDs = (List<String>) objData.getValue();
            // load all playlists
            for(String id : listIDs) {
                String playlistID = playlistKeyPre + id;
                LinkedHashMap<String, Integer> mapPlaylist = loadPlaylist(playlistID);

                if(mapPlaylist == null) {
                    System.err.println(EffectPlaylist.PREFIX + "Could not load stored playlist for ID '" + id + "'.");
                    continue;
                }

                // create playlist instance
                Playlist playlist = new Playlist(id);
                playlist.getPlaylistMap().putAll(mapPlaylist);
                listPlaylist.add(playlist);
            }
        }
        System.out.println(EffectPlaylist.PREFIX + "Loaded " + listPlaylist.size() + " stored playlists.");
    }

    /**
     * Load a playlist from type {@link LinkedHashMap} from settings
     * @param id    the id or name of the playlist
     * @return      the hash map (playlist) or null
     */
    @SuppressWarnings("unchecked")
    public LinkedHashMap<String, Integer> loadPlaylist(String id) {
        SettingObject settingPlaylist = sm.getSettingObject(playlistKeyPre + id);
        Object objData = settingPlaylist.getValue();
        if(objData instanceof LinkedHashMap) {
            return (LinkedHashMap<String, Integer>) objData;
        }
        return null;
    }


    /**
     * Save all registered playlist IDs and their playlist data
     */
    public void saveAll() {
        // store all playlist settings IDs in a list
        List<String> listIDs = new ArrayList<String>();
        for(Playlist playlist : listPlaylist) {
            listIDs.add(playlist.getId());
            // store playlist data
            SettingObject settingPlaylist = sm.addSetting(new SettingObject(playlistKeyPre + playlist.getId(), "Playlist dara", playlist.getPlaylistMap()));
            settingPlaylist.setValue(playlist.getPlaylistMap());
        }
        SettingObject settingIDs = sm.addSetting(new SettingObject(EffectPlaylist.SETTING_PRE + ".playlistIds", "Playlist IDs", listIDs));
        settingIDs.setValue(listIDs);
    }

    public List<Playlist> getAllPlaylists() {
        return listPlaylist;
    }

    /**
     * Add a new playlist
     * @param playlist  the new playlist
     * @return          true if the playlist could be added,
     *                  false if the playlist ID is already in use
     */
    public boolean addPlaylist(Playlist playlist) {
        if(getPlaylist(playlist.getId()) != null)
            return false;
        listPlaylist.add(playlist);
        return true;
    }

    public void removePlaylist(Playlist playlist) {
        listPlaylist.remove(playlist);
    }

    public void removePlaylist(String id) {
        Playlist playlist = getPlaylist(id);
        if(playlist != null)
            removePlaylist(playlist);
    }

    /**
     * Get playlist by name
     * @param id    the id/name of the playlist
     * @return      the playlist with specified id or null
     */
    public Playlist getPlaylist(String id) {
        for(Playlist playlist : listPlaylist) {
            if(playlist.getId().equalsIgnoreCase(id))
                return playlist;
        }
        return null;
    }

    /**
     * Check if some playlist is running
     * @return  true if active, false otherwise
     */
    public boolean isActive() {
        return activePlaylist != null;
    }

    /**
     * Get active playlist
     * @return  active playlist or null
     */
    public Playlist getActivePlaylist() {
        return activePlaylist;
    }

    public void startPlaylist(Playlist playlist) {
        if(isActive()) {
            // stop active playlist
            stopPlaylist();
        }
        if(playlist != null) {
            activePlaylist = playlist;
            timer.schedule(new PlaylistTask(), 0);
        }
    }

    public void stopPlaylist() {
        if(isActive()) {
            timer.cancel();
            timer.purge();
            activePlaylist = null;
        }
    }


    /**
     * Playlist Timer Task class
     */
    private class PlaylistTask extends TimerTask {

        @Override
        public void run() {
            if(activePlaylist != null) {
                Map.Entry<String, Integer> element = activePlaylist.getCurrentElement();
                String animation = element.getKey();
                int duration = element.getValue();

                AnimationManager manager = EffectPlaylist.getInstance().getInterface().getAnimationManager();
                // find animation
                Animation ani = findAnimation(manager.getAnimations(), animation);
                // if animation can not be found, then skip and go to next element
                if(ani == null) {
                    timer.schedule(new PlaylistTask(), 0);
                    return;
                }
                // start animation
                manager.start(ani);

                // plan next element
                boolean finished = activePlaylist.nextIndex();
                // if this was the last element and loop is disabled, then stop
                if(finished && !loopPlaylist) {
                    stopPlaylist();
                    return;
                }

                // schedule new task
                timer.schedule(new PlaylistTask(), duration);
            }
        }

        private Animation findAnimation(List<Animation> animations, String name) {
            for(Animation a : animations) {
                if(a.getName().equals(name))
                    return a;
            }
            return null;
        }

    }

}
