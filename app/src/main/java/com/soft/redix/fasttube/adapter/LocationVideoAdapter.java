package com.soft.redix.fasttube.adapter;

import android.content.Context;
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
 * Created by Gonzalo on 1/4/2016.
 */
public class LocationVideoAdapter extends RecyclerView.Adapter<LocationVideoAdapter.SimpleViewHolder>
        implements ItemClickListener {

    private RecyclerView recyclerVideoInfo;
    private final Context context;
    private List<Video> items;
    private PeriodFormatter formatter = ISOPeriodFormat.standard();

    public static class SimpleViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        // Campos respectivos de un item
        public ImageView thumbnail;
        public TextView textTitle, textChannelTitle, textViews, textDuration, aux;
        public ItemClickListener listener;

        public SimpleViewHolder(View v, ItemClickListener listener) {
            super(v);
            thumbnail = (ImageView) v.findViewById(R.id.locationVideoThumbnail);
            textTitle = (TextView) v.findViewById(R.id.locationVideoTitle);
            textChannelTitle = (TextView) v.findViewById(R.id.locationVideoChannel);
            textViews = (TextView) v.findViewById(R.id.locationVideoViews);
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

    public LocationVideoAdapter(Context context, List<Video> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_location, viewGroup, false);
        return new SimpleViewHolder(v, this);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder viewHolder, int i) {
        Video video = items.get(i);
        viewHolder.textChannelTitle.setText(video.getSnippet().getChannelTitle());
        viewHolder.textTitle.setText(video.getSnippet().getTitle());
        viewHolder.textViews.setText(NumberFormat.getNumberInstance().format(video.getStatistics().getViewCount()) + " " +
                context.getString(R.string.views));
        String duration = "";
        Period p = formatter.parsePeriod(video.getContentDetails().getDuration());
        if(p.getHours() > 0){
            if(p.getHours() > 9)
                duration += String.valueOf(p.getHours());
            else {
                duration += "0";
                duration += String.valueOf(p.getHours());
            }
            duration += ":";
        }
        if(p.getMinutes() > 9)
            duration += String.valueOf(p.getMinutes());
        else {
            duration += "0";
            duration += String.valueOf(p.getMinutes());
        }
        duration += ":";
        if(p.getSeconds() > 9)
            duration += String.valueOf(p.getSeconds());
        else{
            duration += "0";
            duration += String.valueOf(p.getSeconds());
        }
        viewHolder.textDuration.setText(duration);
        Picasso.get()
                .load(video.getSnippet().getThumbnails().getMedium().getUrl())
                .fit().centerCrop()
                .transform(new RoundedCornersTransformation(20,0))
                .into(viewHolder.thumbnail);
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
        videoFragment.setVideoId(items.get(position - 1).getId());

        final DraggableView draggableView = (DraggableView) activity.findViewById(R.id.draggable_view);
        draggableView.setVisibility(View.VISIBLE);
        draggableView.setHorizontalAlphaEffectEnabled(false);
        draggableView.setClickToMinimizeEnabled(false);
        draggableView.maximize();

        recyclerVideoInfo = activity.findViewById(R.id.recyclerVideoInfo);
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
                        VideoInfoHeaderAdapter videoInfoAdapter = new VideoInfoHeaderAdapter(context, items.get(position - 1), channel);
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

}
