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
import com.twilio.video.Participant;

/**
 * Created by pautenavidal on 4/3/17.
 */

public class WaitForStartFragment extends Fragment {
    public static final String TAG = WaitForStartFragment.class.getSimpleName();

    public interface WaitForStartCallback {
        void onStartSing();
    }

    private static WaitForStartCallback emptyCalblack = new WaitForStartCallback() {
        @Override
        public void onStartSing() {

        }
    };

    private WaitForStartCallback callback = emptyCalblack;
    private static final String ARG_HAVE_PARTICIPANT = "argHaveParticipant";

    public static WaitForStartFragment newInstance(Participant participant) {

        Bundle args = new Bundle();
        args.putBoolean(ARG_HAVE_PARTICIPANT, participant != null);

        WaitForStartFragment fragment = new WaitForStartFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callback = (WaitForStartCallback) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = emptyCalblack;
    }

    private Button startButton;
    private TextView titleTextView;
    private boolean haveParticipant;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wait_for_start, container, false);

        startButton = (Button) view.findViewById(R.id.start_button);
        titleTextView = (TextView) view.findViewById(R.id.title);

        haveParticipant = getArguments().getBoolean(ARG_HAVE_PARTICIPANT);

        setHaveParticipant(haveParticipant);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onStartSing();
            }
        });


        return view;
    }

    public void setHaveParticipant(boolean haveParticipant) {
        Log.d(TAG, "setHaveParticipant. haveParticipant: " + haveParticipant);

        this.haveParticipant = haveParticipant;
        if (haveParticipant) {
            titleTextView.setText(R.string.wait_for_start);
        } else {
            titleTextView.setText(R.string.wait_for_participant);
        }

        //TODO: remove comment - >startButton.setEnabled(haveParticipant);
    }
}
