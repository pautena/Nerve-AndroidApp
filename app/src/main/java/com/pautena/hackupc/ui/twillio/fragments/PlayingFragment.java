package com.pautena.hackupc.ui.twillio.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pautena.hackupc.R;
import com.pautena.hackupc.ui.twillio.activity.VideoActivity;

/**
 * Created by pautenavidal on 4/3/17.
 */

public class PlayingFragment extends Fragment {
    public static final String TAG = PlayingFragment.class.getSimpleName();

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

    public static PlayingFragment newInstance() {

        Bundle args = new Bundle();

        PlayingFragment fragment = new PlayingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private PlayingFragmentCallback callback = emptyCallback;

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


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.playing_fragment, container, false);


        switchCameraActionFab = (FloatingActionButton) view.findViewById(R.id.switch_camera_action_fab);
        localVideoActionFab = (FloatingActionButton) view.findViewById(R.id.local_video_action_fab);
        muteActionFab = (FloatingActionButton) view.findViewById(R.id.mute_action_fab);


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
}
