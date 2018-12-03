package com.soft.redix.fasttube.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.google.api.services.youtube.model.Channel;
import com.soft.redix.fasttube.R;
import com.soft.redix.fasttube.fragment.ChannelAboutFragment;
import com.soft.redix.fasttube.fragment.ChannelHomeFragment;
import com.soft.redix.fasttube.fragment.ChannelPlaylistsFragment;
import com.soft.redix.fasttube.fragment.ChannelVideosFragment;

/**
 * Created by Gonzalo on 20/3/2016.
 */
public class ChannelPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence Titles[];
    Context context;
    int numbOfTabs = 4;
    Channel actualChannel;

    public ChannelPagerAdapter(FragmentManager fm, Context context, Channel channel) {
        super(fm);
        this.context = context;
        this.actualChannel = channel;
        Titles = new CharSequence[4];
        Titles[0] = context.getString(R.string.tab_home);
        Titles[1] = context.getString(R.string.tab_videos);
        Titles[2] = context.getString(R.string.tab_playlists);
        Titles[3] = context.getString(R.string.tab_about);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0)
        {
            Fragment channelHomeFragment = new ChannelHomeFragment();
            Bundle args = new Bundle();
            args.putString("channel_id", actualChannel.getId());
            args.putString("channel_banner", actualChannel.getBrandingSettings().getImage().getBannerMobileImageUrl());
            args.putString("channel_thumb", actualChannel.getSnippet().getThumbnails().getMedium().getUrl());
            args.putString("channel_title", actualChannel.getSnippet().getTitle());
            args.putInt("channel_subs", actualChannel.getStatistics().getSubscriberCount().intValue());
            args.putString("channel_liked", actualChannel.getContentDetails().getRelatedPlaylists().getLikes());
            channelHomeFragment.setArguments(args);
            return channelHomeFragment;
        }
        else if(position == 1) {
            Fragment channelVideosFragment = new ChannelVideosFragment();
            Bundle args = new Bundle();
            args.putString("channel_id", actualChannel.getId());
            channelVideosFragment.setArguments(args);
            return channelVideosFragment;
        }
        else if(position == 2){
            Fragment channelPlaylistsFragment = new ChannelPlaylistsFragment();
            Bundle args = new Bundle();
            args.putString("channel_id", actualChannel.getId());
            channelPlaylistsFragment.setArguments(args);
            return channelPlaylistsFragment;
        }
        else{
            Fragment channelAboutFragment = new ChannelAboutFragment();
            Bundle args = new Bundle();
            args.putString("channel_about", actualChannel.getSnippet().getDescription());
            args.putInt("channel_views", actualChannel.getStatistics().getViewCount().intValue());
            args.putInt("channel_subs", actualChannel.getStatistics().getSubscriberCount().intValue());
            channelAboutFragment.setArguments(args);
            return channelAboutFragment;
        }
    }

    @Override
    public int getCount() {
        return numbOfTabs;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }
}
