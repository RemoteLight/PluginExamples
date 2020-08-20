package de.lars.effectplaylist;

import java.io.Serializable;

public class PlaylistElement implements Serializable {

    public enum Type {
        Animation, MusicEffect
    }

    private Type type;
    private String name;
    private int duration;

    public PlaylistElement(Type type, String name, int duration) {
        this.type = type;
        this.name = name;
        this.duration = duration;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
