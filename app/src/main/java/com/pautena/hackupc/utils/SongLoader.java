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
                "peter_pan_ecdl.lrc",
                "http://www.chartsinfrance.net/covers/aHR0cHM6Ly9pLnNjZG4uY28vaW1hZ2UvODNiNGE1NTU5ODZlMDM0YmI4YTAxMDFlNDYxMmM0ZWY3NDFhYjk0Nw==.jpg",
                "Country",
                74);
        songs.add(song);

        song = new Song("59668822",
                "Viva la vida",
                "Coldplay",
                "viva_la_vida.mp3",
                "viva_la_vida_ecdl.lrc",
                "http://cfs9.tistory.com/image/25/tistory/2008/08/12/17/05/48a1444fbda4c",
                "Pop",
                80);
        songs.add(song);

        song = new Song("71178111",
                "Adrenalina",
                "Wisin feat. Jennifer Lopez & Ricky Martin",
                "adrenalina.mp3",
                "adrenalina_ecdl.lrc",
                "https://upload.wikimedia.org/wikipedia/en/f/f5/Wisin_-_Adrenalina.png",
                "Urban Latin",
                81);
        songs.add(song);

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(songs);
        realm.commitTransaction();
    }
}
