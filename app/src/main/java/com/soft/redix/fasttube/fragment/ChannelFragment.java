package com.soft.redix.fasttube.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

import com.google.api.services.youtube.model.Channel;
import com.soft.redix.fasttube.R;
import com.soft.redix.fasttube.adapter.ChannelPagerAdapter;

/**
 * Created by gonzalo on 15/2/18.
 */

public class ChannelFragment extends Fragment {

    private Channel channel;

    public void setChannel(Channel channel){
        this.channel = channel;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_search, container, false);

        ChannelPagerAdapter channelPagerAdapter = new ChannelPagerAdapter(getFragmentManager(), getContext(), channel);

        TabLayout mTabLayout = (TabLayout) v.findViewById(R.id.tab_layout);
        ViewPager mPager = (ViewPager) v.findViewById(R.id.pager);

        mPager.setVisibility(View.VISIBLE);
        mTabLayout.setVisibility(View.VISIBLE);
        mTabLayout.animate().scaleY(1).setInterpolator(new AccelerateInterpolator()).start();

        mPager.setAdapter(channelPagerAdapter);
        mTabLayout.setupWithViewPager(mPager);

        //mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        return v;
    }
}
