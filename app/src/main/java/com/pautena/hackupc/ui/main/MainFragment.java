package com.pautena.hackupc.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pautena.hackupc.R;

/**
 * Created by pautenavidal on 3/3/17.
 */

public class MainFragment extends Fragment {

    public interface MainFragmentCallback{
        void onStartCall();
    }

    private static MainFragmentCallback emptyCallback = new MainFragmentCallback() {
        @Override
        public void onStartCall() {

        }
    };

    public static MainFragment newInstance() {

        Bundle args = new Bundle();

        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }


    private FloatingActionButton fabStartCall;
    private MainFragmentCallback callback= emptyCallback;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callback = (MainFragmentCallback) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = emptyCallback;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment,container,false);


        fabStartCall = (FloatingActionButton) view.findViewById(R.id.fab_start_call);


        fabStartCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onStartCall();
            }
        });

        return  view;
    }
}
