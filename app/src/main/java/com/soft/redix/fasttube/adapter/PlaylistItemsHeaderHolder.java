package com.soft.redix.fasttube.adapter;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.soft.redix.fasttube.R;

/**
 * Created by gonzalo on 19/10/17.
 */

public class PlaylistItemsHeaderHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView title, channelTitle, videoCount;
    public ImageView info, share;
    public FloatingActionButton fab;
    public ItemClickListener listener;

    public PlaylistItemsHeaderHolder(View v, ItemClickListener listener) {
        super(v);
        this.listener = listener;
        title = (TextView) v.findViewById(R.id.playlistItemsTitle);
        channelTitle = (TextView) v.findViewById(R.id.playlistItemsChannelTitle);
        videoCount = (TextView) v.findViewById(R.id.playlistItemsVideoCount);
        info = (ImageView) v.findViewById(R.id.playlistItemsInfo);
        share = (ImageView) v.findViewById(R.id.playlistItemsShare);
        fab = (FloatingActionButton) v.findViewById(R.id.playlistItemsFab);
        info.setOnClickListener(this);
        share.setOnClickListener(this);
        fab.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        listener.onItemClick(v, getAdapterPosition());
    }
}
