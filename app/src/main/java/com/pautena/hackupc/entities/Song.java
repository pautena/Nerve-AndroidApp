package com.pautena.hackupc.entities;

import java.sql.Time;
import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by pautenavidal on 3/3/17.
 */

public class Song extends RealmObject {

    @PrimaryKey
    private String id;

    private String title;
    private String author;
    private String audioAssetName;
    private String lyricsAssetName;


    public Song() {
    }


    public Song(String id, String title, String author, String audioAssetName, String lyricsAssetName) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.audioAssetName = audioAssetName;
        this.lyricsAssetName = lyricsAssetName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAudioAssetName() {
        return audioAssetName;
    }

    public void setAudioAssetName(String audioAssetName) {
        this.audioAssetName = audioAssetName;
    }

    public String getLyricsAssetName() {
        return lyricsAssetName;
    }

    public void setLyricsAssetName(String lyricsAssetName) {
        this.lyricsAssetName = lyricsAssetName;
    }
}
