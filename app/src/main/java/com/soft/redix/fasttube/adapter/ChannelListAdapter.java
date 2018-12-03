package com.soft.redix.fasttube.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.api.services.youtube.model.Channel;
import com.soft.redix.fasttube.R;
import com.soft.redix.fasttube.activity.MainActivity;
import com.soft.redix.fasttube.fragment.ChannelFragment;
import com.soft.redix.fasttube.util.NavigationUtil;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;

/**
 * Created by Gonzalo on 29/3/2016.
 */
public class ChannelListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements ItemClickListener {

    private final Context context;
    private List<Channel> items;

    public static class SimpleViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        // Campos respectivos de un item
        public ImageView thumbnail;
        public TextView textTitle, textVideos, textSubs;
        public ItemClickListener listener;

        public SimpleViewHolder(View v, ItemClickListener listener) {
            super(v);
            thumbnail = (ImageView) v.findViewById(R.id.channelThumb);
            textTitle = (TextView) v.findViewById(R.id.channelTitle);
            textVideos = (TextView) v.findViewById(R.id.channelVideos);
            textSubs = (TextView) v.findViewById(R.id.channelSubs);
            this.listener = listener;
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(v, getAdapterPosition());
        }
    }

    public ChannelListAdapter(Context context, List<Channel> items) {
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

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        if(viewType == ViewType.ITEM) {
            v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.list_channel, viewGroup, false);
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
            Channel channel = items.get(i);
            holder.textTitle.setText(channel.getSnippet().getTitle());
            holder.textSubs.setText(NumberFormat.getNumberInstance().format(channel.getStatistics().getSubscriberCount())
                    + " " + context.getString(R.string.subs));
            holder.textVideos.setText(NumberFormat.getNumberInstance().format(channel.getStatistics().getVideoCount()) + " videos");
            Picasso.get()
                    .load(channel.getSnippet().getThumbnails().getMedium().getUrl())
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
        AppCompatActivity mActivity = (AppCompatActivity) context;

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
                        ((TextView) toolbarLayout.findViewById(R.id.toolbar_text)).setText(items.get(position).getSnippet().getTitle());

                    }
                }, 200);
            }
        });

        mActivity.findViewById(R.id.main_title).setVisibility(View.GONE);

        ChannelFragment channelFragment = new ChannelFragment();
        channelFragment.setChannel(items.get(position));
        replaceFragment(channelFragment, mActivity);

        /*TabLayout mTabLayout = (TabLayout) mActivity.findViewById(R.id.tab_layout);
        ViewPager mPager = (ViewPager) mActivity.findViewById(R.id.pager);
        mTabLayout.clearOnTabSelectedListeners();

        mPager.setVisibility(View.VISIBLE);
        mTabLayout.setVisibility(View.VISIBLE);
        mTabLayout.animate().scaleY(1).setInterpolator(new AccelerateInterpolator()).start();

        mPager.setAdapter(channelPagerAdapter);
        mTabLayout.setSelectedTabIndicatorColor(Color.WHITE);

        mTabLayout.setupWithViewPager(mPager);
        mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));*/
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
