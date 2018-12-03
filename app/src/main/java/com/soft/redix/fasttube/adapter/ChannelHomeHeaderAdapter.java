package com.soft.redix.fasttube.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.api.services.youtube.model.Channel;
import com.soft.redix.fasttube.R;
import com.soft.redix.fasttube.util.AnimationUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Gonzalo on 21/3/2016.
 */
public class ChannelHomeHeaderAdapter extends RecyclerView.Adapter<ChannelHomeHeaderAdapter.SimpleViewHolder>
        implements ItemClickListener {

    private final Context context;
    private List<Channel> channels;
    private String channelId, bannerUrl, thumbUrl, channelTitle;
    private int channelSubs;
    private boolean flag;

    public static class SimpleViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        // Campos respectivos de un item
        public ImageView banner;
        public ItemClickListener listener;
        public CircleImageView thumbnail;
        public TextView channelTitle, channelSubsCount;

        public SimpleViewHolder(View v, ItemClickListener listener) {
            super(v);
            banner = (ImageView) v.findViewById(R.id.channelBanner);
            thumbnail = (CircleImageView) v.findViewById(R.id.channelThumbnail);
            channelSubsCount = (TextView) v.findViewById(R.id.channelHeaderSubsCount);
            channelTitle = (TextView) v.findViewById(R.id.channelHeaderTitle);
            this.listener = listener;
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(v, getAdapterPosition());
        }
    }

    public ChannelHomeHeaderAdapter(Context context, String channelId, String bannerUrl, String thumbUrl,
                                    String channelTitle, int channelSubs) {
        this.context = context;
        this.channelId = channelId;
        this.bannerUrl = bannerUrl;
        this.thumbUrl = thumbUrl;
        this.channelSubs = channelSubs;
        this.channelTitle = channelTitle;
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.header_channel, viewGroup, false);
        //AdView de Admob
        /*
        AdView mAdView = (AdView) v.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        */

        //AdView de Facebook
        /*AdView adView = new AdView(context,
                context.getString(R.string.facebook_placement_id_channel), AdSize.BANNER_HEIGHT_50);
        RelativeLayout layout = (RelativeLayout) v.findViewById(R.id.channelLayoutAd);
        layout.addView(adView);
        adView.loadAd();*/
        return new SimpleViewHolder(v, this);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, int i) {
        viewHolder.channelTitle.setText(channelTitle);
        viewHolder.channelSubsCount.setText(
                NumberFormat.getNumberInstance().format(channelSubs));

        Picasso.get()
                .load(bannerUrl)
                .fit().centerCrop()
                .into(viewHolder.banner);
        Picasso.get()
                .load(thumbUrl)
                .fit().centerCrop()
                .into(viewHolder.thumbnail, new Callback() {
                    @Override
                    public void onSuccess() {
                        if(!flag) {
                            AnimationUtil.enterReveal(viewHolder.thumbnail);
                            flag = true;
                        }
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
    }

    /**
     * Sobrescritura del método de la interfaz {@link ItemClickListener}
     *
     * @param view     item actual
     * @param position posición del item actual
     */
    @Override
    public void onItemClick(View view, final int position) {
    }

}