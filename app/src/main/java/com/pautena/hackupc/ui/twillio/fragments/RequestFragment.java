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
import com.pautena.hackupc.entities.RequestUser;
import com.pautena.hackupc.entities.Song;
import com.pautena.hackupc.entities.VideoRoom;
import com.twilio.video.Participant;
import com.twilio.video.Room;

import im.delight.android.ddp.Meteor;
import im.delight.android.ddp.MeteorSingleton;
import im.delight.android.ddp.db.Document;
import io.realm.Realm;

/**
 * Created by pautenavidal on 4/3/17.
 */

public class RequestFragment extends Fragment {
    public static final String TAG = RequestFragment.class.getSimpleName();

    public interface RequestFragmentCallback {
        void onAcceptRequest(VideoRoom videoRoom);
    }

    private static RequestFragmentCallback emptyCalblack = new RequestFragmentCallback() {
        @Override
        public void onAcceptRequest(VideoRoom videoRoom) {

        }
    };

    private RequestFragmentCallback callback = emptyCalblack;
    private static final String ARG_VIDEO_ROOM_ID = "argVideoRoomId";

    public static RequestFragment newInstance(VideoRoom room) {

        Bundle args = new Bundle();
        args.putString(ARG_VIDEO_ROOM_ID, room.getId());


        RequestFragment fragment = new RequestFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callback = (RequestFragmentCallback) context;
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

        setVideoRoom(videoRoom);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onAcceptRequest(videoRoom);
            }
        });


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realm.close();
    }

    public void setVideoRoom(VideoRoom videoRoom) {
        Log.d(TAG, "setHaveParticipant. videoRoom: " + videoRoom);

        String singer1Id = videoRoom.getSinger1Id();

        RequestUser singer1 = realm.where(RequestUser.class).equalTo("id", singer1Id).findFirst();

        if (singer1 != null) {

            Log.d(TAG, "singer1: " + singer1);

            String title = getResources()
                    .getString(R.string.new_request, singer1.getUsername(), videoRoom.getSong().getTitle());

            titleTextView.setText(title);
        } else {
            Log.e(TAG, "singer1 is null");
        }
    }
}
