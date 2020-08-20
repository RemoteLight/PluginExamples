package de.lars.effectplaylist;

import java.util.ArrayList;
import java.util.List;

public class Playlist {

    /** playlist List */
    private final List<PlaylistElement> playlistList;
    /** playlist id / name */
    private String id;
    /** shuffle mode */
    private boolean loop = true;

    private int lastIndex = 0;

    public Playlist(String id) {
        this.id = id;
        playlistList = new ArrayList<PlaylistElement>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public List<PlaylistElement> getPlaylistList() {
        return playlistList;
    }

    /**
     * Increment index position and go to the next element in this playlist
     * @return  true if the playlist finished and the next element is the first,
     *          false if there is a next element in the playlist
     */
    public boolean nextIndex() {
        if(++lastIndex >= playlistList.size()) {
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
     * @return  the playlist entry on the current position
     */
    public PlaylistElement getCurrentElement() {
        return playlistList.get(lastIndex);
    }

}
