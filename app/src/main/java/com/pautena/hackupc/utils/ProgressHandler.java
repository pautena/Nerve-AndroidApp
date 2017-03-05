package com.pautena.hackupc.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;

import com.pautena.hackupc.R;
import com.pautena.hackupc.entities.Song;
import com.pautena.hackupc.ui.twillio.customviews.MyPrimaryVideoView;
import com.pautena.hackupc.utils.lrc.Lrc;
import com.pautena.hackupc.utils.lrc.LrcLoader;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pautenavidal on 4/3/17.
 */

public class ProgressHandler {
    private static final String TAG=ProgressHandler.class.getSimpleName();

    private final MyPrimaryVideoView primaryVideoView;
    private final Context context;
    private final MediaPlayer mediaPlayer;
    private final Song song;
    private final SongPlayer.SongPlayerListener listener;
    private Lrc lrc;
    private int step;
    private Time time;

    public ProgressHandler(Context context, Song song, MediaPlayer mediaPlayer, MyPrimaryVideoView primaryVideoView, SongPlayer.SongPlayerListener listener) {
        this.primaryVideoView = primaryVideoView;
        this.context = context;
        this.mediaPlayer = mediaPlayer;
        this.song = song;
        step = context.getResources().getInteger(R.integer.progress_step_lyrics);
        this.listener = listener;

        lrc = LrcLoader.load(context, song.getLyricsAssetName());
        time = new Time(0);

    }

    public void start() {
        final Handler handler = new Handler();


        handler.post(new Runnable() {
            @Override
            public void run() {

                List<String> lyricLines = lrc.getPart(time).getLines();
                Log.d(TAG,"lyricLines: "+lyricLines);
                primaryVideoView.setLyricsLines(lyricLines);

                Log.d(TAG,"isPlaying: "+mediaPlayer.isPlaying());
                if (mediaPlayer.isPlaying()) {
                    handler.postDelayed(this, step);
                    time.setTime(time.getTime() + step);
                }else{
                    listener.onFinishSong();
                    primaryVideoView.setLyricsLines(new ArrayList<String>());
                }

            }
        });
    }

}