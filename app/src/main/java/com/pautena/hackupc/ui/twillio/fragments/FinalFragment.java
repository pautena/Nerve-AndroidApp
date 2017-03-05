package com.pautena.hackupc.ui.twillio.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.pautena.hackupc.R;
import com.pautena.hackupc.entities.VideoRoom;

import im.delight.android.ddp.Meteor;
import im.delight.android.ddp.MeteorSingleton;
import io.realm.Realm;

/**
 * Created by pautenavidal on 4/3/17.
 */

public class FinalFragment extends Fragment {
    public static final String TAG = FinalFragment.class.getSimpleName();

    public interface FinalFragmentCallback {
        void restartActivity();
    }

    private static FinalFragmentCallback emptyCalblack = new FinalFragmentCallback() {

        @Override
        public void restartActivity() {

        }
    };

    private FinalFragmentCallback callback = emptyCalblack;
    private static final String ARG_VIDEO_ROOM_ID = "argVideoRoomId";
    private static final String ARG_MASTER = "argMaster";

    public static FinalFragment newInstance(VideoRoom room, boolean master) {

        Bundle args = new Bundle();
        args.putString(ARG_VIDEO_ROOM_ID, room.getId());
        args.putBoolean(ARG_MASTER, master);


        FinalFragment fragment = new FinalFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callback = (FinalFragmentCallback) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = emptyCalblack;
    }

    private Button startButton;
    private TextView titleTextView;
    private VideoRoom videoRoom;
    private Realm realm;
    private Meteor mMeteor;
    private boolean master;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.request_fragment, container, false);

        mMeteor = MeteorSingleton.getInstance();
        realm = Realm.getDefaultInstance();

        startButton = (Button) view.findViewById(R.id.start_button);
        titleTextView = (TextView) view.findViewById(R.id.title);

        videoRoom = realm.where(VideoRoom.class)
                .equalTo("id", getArguments().getString(ARG_VIDEO_ROOM_ID)).findFirst();
        master = getArguments().getBoolean(ARG_MASTER);

        setVideoRoom(videoRoom, master);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.restartActivity();
            }
        });


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realm.close();
    }

    public void setVideoRoom(VideoRoom videoRoom, boolean master) {

        String message;
        if (master && videoRoom.getVote() < 0) {
            message = getResources().getString(R.string.winner_finish_message, videoRoom.getSinger2Username());
        } else if (!master && videoRoom.getVote() > 0) {
            message = getResources().getString(R.string.winner_finish_message, videoRoom.getSinger1Username());
        } else {
            String username;
            if (master) {
                username = videoRoom.getSinger2Username();
            } else {
                username = videoRoom.getSinger1Username();

            }
            startButton.setText(R.string.try_again);
            message = getResources().getString(R.string.looser_finish_message, username);
        }
        Log.d(TAG, "setVideoRoom(" + master + "). videoRoom: " + videoRoom);

        String singer1Username = videoRoom.getSinger1Username();

        Log.d(TAG, "singer1: " + singer1Username);

        titleTextView.setText(message);


    }
}
