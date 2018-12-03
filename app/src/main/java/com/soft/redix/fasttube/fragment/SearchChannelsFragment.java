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

import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.soft.redix.fasttube.R;
import com.soft.redix.fasttube.adapter.ChannelListAdapter;
import com.soft.redix.fasttube.util.YouTubeService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gonzalo on 29/3/2016.
 */
public class SearchChannelsFragment extends Fragment {

    private YouTubeService service;
    private RecyclerView recyclerVideos;
    private Handler handler;
    private String query;
    private List<Channel> searchChannels;
    private ChannelListAdapter channelAdapter;
    private LinearLayoutManager linearLayoutManager;

    private boolean loading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    
    private String nextPageToken;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_search_channels, container, false);

        query = getArguments().getString("query");
        linearLayoutManager = new LinearLayoutManager(getContext());
        searchChannels = new ArrayList<>();
        channelAdapter = new ChannelListAdapter(getContext(), searchChannels);

        recyclerVideos = (RecyclerView) v.findViewById(R.id.recyclerSearch);
        recyclerVideos.setLayoutManager(linearLayoutManager);
        recyclerVideos.setAdapter(channelAdapter);

        //Query via Youtube Data Api v3
        handler = new Handler();
        new Thread(){
            public void run(){
                service = new YouTubeService(getContext());
                ChannelListResponse response = service.searchChannels(query, nextPageToken);
                nextPageToken = response.getNextPageToken();

                for (Channel c : response.getItems())
                    searchChannels.add(c);

                handler.post(new Runnable() {
                    public void run() {

                        channelAdapter.notifyDataSetChanged();

                        v.findViewById(R.id.progressSearch).setVisibility(View.GONE);
                    }
                });
            }
        }.start();

        recyclerVideos.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(final RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = linearLayoutManager.getChildCount();
                    totalItemCount = linearLayoutManager.getItemCount();
                    pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            searchChannels.add(null);
                            channelAdapter.notifyItemInserted(searchChannels.size());
                            loading = false;

                            new Thread() {
                                public void run() {
                                    final ChannelListResponse response = service.searchChannels(query, nextPageToken);
                                    nextPageToken = response.getNextPageToken();

                                    handler.post(new Runnable() {
                                        public void run() {

                                            //Procedimiento para ocultar el progress
                                            searchChannels.remove(searchChannels.size() - 1);
                                            channelAdapter.notifyItemRemoved(searchChannels.size());

                                            final int prevLastPos = searchChannels.size() - 1;
                                            for (Channel c : response.getItems()) {
                                                searchChannels.add(c);
                                            }

                                            channelAdapter.notifyItemRangeInserted(prevLastPos, response.getItems().size());
                                            recyclerView.scrollToPosition(prevLastPos + 1);

                                            loading = nextPageToken != null;
                                        }
                                    });
                                }
                            }.start();
                        }
                    }
                }
            }
        });


        return v;
    }

}
