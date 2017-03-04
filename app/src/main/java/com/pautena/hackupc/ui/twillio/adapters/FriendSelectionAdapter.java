package com.pautena.hackupc.ui.twillio.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pautena.hackupc.R;
import com.pautena.hackupc.entities.RequestUser;
import com.pautena.hackupc.entities.Song;
import com.pautena.hackupc.entities.User;
import com.pautena.hackupc.entities.manager.UserManager;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

/**
 * Created by pautenavidal on 4/3/17.
 */

public class FriendSelectionAdapter extends RealmRecyclerViewAdapter<RequestUser, FriendSelectionAdapter.ViewHolder> {
    private static final String TAG = FriendSelectionAdapter.class.getSimpleName();

    public interface FriendSelectionAdapterListener {
        void onSelectItem(RequestUser requestUser);
    }

    public static FriendSelectionAdapter newInstance(Context context, Realm realm, FriendSelectionAdapterListener listener) {

        User mainUser = UserManager.getInstance(context).getMainUser(realm);

        RealmResults<RequestUser> requestUsers = realm
                .where(RequestUser.class)
                .notEqualTo("id", mainUser.getId())
                .findAll();

        return new FriendSelectionAdapter(requestUsers, true, listener);
    }

    private FriendSelectionAdapterListener listener;

    private FriendSelectionAdapter(@Nullable OrderedRealmCollection data, boolean autoUpdate, FriendSelectionAdapterListener listener) {
        super(data, autoUpdate);
        this.listener = listener;

        Log.d(TAG, "data: " + data);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_selection_item, parent, false);
        return new FriendSelectionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final RequestUser song = getItem(position);

        holder.tvUsername.setText(song.getUsername());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSelectItem(song);
            }
        });

    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvUsername;
        public View itemView;

        public ViewHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;

            tvUsername = (TextView) itemView.findViewById(R.id.tv_username);
        }
    }
}
