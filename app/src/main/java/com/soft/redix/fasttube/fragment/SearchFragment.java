package com.soft.redix.fasttube.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.soft.redix.fasttube.R;
import com.soft.redix.fasttube.adapter.SearchPagerAdapter;
import com.soft.redix.fasttube.util.NavigationUtil;

/**
 * Created by gonzalo on 13/2/18.
 */

public class SearchFragment extends Fragment {

    public TabLayout searchTabLayout;
    public SearchPagerAdapter searchPagerAdapter;
    public ViewPager searchViewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_search, container, false);

        String query = NavigationUtil.getInstance().popQuery();

        searchViewPager = (ViewPager) v.findViewById(R.id.pager);
        searchTabLayout = (TabLayout) v.findViewById(R.id.tab_layout);


        if (!query.equals("")) {
            searchPagerAdapter = new SearchPagerAdapter(getFragmentManager(), getContext(), query);

            searchViewPager.setAdapter(searchPagerAdapter);
            searchViewPager.setOffscreenPageLimit(0);

            searchTabLayout.setupWithViewPager(searchViewPager);
        }

        return v;
    }

}
