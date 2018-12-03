package com.soft.redix.fasttube.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
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
import com.squareup.picasso.Picasso;

import org.joda.time.Period;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;

import java.text.NumberFormat;
import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import su.j2e.rvjoiner.JoinableAdapter;
import su.j2e.rvjoiner.RvJoiner;

/**
 * Created by gonzalo on 19/10/17.
 */

public class PlaylistItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements ItemClickListener {

    private RecyclerView recyclerVideoInfo;
    private final Context context;
    private List<Video> items;
    private String videoCount, channelTitle, playlistTitle, playlistThumb, playlistId;
    private PeriodFormatter formatter = ISOPeriodFormat.standard();

    public static class SimpleViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        // Campos respectivos de un item
        public ImageView thumbnail;
        public TextView textTitle, textChannelTitle, textViews, textDuration, aux;
        public ItemClickListener listener;

        public SimpleViewHolder(View v, ItemClickListener listener) {
            super(v);
            thumbnail = (ImageView) v.findViewById(R.id.relatedVideoThumbnail);
            textTitle = (TextView) v.findViewById(R.id.relatedVideoTitle);
            textChannelTitle = (TextView) v.findViewById(R.id.relatedVideoChannel);
            textViews = (TextView) v.findViewById(R.id.relatedVideoViews);
            textDuration = (TextView) v.findViewById(R.id.videoDuration);
            aux = (TextView) v.findViewById(R.id.aux);
            aux.setOnClickListener(this);
            this.listener = listener;
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(v, getAdapterPosition());
        }
    }

    public PlaylistItemsAdapter(Context context, List<Video> items, ContentValues values) {
        this.context = context;
        this.items = items;

        this.videoCount = values.getAsString("videoCount");
        this.playlistTitle = values.getAsString("playlistTitle");
        this.channelTitle = values.getAsString("channelTitle");
        this.playlistThumb = values.getAsString("playlistThumb");
        this.playlistId = values.getAsString("playlistId");
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0)
            return ViewType.HEAD;
        else if(items.get(position) == null)
            return ViewType.PROG;
        else
            return ViewType.ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        if(viewType == ViewType.HEAD){
            v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.header_playlist_items, viewGroup, false);
            return new PlaylistItemsHeaderHolder(v, this);
        }
        else if(viewType == ViewType.ITEM) {
            v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.list_related, viewGroup, false);
            return new SimpleViewHolder(v, this);
        }
        else {
            v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.list_progress, viewGroup, false);
            return new ProgressViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {

        if(viewHolder instanceof PlaylistItemsHeaderHolder){
            PlaylistItemsHeaderHolder holder = (PlaylistItemsHeaderHolder) viewHolder;
            holder.videoCount.setText(videoCount + " videos");
            holder.channelTitle.setText(channelTitle);
            holder.title.setText(playlistTitle);
        }
        else if(viewHolder instanceof SimpleViewHolder) {
            SimpleViewHolder holder = (SimpleViewHolder) viewHolder;
            Video video = items.get(i);
            try {
                holder.textChannelTitle.setText(video.getSnippet().getChannelTitle());
                holder.textTitle.setText(video.getSnippet().getTitle());
                holder.textViews.setText(NumberFormat.getNumberInstance().format(video.getStatistics().getViewCount()) + " " +
                        context.getString(R.string.views));
                String duration = "";
                Period p = formatter.parsePeriod(video.getContentDetails().getDuration());
                if (p.getHours() > 0) {
                    if (p.getHours() > 9)
                        duration += String.valueOf(p.getHours());
                    else {
                        duration += "0";
                        duration += String.valueOf(p.getHours());
                    }
                    duration += ":";
                }
                if (p.getMinutes() > 9)
                    duration += String.valueOf(p.getMinutes());
                else {
                    duration += "0";
                    duration += String.valueOf(p.getMinutes());
                }
                duration += ":";
                if (p.getSeconds() > 9)
                    duration += String.valueOf(p.getSeconds());
                else {
                    duration += "0";
                    duration += String.valueOf(p.getSeconds());
                }
                holder.textDuration.setText(duration);
                Picasso.get()
                        .load(video.getSnippet().getThumbnails().getMedium().getUrl())
                        .transform(new RoundedCornersTransformation(30, 0))
                        .fit().centerCrop()
                        .into(holder.thumbnail);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        int viewId = view.getId();

        if(viewId == R.id.aux) {
            final AppCompatActivity activity = (AppCompatActivity) context;
            /*final MainActivity.VideoFragment videoFragment =
                    (MainActivity.VideoFragment) activity.getSupportFragmentManager().findFragmentById(R.id.video_fragment_container);
            videoFragment.setVideoId(items.get(position).getId());

            final DraggableView draggableView = activity.findViewById(R.id.draggable_view);
            draggableView.setVisibility(View.VISIBLE);
            draggableView.setHorizontalAlphaEffectEnabled(false);
            draggableView.setClickToMinimizeEnabled(false);
            draggableView.maximize();

            final RecyclerView recyclerVideoInfo =  activity.findViewById(R.id.recyclerVideoInfo);
            recyclerVideoInfo.setAdapter(new ProgressAdapter());
            MainActivity.videoId = items.get(position).getId();*/

            final Handler handler = new Handler();
            //Request para obtener datos del canal
            new Thread(){
                public void run(){
                    YouTubeService service = new YouTubeService(context);
                    final List<Channel> channel = service.getChannels(items.get(position - 1).getSnippet().getChannelId());
                    final List<Video> relatedVideos = service.getRelatedVideos(items.get(position - 1).getId());
                    handler.post(new Runnable() {
                        public void run() {

                            //Related videos
                            VideoInfoHeaderAdapter videoInfoAdapter = new VideoInfoHeaderAdapter(context, items.get(position), channel);
                            RelatedAdapter relatedAdapter = new RelatedAdapter(context, relatedVideos);

                            RvJoiner joiner = new RvJoiner();
                            joiner.add(new JoinableAdapter(videoInfoAdapter));
                            joiner.add(new JoinableAdapter(relatedAdapter));

                            recyclerVideoInfo.setAdapter(joiner.getAdapter());

                            //videoFragment.play();

                            //activity.findViewById(R.id.progressRelated).setVisibility(View.GONE);
                        }
                    });
                }
            }.start();
        }
        //HEADER
        else{
            AppCompatActivity mActivity = (AppCompatActivity) context;
            final Video firstVideo = items.get(1);

            if(viewId == R.id.playlistItemsInfo) {
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
                if(viewId == R.id.playlistItemsShare){
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
                    videoFragment.setVideoId(items.get(1).getId());

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

}
