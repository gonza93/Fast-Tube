package com.soft.redix.fasttube.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.PromotedItem;
import com.google.api.services.youtube.model.Video;
import com.soft.redix.fasttube.R;
import com.soft.redix.fasttube.activity.MainActivity;
import com.soft.redix.fasttube.util.YouTubeService;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;

import su.j2e.rvjoiner.JoinableAdapter;
import su.j2e.rvjoiner.RvJoiner;

/**
 * Created by Gonzalo on 21/3/2016.
 */
public class TrendVideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements ItemClickListener {

    private RecyclerView recyclerVideoInfo;
    private final Context context;
    private List<Video> items;
    private List<Video> relatedVideos;
    private RvJoiner joiner;

    public static class SimpleViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        // Campos respectivos de un item
        public ImageView thumbnail, channelThumb;
        public TextView textTitle, textChannelTitle, textViews, aux;
        public ItemClickListener listener;
        public CardView cardView;

        public SimpleViewHolder(View v, ItemClickListener listener) {
            super(v);
            thumbnail = (ImageView) v.findViewById(R.id.trendThumb);
            cardView = (CardView) v.findViewById(R.id.trendCard);
            cardView.setPreventCornerOverlap(false);
            channelThumb = (ImageView) v.findViewById(R.id.trendChannelThumb);
            textTitle = (TextView) v.findViewById(R.id.trendTitle);
            textChannelTitle = (TextView) v.findViewById(R.id.trendChannelTitle);
            textViews = (TextView) v.findViewById(R.id.trendViews);
            aux = (TextView) v.findViewById(R.id.aux);
            aux.setOnClickListener(this);
            this.listener = listener;
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(v, getAdapterPosition());
        }
    }

    public TrendVideoAdapter(Context context, List<Video> items) {
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

    public List<Video> getDataSet(){
        return items;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if(viewType == ViewType.ITEM) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.list_trend, viewGroup, false);
            return new SimpleViewHolder(v, this);
        }
        else{
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.list_progress, viewGroup, false);
            return new ProgressViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if(viewHolder instanceof SimpleViewHolder) {
            SimpleViewHolder holder = (SimpleViewHolder) viewHolder;
            Video video = items.get(i);

                holder.textChannelTitle.setText(video.getSnippet().getChannelTitle());
                holder.textTitle.setText(video.getSnippet().getTitle());
                try {
                    holder.textViews.setText(NumberFormat.getNumberInstance().format(video.getStatistics().getViewCount()) + " " +
                            context.getString(R.string.views));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    Picasso.get()
                            .load(video.getSnippet().getThumbnails().getHigh().getUrl())
                            .fit().centerCrop()
                            .into(holder.thumbnail);
                    Picasso.get()
                            .load(video.getSnippet().getThumbnails().getDefault().getUrl())
                            .fit().centerCrop()
                            .into(holder.channelThumb);
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
        //Cargamos video seleccionado al Youtube player api
        final AppCompatActivity activity = (AppCompatActivity) context;
        /*final MainActivity.VideoFragment videoFragment =
                (MainActivity.VideoFragment) activity.getSupportFragmentManager().findFragmentById(R.id.video_fragment_container);
        videoFragment.setVideoId(items.get(position).getId());

        final DraggableView draggableView = (DraggableView) activity.findViewById(R.id.draggable_view);
        draggableView.setVisibility(View.VISIBLE);
        draggableView.setHorizontalAlphaEffectEnabled(false);
        draggableView.setClickToMinimizeEnabled(false);
        draggableView.maximize();

        recyclerVideoInfo = (RecyclerView) activity.findViewById(R.id.recyclerVideoInfo);
        recyclerVideoInfo.setAdapter(new ProgressAdapter());
        MainActivity.videoId = items.get(position).getId();*/

        final Handler handler = new Handler();
        //Request para obtener datos del canal
        new Thread(){
            public void run(){
                YouTubeService service = new YouTubeService(context);
                final List<Channel> channel = service.getChannels(items.get(position).getSnippet().getChannelId());
                relatedVideos = service.getRelatedVideos(items.get(position).getId());
                handler.post(new Runnable() {
                    public void run() {

                        joiner = new RvJoiner();

                        VideoInfoHeaderAdapter videoInfoAdapter = new VideoInfoHeaderAdapter(context, items.get(position), channel);
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
