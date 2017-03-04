package com.pautena.hackupc.ui.twillio.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pautena.hackupc.R;
import com.pautena.hackupc.entities.Song;
import com.pautena.hackupc.ui.customview.SimpleDividerItemDecoration;
import com.pautena.hackupc.ui.twillio.adapters.SongSelectionAdapter;

import io.realm.Realm;

/**
 * Created by pautenavidal on 3/3/17.
 */

public class SongSelectionFragment extends Fragment implements SongSelectionAdapter.SongSelectionAdapterListener {

    private LinearLayoutManager mLayoutManager;

    public interface SongSelectionCallback{
        void onSelectSong(Song song);

    }

    private static SongSelectionCallback emptyCallback = new SongSelectionCallback() {

        @Override
        public void onSelectSong(Song song) {

        }
    };

    public static final String TAG = SongSelectionFragment.class.getSimpleName();

    public static SongSelectionFragment newInstance() {

        Bundle args = new Bundle();

        SongSelectionFragment fragment = new SongSelectionFragment();
        fragment.setArguments(args);
        return fragment;
    }


    private RecyclerView recyclerView;
    private Toolbar mToolbar;
    private SongSelectionCallback callback = emptyCallback;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callback = (SongSelectionCallback) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback =emptyCallback;
    }

    private Realm realm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.song_selection_fragment, container, false);

        realm =Realm.getDefaultInstance();

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);


        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(SongSelectionAdapter.newInstance(realm,this));
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));


        mToolbar.setTitle(R.string.select_song_name);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);


        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });


        return view;
    }

    @Override
    public void onSelectItem(Song song) {
        callback.onSelectSong(song);
    }
}
