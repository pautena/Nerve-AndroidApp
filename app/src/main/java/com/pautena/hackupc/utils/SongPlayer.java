package com.pautena.hackupc.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;

import com.pautena.hackupc.R;
import com.pautena.hackupc.entities.Song;
import com.pautena.hackupc.ui.twillio.customviews.MyPrimaryVideoView;
import com.pautena.hackupc.utils.lrc.Lrc;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by pautenavidal on 4/3/17.
 */

public class SongPlayer {
    public interface SongPlayerListener{
        void onFinishSong();
    }
    private static final String TAG = SongPlayer.class.getSimpleName();
    private final MyPrimaryVideoView primaryVideoView;
    private Context context;
    private Song song;
    private MediaPlayer player;
    private int delay;
    private int step;

    private SongPlayerListener listener= new SongPlayerListener() {
        @Override
        public void onFinishSong() {

        }
    };


    public SongPlayer(Context context, MyPrimaryVideoView primaryVideoView) {
        this.context = context;
        this.primaryVideoView = primaryVideoView;
        delay = context.getResources().getInteger(R.integer.play_song_delay);
        step = context.getResources().getInteger(R.integer.play_song_step);
    }

    public void setListener(SongPlayerListener listener) {
        this.listener = listener;
    }

    public void play(Song song) {
        stopSong();
        this.song = song;
        Log.d(TAG, "play song: " + song);

        playWithDelay(delay, step);
    }

    private void playWithDelay(final int delay, final int step) {

        final Handler handler = new Handler();
        final int[] currentDelay = {0};

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (currentDelay[0] < delay) {
                    currentDelay[0] += step;
                    handler.postDelayed(this, step);
                    primaryVideoView.delayToStart(delay - currentDelay[0]);
                } else {
                    try {
                        primaryVideoView.delayToStart(-1);
                        AssetFileDescriptor afd = context.getAssets().openFd(song.getAudioAssetName());
                        player = new MediaPlayer();
                        player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                        player.prepare();
                        player.start();


                        new ProgressHandler(context,song,player,primaryVideoView,listener).start();
                    } catch (IOException e) {
                    }
                }

            }
        }, step);

    }

    public void stopSong() {
        if (song != null) {

        }
    }
}
