package com.soft.redix.fasttube.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.soft.redix.fasttube.R;
import com.soft.redix.fasttube.util.YouTubeService;

import java.text.NumberFormat;

/**
 * Created by Gonzalo on 20/3/2016.
 */
public class ChannelAboutFragment extends Fragment {

    private YouTubeService service;
    private RecyclerView recyclerHome;
    private String channelId;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_channel_about, container, false);

        String about = getArguments().getString("channel_about");
        String subs = NumberFormat.getNumberInstance().format(getArguments().getInt("channel_subs"));
        String views = NumberFormat.getNumberInstance().format(getArguments().getInt("channel_views"));

        TextView description = (TextView) v.findViewById(R.id.aboutChannelDescription);
        description.setText(about);
        TextView subCount = (TextView) v.findViewById(R.id.aboutSubscribers);
        subCount.setText(subs + " " + getResources().getString(R.string.subs));
        TextView viewCount = (TextView) v.findViewById(R.id.aboutViews);
        viewCount.setText(views + " " + getResources().getString(R.string.views));

        return v;
    }
}