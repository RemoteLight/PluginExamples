package de.lars.effectplaylist;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.lars.remotelightcore.animation.Animation;
import de.lars.remotelightcore.animation.AnimationManager;
import de.lars.remotelightcore.musicsync.MusicEffect;
import de.lars.remotelightcore.musicsync.MusicSyncManager;
import de.lars.remotelightcore.settings.SettingsManager;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingObject;

import java.util.*;

public class PlaylistHandler {

    /** playlist setting id prefix of the stored List */
    public final static String PRE_LIST_DATA = EffectPlaylist.SETTING_PRE + "playlist.";
    /** playlist setting id prefix of the loop boolean setting */
    public final static String PRE_BOOL_LOOP = EffectPlaylist.SETTING_PRE + "loop.";
    /** playlist setting id of the store IDs */
    public final static String KEY_LIST_IDS = EffectPlaylist.SETTING_PRE + "playlistIds";

    private final Gson gson;
    private final SettingsManager sm;
    /** a list of all playlist HashMaps (Animation name, duration) */
    private final List<Playlist> listPlaylist;

    /** current active playlist; null if none active */
    private Playlist activePlaylist;
    private final Timer timer;

    public PlaylistHandler(SettingsManager sm) {
        this.gson = new GsonBuilder().serializeNulls().create();
        this.sm = sm;
        listPlaylist = new ArrayList<Playlist>();
        timer = new Timer();
    }


    /**
     * Load all saved playlists from settings
     */
    @SuppressWarnings("unchecked")
    public void loadALl() {
        SettingObject objData = sm.getSettingObject(KEY_LIST_IDS);
        if(objData != null && objData.getValue() instanceof List) {
            List<String> listIDs = (List<String>) objData.getValue();
            // load all playlists
            for(String id : listIDs) {
                List<PlaylistElement> listPlaylistElements = loadPlaylist(id);

                if(listPlaylistElements == null) {
                    System.err.println(EffectPlaylist.PREFIX + "Could not load stored playlist for ID '" + id + "'.");
                    continue;
                }

                // create playlist instance
                Playlist playlist = new Playlist(id);
                playlist.getPlaylistList().addAll(listPlaylistElements);
                SettingBoolean settingLoop = sm.getSetting(SettingBoolean.class, PRE_BOOL_LOOP + id);
                if(settingLoop != null)
                    playlist.setLoop(settingLoop.getValue());
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
    public List<PlaylistElement> loadPlaylist(String id) {
        SettingObject settingPlaylist = sm.getSettingObject(PRE_LIST_DATA + id);
        Object objData = settingPlaylist.getValue();
        if(objData instanceof String) {
            // deserialize json data using Gson
            String jsonData = (String) objData;
            PlaylistElement[] array = gson.fromJson(jsonData, PlaylistElement[].class);
            return Arrays.asList(array);
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
            // add playlist ID to list
            listIDs.add(playlist.getId());

            // serialize data to json using Gson
            PlaylistElement[] dataArray = playlist.getPlaylistList().toArray(new PlaylistElement[0]);
            String jsonData = gson.toJson(dataArray);

            // store playlist data
            SettingObject settingPlaylist = sm.addSetting(
                    new SettingObject(PRE_LIST_DATA + playlist.getId(), "Playlist data", jsonData));
            settingPlaylist.setValue(jsonData);

            // store loop data
            SettingBoolean settingLoop = sm.addSetting(
                    new SettingBoolean(PRE_BOOL_LOOP + playlist.getId(), "Playlist Loop mode", SettingsManager.SettingCategory.Intern, null, playlist.isLoop()));
            settingLoop.setValue(playlist.isLoop());
        }
        // store all playlist IDs
        SettingObject settingIDs = sm.addSetting(new SettingObject(KEY_LIST_IDS, "Playlist IDs", listIDs));
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
            System.out.println(EffectPlaylist.PREFIX + "Started playlist " + activePlaylist.getId());
        }
    }

    public void stopPlaylist() {
        if(isActive()) {
            timer.cancel();
            timer.purge();
            System.out.println(EffectPlaylist.PREFIX + "Stopped playlist " + (activePlaylist != null ? activePlaylist.getId() : "?"));
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
                PlaylistElement element = activePlaylist.getCurrentElement();

                if(element.getType() == PlaylistElement.Type.Animation) {
                    AnimationManager manager = EffectPlaylist.getInstance().getInterface().getAnimationManager();
                    // find animation
                    Animation ani = findAnimation(manager.getAnimations(), element.getName());
                    // if animation can not be found, then skip and go to next element
                    if (ani == null) {
                        timer.schedule(new PlaylistTask(), 0);
                        return;
                    }
                    // start animation
                    manager.start(ani);
                } else {
                    MusicSyncManager manager = EffectPlaylist.getInstance().getInterface().getMusicSyncManager();
                    // find music effect
                    MusicEffect effect = findMusicEffect(manager.getMusicEffects(), element.getName());
                    // if effect can not be found, then skip and go to next element
                    if (effect == null) {
                        timer.schedule(new PlaylistTask(), 0);
                        return;
                    }
                    // start music effect
                    manager.start(effect);
                }

                // plan next element
                boolean finished = activePlaylist.nextIndex();
                // if this was the last element and loop is disabled, then stop
                if(finished && !activePlaylist.isLoop()) {
                    stopPlaylist();
                    return;
                }

                // schedule new task
                timer.schedule(new PlaylistTask(), element.getDuration()*1000);
            }
        }

        private Animation findAnimation(List<Animation> animations, String name) {
            for(Animation a : animations) {
                if(a.getName().equals(name))
                    return a;
            }
            return null;
        }

        private MusicEffect findMusicEffect(List<MusicEffect> musicEffects, String name) {
            for(MusicEffect m : musicEffects) {
                if(m.getName().equals(name))
                    return m;
            }
            return null;
        }

    }

}
