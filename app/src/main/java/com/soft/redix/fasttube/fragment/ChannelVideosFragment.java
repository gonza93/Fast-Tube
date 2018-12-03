package com.soft.redix.fasttube.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.soft.redix.fasttube.R;
import com.soft.redix.fasttube.adapter.VideoAdapter;
import com.soft.redix.fasttube.adapter.ViewType;
import com.soft.redix.fasttube.util.YouTubeService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gonzalo on 20/3/2016.
 */
public class ChannelVideosFragment extends Fragment {

    RecyclerView recyclerChannelVideos;
    private boolean loading = true;
    private GridLayoutManager gridLayoutManager;
    private VideoAdapter videoAdapter;
    private List<Video> channelVideos;
    private String nextPageToken;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_channel_videos, container, false);

        final String channelId = getArguments().getString("channel_id");

        channelVideos = new ArrayList<>();
        gridLayoutManager = new GridLayoutManager(getContext(), 2);
        videoAdapter = new VideoAdapter(getContext(), channelVideos);

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (videoAdapter.getItemViewType(position)){
                    case ViewType.ITEM:
                        return 1;
                    case ViewType.PROG:
                        return 2;
                    default:
                        return -1;
                }
            }
        });

        recyclerChannelVideos = (RecyclerView) v.findViewById(R.id.recyclerChannelVideos);
        recyclerChannelVideos.setLayoutManager(gridLayoutManager);
        recyclerChannelVideos.setAdapter(videoAdapter);

        final Handler handler = new Handler();
        new Thread(){
            public void run(){
                final YouTubeService service = new YouTubeService(getContext());
                VideoListResponse response = service.getChannelVideos(channelId, nextPageToken);
                if(response == null){
                    v.findViewById(R.id.error_message).setVisibility(View.VISIBLE);
                    return;
                }
                for(Video v: response.getItems())
                    channelVideos.add(v);
                nextPageToken = response.getNextPageToken();
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        videoAdapter.notifyDataSetChanged();

                        v.findViewById(R.id.progressChannelVideos).setVisibility(View.GONE);
                        if(nextPageToken == null)
                            loading = false;
                    }
                });
            }
        }.start();

        recyclerChannelVideos.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(final RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = gridLayoutManager.getChildCount();
                    totalItemCount = gridLayoutManager.getItemCount();
                    pastVisiblesItems = gridLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            channelVideos.add(null);
                            videoAdapter.notifyItemInserted(channelVideos.size());
                            loading = false;

                            new Thread() {
                                public void run() {
                                    YouTubeService service = new YouTubeService(getContext());
                                    final VideoListResponse response = service.getChannelVideos(channelId, nextPageToken);

                                    nextPageToken = response.getNextPageToken();
                                    handler.post(new Runnable() {
                                        public void run() {

                                            channelVideos.remove(channelVideos.size() - 1);
                                            videoAdapter.notifyItemRemoved(channelVideos.size());

                                            final int prevLastPos = channelVideos.size() - 1;
                                            for(Video v : response.getItems())
                                                channelVideos.add(v);

                                            videoAdapter.notifyItemRangeInserted(prevLastPos, response.getItems().size());
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
