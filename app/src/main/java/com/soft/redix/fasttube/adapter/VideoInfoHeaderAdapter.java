package com.soft.redix.fasttube.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.Video;
import com.soft.redix.fasttube.R;
import com.soft.redix.fasttube.activity.MainActivity;
import com.soft.redix.fasttube.fragment.ChannelFragment;
import com.soft.redix.fasttube.util.AnimationUtil;
import com.soft.redix.fasttube.util.NavigationUtil;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Gonzalo on 1/3/2016.
 */
public class VideoInfoHeaderAdapter extends RecyclerView.Adapter<VideoInfoHeaderAdapter.SimpleViewHolder>
        implements ItemClickListener {

    private final Context context;
    private Video item;
    private List<Channel> channels;

    public static class SimpleViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        // Campos respectivos de un item
        public CircleImageView thumbnail;
        public TextView textTitle, textChannelTitle, textViews, textDesc, aux;
        public View infoLayout;
        public LinearLayout shareVideo, openVideo;
        public TextView textLike, textDislike, textSubs;
        public ItemClickListener listener;
        public AVLoadingIndicatorView progress;
        public ProgressBar bar;

        public SimpleViewHolder(View v, ItemClickListener listener) {
            super(v);
            thumbnail = (CircleImageView) v.findViewById(R.id.videoInfoChannelThumb);
            textTitle = (TextView) v.findViewById(R.id.videoInfoTitle);
            textChannelTitle = (TextView) v.findViewById(R.id.videoChannelTitle);
            textViews = (TextView) v.findViewById(R.id.videoInfoViewCount);
            textLike = (TextView) v.findViewById(R.id.videoLikeCount);
            textDislike = (TextView) v.findViewById(R.id.videoDislikeCount);
            textSubs = (TextView) v.findViewById(R.id.videoChannelSubsCount);
            textDesc = (TextView) v.findViewById(R.id.videoInfoDesc);
            shareVideo = (LinearLayout) v.findViewById(R.id.shareLayout);
            shareVideo.setOnClickListener(this);
            infoLayout = v.findViewById(R.id.layoutVideoInfo);
            infoLayout.setOnClickListener(this);
            bar = (ProgressBar) v.findViewById(R.id.likeMeter);
            aux = (TextView) v.findViewById(R.id.aux);
            aux.setOnClickListener(this);
            this.listener = listener;
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(v, getAdapterPosition());
        }
    }

    public VideoInfoHeaderAdapter(Context context, Video video, List<Channel> channels) {
        this.context = context;
        this.item = video;
        this.channels = channels;
    }

    @Override
    public int getItemCount() {
        return channels.size();
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.header_videoinfo, viewGroup, false);

        return new SimpleViewHolder(v, this);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder viewHolder, int i) {
        Video video = item;
        Channel channel = channels.get(i);
        viewHolder.textChannelTitle.setText(channel.getSnippet().getTitle());
        viewHolder.textTitle.setText(video.getSnippet().getTitle());
        try {
            viewHolder.textViews.setText(NumberFormat.getNumberInstance().format(video.getStatistics().getViewCount()) + " " +
                    context.getString(R.string.views));


            int likes, dislikes;
            if (video.getStatistics().getLikeCount() != null) {
                likes = video.getStatistics().getLikeCount().intValue();
                dislikes = video.getStatistics().getDislikeCount().intValue();

                viewHolder.bar.setMax(likes + dislikes);
                viewHolder.bar.setProgress(likes);

                viewHolder.textLike.setText(NumberFormat.getNumberInstance().format(video.getStatistics().getLikeCount()));
                viewHolder.textDislike.setText(NumberFormat.getNumberInstance().format(video.getStatistics().getDislikeCount()));
            }
            viewHolder.textSubs.setText(NumberFormat.getNumberInstance().format(channel.getStatistics().getSubscriberCount()) +
                    " " + context.getString(R.string.subs));
        }
        catch (Exception e){

        }
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(item.getSnippet().getPublishedAt().getValue());
        SimpleDateFormat dateParser = new SimpleDateFormat("MMM dd, yyyy");
        String pubDate = context.getString(R.string.published_on) + " " + dateParser.format(cal.getTime());
        viewHolder.textDesc.setText(pubDate + " - " + item.getSnippet().getDescription());

        Picasso.get()
                .load(channel.getSnippet().getThumbnails().getMedium().getUrl())
                .fit().centerCrop()
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

        AppCompatActivity mActivity = (AppCompatActivity) context;

        //Tocar en el titulo
        if(view == mActivity.findViewById(R.id.layoutVideoInfo)){
            View arrow = mActivity.findViewById(R.id.videoInfoArrow);
            View desc = mActivity.findViewById(R.id.videoInfoDesc);
            if(desc.getVisibility() == View.GONE) {
                AnimationUtil.fadeIn(desc, 300);
                AnimationUtil.rotateView(arrow, 180);
            }
            else {
                AnimationUtil.fadeOut(desc, 300);
                AnimationUtil.rotateView(arrow, 0);
            }
        }
        //Tocar en boton compartir
        else if(view == mActivity.findViewById(R.id.shareLayout)){
            String url = "https://www.youtube.com/watch?v=" + item.getId();
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, url);
            shareIntent.setType("text/plain");
            mActivity.startActivity(Intent.createChooser(shareIntent, mActivity.getString(R.string.share_label)));
        }
        //Tocar en el canal
        else {
            final View searchLayout = mActivity.findViewById(R.id.main_search_layout);
            final View toolbarLayout = mActivity.findViewById(R.id.toolbar_text_layout);

            searchLayout.animate().setListener(null);
            searchLayout.animate().alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    searchLayout.setVisibility(View.GONE);
                    searchLayout.animate().setListener(null);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toolbarLayout.setVisibility(View.VISIBLE);
                            ((TextView) toolbarLayout.findViewById(R.id.toolbar_text)).setText(channels.get(position).getSnippet().getTitle());
                        }
                    }, 200);
                }
            });

            if(mActivity.findViewById(R.id.main_title).isShown())
                mActivity.findViewById(R.id.main_title).setVisibility(View.INVISIBLE);


            /*DraggableView draggableView = (DraggableView) mActivity.findViewById(R.id.draggable_view);
            draggableView.minimize();*/

            ChannelFragment channelFragment = new ChannelFragment();
            channelFragment.setChannel(channels.get(position));
            replaceFragment(channelFragment, mActivity);
        }
    }

    public void replaceFragment(Fragment fragment, AppCompatActivity activity) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(MainActivity.actualFragment.getId(), fragment);
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }

}
