package com.soft.redix.fasttube.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.api.services.youtube.model.Playlist;
import com.soft.redix.fasttube.R;
import com.soft.redix.fasttube.activity.MainActivity;
import com.soft.redix.fasttube.fragment.PlaylistItemsFragment;
import com.soft.redix.fasttube.util.NavigationUtil;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by Gonzalo on 25/3/2016.
 */
public class PlaylistAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements ItemClickListener {

    private final Context context;
    private List<Playlist> items;

    public static class SimpleViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        // Campos respectivos de un item
        public ImageView thumbnail;
        public TextView title, videoCount, videos, aux;
        public ItemClickListener listener;
        public CardView cardView;

        public SimpleViewHolder(View v, ItemClickListener listener) {
            super(v);
            cardView = (CardView) v.findViewById(R.id.playlistCard);
            cardView.setPreventCornerOverlap(false);
            thumbnail = (ImageView) v.findViewById(R.id.playlistThumbnail);
            title = (TextView) v.findViewById(R.id.playlistTitle);
            videoCount = (TextView) v.findViewById(R.id.playlistVideoCount);
            videos = (TextView) v.findViewById(R.id.playlistVideos);
            aux = (TextView) v.findViewById(R.id.aux);
            aux.setOnClickListener(this);
            this.listener = listener;
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(v, getAdapterPosition());
        }
    }

    public PlaylistAdapter(Context context, List<Playlist> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) != null ? ViewType.ITEM : ViewType.PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        if(viewType == ViewType.ITEM) {
            v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.list_playlists, viewGroup, false);
            return new SimpleViewHolder(v, this);
        }
        else{
            v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.list_progress, viewGroup, false);
            return new ProgressViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if(viewHolder instanceof SimpleViewHolder) {
            SimpleViewHolder holder = (SimpleViewHolder) viewHolder;
            Playlist playlist = items.get(i);
            holder.title.setText(playlist.getSnippet().getTitle());
            holder.videoCount.setText(playlist.getContentDetails().getItemCount().toString() + " videos");
            holder.videos.setText(playlist.getContentDetails().getItemCount().toString());
            Picasso.get()
                    .load(playlist.getSnippet().getThumbnails().getMedium().getUrl())
                    .fit().centerCrop()
                    .into(holder.thumbnail);
        }
        else{
            ProgressViewHolder holder = (ProgressViewHolder) viewHolder;
            holder.progress.show();
        }
    }

    /**
     * Sobrescritura del método de la interfaz {@link ItemClickListener}
     *
     * @param view     item actual
     * @param position posición del item actual
     */
    @Override
    public void onItemClick(View view, final int position) {
        final Playlist playlist = items.get(position);
        AppCompatActivity mActivity = (AppCompatActivity) context;

        FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
        Fragment newFragment = new PlaylistItemsFragment();
        Bundle args = new Bundle();
        args.putString("playlist_id", playlist.getId());
        args.putString("playlist_channel_title", playlist.getSnippet().getChannelTitle());
        args.putString("playlist_title", playlist.getSnippet().getTitle());
        args.putString("video_count", playlist.getContentDetails().getItemCount().toString());
        args.putString("playlist_thumb", playlist.getSnippet().getThumbnails().getMedium().getUrl());
        newFragment.setArguments(args);

        ViewPager mPager = (ViewPager) mActivity.findViewById(R.id.pager);
        mPager.setVisibility(View.GONE);

        /*TabLayout tabLayout = (TabLayout) mActivity.findViewById(R.id.tab_layout);
        tabLayout.animate().scaleY(0).setInterpolator(new AccelerateInterpolator()).start();
        tabLayout.setVisibility(View.GONE);*/
        ActionBar actionBar = mActivity.getSupportActionBar();
        if (actionBar != null)
            mActivity.getSupportActionBar().setTitle(playlist.getSnippet().getTitle());

        String id = playlist.getId() + "," +
                    playlist.getSnippet().getChannelTitle() + "," +
                    playlist.getSnippet().getTitle() + "," +
                    playlist.getContentDetails().getItemCount().toString() + "," +
                    playlist.getSnippet().getThumbnails().getHigh().getUrl();
    }

}
