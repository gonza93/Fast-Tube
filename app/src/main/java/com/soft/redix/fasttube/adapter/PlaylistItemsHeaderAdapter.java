package com.soft.redix.fasttube.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.Video;
import com.soft.redix.fasttube.R;
import com.soft.redix.fasttube.activity.MainActivity;
import com.soft.redix.fasttube.util.PlaylistDialog;
import com.soft.redix.fasttube.util.YouTubeService;

import java.util.List;

import su.j2e.rvjoiner.JoinableAdapter;
import su.j2e.rvjoiner.RvJoiner;

/**
 * Created by Gonzalo on 25/3/2016.
 */
public class PlaylistItemsHeaderAdapter extends RecyclerView.Adapter<PlaylistItemsHeaderAdapter.SimpleViewHolder>
        implements ItemClickListener {

    private final Context context;
    private String videoCount, channelTitle, playlistTitle, playlistThumb, playlistId;
    private Video firstVideo;
    private RecyclerView recyclerVideoInfo;

    public static class SimpleViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        // Campos respectivos de un item
        public TextView title, channelTitle, videoCount;
        public ImageView info, share;
        public FloatingActionButton fab;
        public ItemClickListener listener;
        public CardView cardView;

        public SimpleViewHolder(View v, ItemClickListener listener) {
            super(v);
            title = (TextView) v.findViewById(R.id.playlistItemsTitle);
            channelTitle = (TextView) v.findViewById(R.id.playlistItemsChannelTitle);
            videoCount = (TextView) v.findViewById(R.id.playlistItemsVideoCount);
            info = (ImageView) v.findViewById(R.id.playlistItemsInfo);
            share = (ImageView) v.findViewById(R.id.playlistItemsShare);
            fab = (FloatingActionButton) v.findViewById(R.id.playlistItemsFab);
            this.listener = listener;
            info.setOnClickListener(this);
            share.setOnClickListener(this);
            fab.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(v, getAdapterPosition());
        }
    }

    public PlaylistItemsHeaderAdapter(Context context, String videoCount, String playlistTitle,
                                      String channelTitle, String playlistThumb, String playlistId,
                                      Video firstVideo) {
        this.context = context;
        this.videoCount = videoCount;
        this.playlistTitle = playlistTitle;
        this.channelTitle = channelTitle;
        this.playlistThumb = playlistThumb;
        this.playlistId = playlistId;
        this.firstVideo = firstVideo;
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.header_playlist_items, viewGroup, false);
        return new SimpleViewHolder(v, this);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder viewHolder, int i) {
        viewHolder.videoCount.setText(videoCount + " videos");
        viewHolder.channelTitle.setText(channelTitle);
        viewHolder.title.setText(playlistTitle);
    }

    /**
     * Sobrescritura del método de la interfaz {@link ItemClickListener}
     *
     * @param view     item actual
     * @param position posición del item actual
     */
    @Override
    public void onItemClick(View view, final int position) {
        AppCompatActivity mActivity = (AppCompatActivity) context;
        if(view == mActivity.findViewById(R.id.playlistItemsInfo)) {
            PlaylistDialog infoDialog = new PlaylistDialog();
            Bundle args = new Bundle();
            args.putString("playlist_title", playlistTitle);
            args.putString("channel_title", channelTitle);
            args.putString("video_count", videoCount);
            args.putString("playlist_thumb", playlistThumb);
            infoDialog.setArguments(args);
            infoDialog.show(mActivity.getSupportFragmentManager(), "PlaylistDialog");
        }
        else{
            if(view == mActivity.findViewById(R.id.playlistItemsShare)){
                String url = "Shared with Fast Tube - https://www.youtube.com/playlist?list=" + playlistId;
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, url);
                shareIntent.setType("text/plain");
                mActivity.startActivity(Intent.createChooser(shareIntent, mActivity.getString(R.string.share_label)));
            }
            else{
                /*final MainActivity.VideoFragment videoFragment =
                        (MainActivity.VideoFragment) mActivity.getSupportFragmentManager().findFragmentById(R.id.video_fragment_container);
                videoFragment.setVideoId(firstVideo.getId());

                final DraggableView draggableView = (DraggableView) mActivity.findViewById(R.id.draggable_view);
                draggableView.setVisibility(View.VISIBLE);
                draggableView.setHorizontalAlphaEffectEnabled(false);
                draggableView.setClickToMinimizeEnabled(false);
                draggableView.maximize();

                recyclerVideoInfo = (RecyclerView) mActivity.findViewById(R.id.recyclerVideoInfo);*/

                final Handler handler = new Handler();
                //Request para obtener datos del canal
                new Thread(){
                    public void run(){
                        YouTubeService service = new YouTubeService(context);
                        final List<Channel> channel = service.getChannels(firstVideo.getSnippet().getChannelId());
                        final List<Video> relatedVideos = service.getRelatedVideos(firstVideo.getId());
                        handler.post(new Runnable() {
                            public void run() {

                                RvJoiner joiner = new RvJoiner();

                                VideoInfoHeaderAdapter videoInfoAdapter = new VideoInfoHeaderAdapter(context, firstVideo, channel);
                                RelatedAdapter relatedAdapter = new RelatedAdapter(context, relatedVideos);

                                joiner.add(new JoinableAdapter(videoInfoAdapter));
                                joiner.add(new JoinableAdapter(relatedAdapter));

                                recyclerVideoInfo.setAdapter(joiner.getAdapter());

                                //videoFragment.play();

                            }
                        });
                    }
                }.start();
            }
        }
    }
}