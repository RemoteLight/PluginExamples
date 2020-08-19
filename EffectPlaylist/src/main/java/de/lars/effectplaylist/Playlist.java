package de.lars.effectplaylist;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Playlist {

    /** playlist HashMap (Animation name, duration) */
    private final LinkedHashMap<String, Integer> playlistMap;
    /** playlist id / name */
    private String id;

    private int lastIndex = 0;

    public Playlist(String id) {
        this.id = id;
        playlistMap = new LinkedHashMap<String, Integer>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LinkedHashMap<String, Integer> getPlaylistMap() {
        return playlistMap;
    }

    /**
     * Increment index position and go to the next element in this playlist
     * @return  true if the playlist finished and the next element is the first,
     *          false if there is a next element in the playlist
     */
    public boolean nextIndex() {
        if(++lastIndex >= playlistMap.size()) {
            lastIndex = 0;
            return true;
        }
        return false;
    }

    public void setIndex(int index) {
        lastIndex = index;
    }

    public int getIndex() {
        return lastIndex;
    }

    /**
     * Get the map entry of the current index.
     * @return  the map entry on the current position
     */
    public Map.Entry<String, Integer> getCurrentElement() {
        List<Map.Entry<String, Integer>> list = new ArrayList<>(playlistMap.entrySet());
        return list.get(lastIndex);
    }

}
