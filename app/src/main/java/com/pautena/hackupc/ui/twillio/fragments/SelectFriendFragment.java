package com.pautena.hackupc.ui.twillio.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pautena.hackupc.R;
import com.pautena.hackupc.entities.RequestUser;
import com.pautena.hackupc.services.ApiServiceAdapter;
import com.pautena.hackupc.services.callback.FinishGetFriendsCallback;
import com.pautena.hackupc.ui.customview.SimpleDividerItemDecoration;
import com.pautena.hackupc.ui.twillio.adapters.FriendSelectionAdapter;
import com.pautena.hackupc.ui.twillio.adapters.SongSelectionAdapter;

import java.util.List;

import io.realm.Realm;

/**
 * Created by pautenavidal on 4/3/17.
 */

public class SelectFriendFragment extends Fragment implements FriendSelectionAdapter.FriendSelectionAdapterListener {

    public static final String TAG = SelectFriendFragment.class.getSimpleName();

    public static SelectFriendFragment newInstance() {

        Bundle args = new Bundle();

        SelectFriendFragment fragment = new SelectFriendFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public interface SelectFriendFragmentCallback {
        void onSelectFriend(RequestUser user);
    }

    private static SelectFriendFragmentCallback emptyCallback = new SelectFriendFragmentCallback() {
        @Override
        public void onSelectFriend(RequestUser user) {

        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callback = (SelectFriendFragmentCallback) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = emptyCallback;
    }

    private SelectFriendFragmentCallback callback = emptyCallback;
    private Realm realm;
    private RecyclerView recyclerView;
    private Toolbar mToolbar;
    private LinearLayoutManager mLayoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.select_friend_fragment, container, false);

        realm = Realm.getDefaultInstance();

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);


        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));

        ApiServiceAdapter.getInstance(getActivity()).getAllUsers(new FinishGetFriendsCallback() {
            @Override
            public void onFinishGetFriends() {
                recyclerView.setAdapter(FriendSelectionAdapter.newInstance(realm, SelectFriendFragment.this));
            }
        });


        mToolbar.setTitle(R.string.select_friend_title);
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
    public void onSelectItem(RequestUser requestUser) {
        callback.onSelectFriend(requestUser);
    }
}
