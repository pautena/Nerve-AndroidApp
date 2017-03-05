package com.pautena.hackupc.ui.twillio.fragments;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.pautena.hackupc.R;
import com.pautena.hackupc.entities.VideoRoom;
import com.pautena.hackupc.ui.twillio.activity.VideoActivity;

import io.realm.Realm;

/**
 * Created by pautenavidal on 4/3/17.
 */

public class PlayingFragment extends Fragment {
    public static final String TAG = PlayingFragment.class.getSimpleName();
    private AudioManager audioManager;

    public interface PlayingFragmentCallback {
        void onSwitchCamera();

        boolean localVideo();

        boolean muteAction();
    }

    private static PlayingFragmentCallback emptyCallback = new PlayingFragmentCallback() {
        @Override
        public void onSwitchCamera() {

        }

        @Override
        public boolean localVideo() {
            return true;
        }

        @Override
        public boolean muteAction() {
            return true;
        }
    };

    private static final String ARG_VIDEO_ROOM_ID = "argVideoRoomId";

    public static PlayingFragment newInstance(VideoRoom videoRoom) {

        Bundle args = new Bundle();
        args.putString(ARG_VIDEO_ROOM_ID, videoRoom.getId());

        PlayingFragment fragment = new PlayingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private PlayingFragmentCallback callback = emptyCallback;
    private Realm realm;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callback = (PlayingFragmentCallback) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = emptyCallback;
    }

    private FloatingActionButton switchCameraActionFab;
    private FloatingActionButton localVideoActionFab;
    private FloatingActionButton muteActionFab;
    private SeekBar volumeSeekBar;
    private ImageButton ibVolume;

    private TextView tvVisitors;
    private VideoRoom videoRoom;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.playing_fragment, container, false);
        realm = Realm.getDefaultInstance();

        videoRoom = realm.where(VideoRoom.class)
                .equalTo("id", getArguments().getString(ARG_VIDEO_ROOM_ID)).findFirst();


        switchCameraActionFab = (FloatingActionButton) view.findViewById(R.id.switch_camera_action_fab);
        localVideoActionFab = (FloatingActionButton) view.findViewById(R.id.local_video_action_fab);
        muteActionFab = (FloatingActionButton) view.findViewById(R.id.mute_action_fab);
        ibVolume = (ImageButton) view.findViewById(R.id.volume_icon);

        tvVisitors = (TextView) view.findViewById(R.id.visitors);
        setViewers(videoRoom.getViewers());


        initializeAudioSeekBar(view);


        switchCameraActionFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onSwitchCamera();
            }
        });

        localVideoActionFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean showSwitchButton = callback.localVideo();

                int icon;
                if (showSwitchButton) {
                    switchCameraActionFab.show();
                    icon = R.drawable.ic_videocam_green_24px;
                } else {
                    icon = R.drawable.ic_videocam_off_red_24px;
                    switchCameraActionFab.hide();
                }


                localVideoActionFab.setImageDrawable(
                        ContextCompat.getDrawable(getActivity(), icon));
            }
        });

        muteActionFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean enable = callback.muteAction();


                int icon = enable ?
                        R.drawable.ic_mic_green_24px : R.drawable.ic_mic_off_red_24px;
                muteActionFab.setImageDrawable(ContextCompat.getDrawable(
                        getActivity(), icon));
            }
        });


        return view;
    }

    private void initializeAudioSeekBar(View view) {
        volumeSeekBar = (SeekBar) view.findViewById(R.id.volume_seekBar);
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        volumeSeekBar.setMax(audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC));

        final int volume = audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC);
        volumeSeekBar.setProgress(volume);
        setVolumeIcon(volume);

        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        progress, 0);

                setVolumeIcon(progress);
            }
        });

        ibVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volumeSeekBar.setEnabled(volumeSeekBar.isEnabled());
                setVolumeIcon(volumeSeekBar.getProgress());

                if (!volumeSeekBar.isEnabled()) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                } else {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volumeSeekBar.getProgress(), 0);
                }
            }
        });
    }

    private void setVolumeIcon(int volume) {
        boolean isSilent = !volumeSeekBar.isEnabled();

        int resId;
        if (isSilent) {
            resId = R.drawable.ic_volume_mute_black_24px;
        } else if (volume == 0) {
            resId = R.drawable.ic_volume_off_black_24px;
        } else if (volume < 50) {
            resId = R.drawable.ic_volume_down_black_24px;
        } else {
            resId = R.drawable.ic_volume_up_black_24px;
        }

        ibVolume.setImageResource(resId);

    }

    public void setViewers(int viewers) {
        if (tvVisitors != null) {
            tvVisitors.setText(String.valueOf(viewers));
        }
    }
}
