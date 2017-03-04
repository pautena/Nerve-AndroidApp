package com.pautena.hackupc.entities;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by pautenavidal on 4/3/17.
 */

public class VideoRoom extends RealmObject{

    @PrimaryKey
    private String id;

    private Song song;

    private String singer1Id;
    private String singer2Id;

    public VideoRoom(){}

    public VideoRoom(String id, Song song, String singer1Id, String singer2Id) {
        this.id = id;
        this.song = song;
        this.singer1Id = singer1Id;
        this.singer2Id = singer2Id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public String getSinger1Id() {
        return singer1Id;
    }

    public void setSinger1Id(String singer1Id) {
        this.singer1Id = singer1Id;
    }

    public String getSinger2Id() {
        return singer2Id;
    }

    public void setSinger2Id(String singer2Id) {
        this.singer2Id = singer2Id;
    }
}
