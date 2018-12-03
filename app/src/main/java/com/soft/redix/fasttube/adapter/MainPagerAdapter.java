package com.soft.redix.fasttube.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.soft.redix.fasttube.R;
import com.soft.redix.fasttube.fragment.DiscoverFragment;
import com.soft.redix.fasttube.fragment.HomeFragment;
import com.soft.redix.fasttube.fragment.SearchVideosFragment;

/**
 * Created by gonzalo on 25/6/17.
 */

public class MainPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence titles[];
    Context context;
    FragmentManager fm;

    public MainPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.fm = fm;
        this.context = context;
        titles = new CharSequence[3];
        titles[0] = context.getString(R.string.tab_home);
        titles[1] = context.getString(R.string.action_search);
        titles[2] = context.getString(R.string.tab_discover);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new HomeFragment();
            case 1:
                return new SearchVideosFragment();
            case 2:
                return new DiscoverFragment();
            default:
                return null;
        }
    }


    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}