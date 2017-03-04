package com.pautena.hackupc.utils;

import com.pautena.hackupc.entities.Song;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * Created by pautenavidal on 3/3/17.
 */

public class SongLoader {


    public void load(Realm realm) {

        List<Song> songs = new ArrayList<>();


        Song song = new Song("37209268",
                "Peter Pan",
                "El canto del loco",
                "peter_pan.mp3",
                "peter_pan_ecdl.lrc");
        songs.add(song);

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(songs);
        realm.commitTransaction();
    }
}
