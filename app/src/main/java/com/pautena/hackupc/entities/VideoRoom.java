package com.pautena.hackupc.entities;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by pautenavidal on 4/3/17.
 */

public class VideoRoom extends RealmObject {

    @PrimaryKey
    private String id;

    private Song song;

    private String singer1Username;
    private String singer2Username;

    private int vote;
    private int viewers;
    private int nVotes;

    public VideoRoom() {
    }

    public VideoRoom(String id, Song song, String singer1Username, String singer2Username) {
        this.id = id;
        this.song = song;
        this.singer1Username = singer1Username;
        this.singer2Username = singer2Username;
        this.vote = 0;
        this.viewers = 0;
        this.nVotes=0;
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

    public String getSinger1Username() {
        return singer1Username;
    }

    public void setSinger1Username(String singer1Username) {
        this.singer1Username = singer1Username;
    }

    public String getSinger2Username() {
        return singer2Username;
    }

    public void setSinger2Username(String singer2Username) {
        this.singer2Username = singer2Username;
    }

    public int getVote() {
        return vote;
    }

    public void setVote(int vote) {
        this.vote = vote;
    }

    public int getViewers() {
        return viewers;
    }

    public void setViewers(int viewers) {
        this.viewers = viewers;
    }

    public int getnVotes() {
        return nVotes;
    }

    public void setnVotes(int nVotes) {
        this.nVotes = nVotes;
    }
}
