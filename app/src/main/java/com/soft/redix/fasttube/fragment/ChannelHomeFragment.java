package com.soft.redix.fasttube.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.api.services.youtube.model.Video;
import com.soft.redix.fasttube.R;
import com.soft.redix.fasttube.adapter.ChannelHomeHeaderAdapter;
import com.soft.redix.fasttube.adapter.ProgressAdapter;
import com.soft.redix.fasttube.adapter.RelatedAdapter;
import com.soft.redix.fasttube.adapter.VideoAdapter;
import com.soft.redix.fasttube.util.AnimationUtil;
import com.soft.redix.fasttube.util.YouTubeService;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import su.j2e.rvjoiner.JoinableAdapter;
import su.j2e.rvjoiner.JoinableLayout;
import su.j2e.rvjoiner.RvJoiner;

/**
 * Created by Gonzalo on 20/3/2016.
 */
public class ChannelHomeFragment extends Fragment {

    private YouTubeService service;
    private RecyclerView recyclerHome;
    private String channelId, bannerUrl, thumbUrl, channelTitle, likedPlaylist;
    private List<Video> likedVideos;
    private int channelSubs;
    private RvJoiner rvJoiner;
    private AVLoadingIndicatorView progressChannelHome;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_channel_home, container, false);

        channelId = getArguments().getString("channel_id");
        bannerUrl = getArguments().getString("channel_banner");
        thumbUrl = getArguments().getString("channel_thumb");
        channelTitle = getArguments().getString("channel_title");
        channelSubs = getArguments().getInt("channel_subs");
        likedPlaylist = getArguments().getString("channel_liked");

        progressChannelHome = (AVLoadingIndicatorView) v.findViewById(R.id.progressChannelHome);

        recyclerHome = (RecyclerView) v.findViewById(R.id.recyclerChannelHome);
        recyclerHome.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerHome.setAdapter(new ProgressAdapter());

        final Handler handler = new Handler();
        new Thread(){
            public void run(){
                service = new YouTubeService(getContext());
                final List<Video> popularVideos = service.getChannelMostPopularVideos(channelId);
                if(likedPlaylist != null)
                    likedVideos = service.getChannelLikedVideos(likedPlaylist);
                handler.post(new Runnable() {
                    public void run() {

                        ChannelHomeHeaderAdapter headerAdapter = new ChannelHomeHeaderAdapter(getContext(),
                                channelId, bannerUrl, thumbUrl, channelTitle, channelSubs);
                        RelatedAdapter mostPopularAdapter = new RelatedAdapter(getContext(), popularVideos);

                        rvJoiner = new RvJoiner();
                        rvJoiner.add(new JoinableAdapter(headerAdapter));
                        rvJoiner.add(new JoinableAdapter(mostPopularAdapter));

                        if(likedVideos != null) {
                            RelatedAdapter likedAdapter = new RelatedAdapter(getContext(), likedVideos);
                            rvJoiner.add(new JoinableLayout(R.layout.header_channel_liked));
                            rvJoiner.add(new JoinableAdapter(likedAdapter));
                        }

                        recyclerHome.setAdapter(rvJoiner.getAdapter());

                        progressChannelHome.hide();
                    }
                });
            }
        }.start();

        return v;
    }

}