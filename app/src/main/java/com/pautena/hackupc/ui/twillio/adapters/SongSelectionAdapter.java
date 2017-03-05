package com.pautena.hackupc.ui.twillio.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.pautena.hackupc.R;
import com.pautena.hackupc.entities.Song;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

/**
 * Created by pautenavidal on 4/3/17.
 */

public class SongSelectionAdapter extends RealmRecyclerViewAdapter<Song, SongSelectionAdapter.ViewHolder> {
    private static final String TAG = SongSelectionAdapter.class.getSimpleName();
    private final Context context;

    public interface SongSelectionAdapterListener {
        void onSelectItem(Song song);
    }

    public static SongSelectionAdapter newInstance(Context context, Realm realm, SongSelectionAdapterListener listener) {

        RealmResults<Song> songs = realm.where(Song.class).findAllSorted("title");

        return new SongSelectionAdapter(context, songs, true, listener);

    }

    private SongSelectionAdapterListener listener;

    private SongSelectionAdapter(Context context, @Nullable OrderedRealmCollection data, boolean autoUpdate, SongSelectionAdapterListener listener) {
        super(data, autoUpdate);
        this.listener = listener;
        this.context = context;

        Log.d(TAG, "data: " + data);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_selection_item, parent, false);
        return new SongSelectionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Song song = getItem(position);

        holder.tvTitle.setText(song.getTitle());
        holder.tvAuthor.setText(song.getAuthor());

        int progress = song.getRating() / 10;
        Log.d(TAG, "progress: " + progress);
        holder.rating.setProgress(progress);


        Uri uri = Uri.parse(song.getCoverUrl());
        Log.d(TAG, "load uri: " + uri);
        Glide.with(context).load(uri).listener(new RequestListener<Uri, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                Log.e(TAG, "error on load img");
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                Log.d(TAG, "resource loaded");
                return false;
            }
        }).into(holder.ivIcon);


        holder.tvDuration.setText(song.getDuration(context));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSelectItem(song);
            }
        });

    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle;
        public TextView tvAuthor;
        public TextView tvDuration;
        public ImageView ivIcon;
        public RatingBar rating;
        public View itemView;

        public ViewHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;

            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvAuthor = (TextView) itemView.findViewById(R.id.tv_author);
            tvDuration = (TextView) itemView.findViewById(R.id.tv_duration);
            ivIcon = (ImageView) itemView.findViewById(R.id.icon);
            rating = (RatingBar) itemView.findViewById(R.id.rating);
        }
    }
}
