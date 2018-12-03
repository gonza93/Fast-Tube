package com.soft.redix.fasttube.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.soft.redix.fasttube.R;
import com.soft.redix.fasttube.fragment.SearchChannelsFragment;
import com.soft.redix.fasttube.fragment.SearchPlaylistsFragment;
import com.soft.redix.fasttube.fragment.SearchVideosFragment;


/**
 * Created by Gonzalo on 28/3/2016.
 */
public class SearchPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence Titles[];
    Context context;
    int numbOfTabs = 3;
    String query;

    public SearchPagerAdapter(FragmentManager fm, Context context, String query) {
        super(fm);
        this.context = context;
        this.query = query;
        Titles = new CharSequence[3];
        Titles[0] = context.getString(R.string.tab_videos);
        Titles[1] = context.getString(R.string.tab_channels);
        Titles[2] = context.getString(R.string.tab_playlists);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0)
        {
            Fragment searchFragment = new SearchVideosFragment();
            Bundle args = new Bundle();
            args.putString("query", query);
            searchFragment.setArguments(args);
            return searchFragment;
        }
        else if(position == 1) {
            Fragment searchChannelsFragment= new SearchChannelsFragment();
            Bundle args = new Bundle();
            args.putString("query", query);
            searchChannelsFragment.setArguments(args);
            return searchChannelsFragment;
        }
        else{
            Fragment searchPlaylistsFragment = new SearchPlaylistsFragment();
            Bundle args = new Bundle();
            args.putString("query", query);
            searchPlaylistsFragment.setArguments(args);
            return searchPlaylistsFragment;
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
