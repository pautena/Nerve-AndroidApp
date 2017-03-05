package com.pautena.hackupc.entities;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import java.io.IOException;
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
    private String coverUrl;
    private String musicGenre;
    private int rating;


    public Song() {
    }


    public Song(String id, String title, String author, String audioAssetName,
                String lyricsAssetName, String coverUrl, String musicGenre, int rating) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.audioAssetName = audioAssetName;
        this.lyricsAssetName = lyricsAssetName;
        this.coverUrl = coverUrl;
        this.musicGenre = musicGenre;
        this.rating = rating;
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

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getMusicGenre() {
        return musicGenre;
    }

    public void setMusicGenre(String musicGenre) {
        this.musicGenre = musicGenre;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getDuration(Context context) {
        try {
            AssetFileDescriptor afd = context.getAssets().openFd(getAudioAssetName());

            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            int duration =
                    Integer.valueOf(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));

            mmr.release();

            int min = duration / 1000 / 60;
            int sec = (duration / 1000) - min * 60;


            return min + "min " + sec+"sec";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
