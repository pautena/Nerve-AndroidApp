package com.pautena.hackupc.ui.twillio.adapters;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    private static final String TAG=SongSelectionAdapter.class.getSimpleName();

    public interface SongSelectionAdapterListener {
        void onSelectItem(Song song);
    }

    public static SongSelectionAdapter newInstance(Realm realm, SongSelectionAdapterListener listener) {

        RealmResults<Song> songs = realm.where(Song.class).findAll();

        return new SongSelectionAdapter(songs, true, listener);

    }

    private SongSelectionAdapterListener listener;

    private SongSelectionAdapter(@Nullable OrderedRealmCollection data, boolean autoUpdate, SongSelectionAdapterListener listener) {
        super(data, autoUpdate);
        this.listener = listener;

        Log.d(TAG,"data: "+data);
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
        public View itemView;

        public ViewHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;

            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvAuthor = (TextView) itemView.findViewById(R.id.tv_author);
        }
    }
}
