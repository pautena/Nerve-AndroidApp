package com.pautena.hackupc.ui.twillio.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.pautena.hackupc.R;

/**
 * Created by pautenavidal on 4/3/17.
 */

public class StartOrJoinFragment extends Fragment {
    public static final String TAG=StartOrJoinFragment.class.getSimpleName();

    public interface StartOrJoinFragmentCallback{
        void onStartServer();
    }

    private static StartOrJoinFragmentCallback emptyCalblack = new StartOrJoinFragmentCallback() {
        @Override
        public void onStartServer() {

        }
    };

    private StartOrJoinFragmentCallback callback = emptyCalblack;

    public static StartOrJoinFragment newInstance() {

        Bundle args = new Bundle();

        StartOrJoinFragment fragment = new StartOrJoinFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callback = (StartOrJoinFragmentCallback) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = emptyCalblack;
    }

    private Button startButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.start_or_join_fragment,container,false);

        startButton = (Button) view.findViewById(R.id.start_button);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onStartServer();
            }
        });



        return  view;
    }
}
