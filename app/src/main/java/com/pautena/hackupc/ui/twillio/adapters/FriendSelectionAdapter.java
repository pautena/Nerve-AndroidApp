package com.pautena.hackupc.ui.twillio.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pautena.hackupc.R;
import com.pautena.hackupc.entities.RequestUser;
import com.pautena.hackupc.entities.Song;
import com.pautena.hackupc.entities.User;
import com.pautena.hackupc.entities.manager.UserManager;
import com.squareup.picasso.Picasso;

import java.util.Random;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

/**
 * Created by pautenavidal on 4/3/17.
 */

public class FriendSelectionAdapter extends RealmRecyclerViewAdapter<RequestUser, FriendSelectionAdapter.ViewHolder> {
    private static final String TAG = FriendSelectionAdapter.class.getSimpleName();
    private final Context context;


    public interface FriendSelectionAdapterListener {
        void onSelectItem(RequestUser requestUser);
    }

    public static FriendSelectionAdapter newInstance(Context context, Realm realm, FriendSelectionAdapterListener listener) {

        User mainUser = UserManager.getInstance(context).getMainUser(realm);

        RealmResults<RequestUser> requestUsers = realm
                .where(RequestUser.class)
                .notEqualTo("id", mainUser.getId())
                .findAll();

        return new FriendSelectionAdapter(context, requestUsers, true, listener);
    }

    private FriendSelectionAdapterListener listener;
    private int[] profileImagesResId = new int[]{R.drawable.ic_profile1, R.drawable.ic_profile2,
            R.drawable.ic_profile3, R.drawable.ic_profile4};

    private FriendSelectionAdapter(Context context, @Nullable OrderedRealmCollection data, boolean autoUpdate, FriendSelectionAdapterListener listener) {
        super(data, autoUpdate);
        this.listener = listener;
        this.context = context;

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
        final RequestUser requestUser = getItem(position);

        holder.tvUsername.setText(requestUser.getUsername());

        Random random = new Random();
        int value = random.nextInt(90) + 10;
        String msg = context.getResources().getString(R.string.num_songs, value);
        holder.tvInfo.setText(msg);

        Picasso.with(context).load(profileImagesResId[position % 4]).into(holder.ivProfileImage);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSelectItem(requestUser);
            }
        });

    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvUsername, tvInfo;
        public View itemView;
        public ImageView ivProfileImage;

        public ViewHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;

            tvUsername = (TextView) itemView.findViewById(R.id.tv_username);
            ivProfileImage = (ImageView) itemView.findViewById(R.id.user_profile_image);
            tvInfo = (TextView) itemView.findViewById(R.id.tv_number);
        }
    }
}
