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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.soft.redix.fasttube.R;
import com.soft.redix.fasttube.activity.MainActivity;
import com.soft.redix.fasttube.adapter.VideoAdapter;
import com.soft.redix.fasttube.adapter.ViewType;
import com.soft.redix.fasttube.util.YouTubeService;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gonzalo on 23/2/2016.
 */
public class SearchVideosFragment extends Fragment {

    private YouTubeService service;
    private RecyclerView recyclerVideos;
    private GridLayoutManager lManager;
    private Handler handler;
    private String query;
    private List<Video> searchVideos;
    private String nextPageToken;
    private VideoAdapter videoAdapter;
    private boolean loading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;

    private AVLoadingIndicatorView progress;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_search_videos, container, false);

        progress = (AVLoadingIndicatorView) v.findViewById(R.id.progressSearch);
        progress.show();

        AdView mAdView = (AdView) v.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        if(getArguments() != null)
            query = getArguments().getString("query");

        if(query.equals("")) {
            progress.hide();
            return v;
        }

        searchVideos = new ArrayList<>();
        videoAdapter = new VideoAdapter(getContext(), searchVideos);
        nextPageToken = "";

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

        recyclerVideos = (RecyclerView) v.findViewById(R.id.recyclerSearch);
        recyclerVideos.setLayoutManager(gridLayoutManager);
        recyclerVideos.setAdapter(videoAdapter);

        //Query via Youtube Data Api v3
        handler = new Handler();
        new Thread(){
            public void run(){
                service = new YouTubeService(getContext());
                final VideoListResponse response = service.searchVideos(query, nextPageToken);
                if(response == null) {
                    progress.hide();
                    return;
                }
                nextPageToken = response.getNextPageToken();
                for (Video v : response.getItems())
                    searchVideos.add(v);
                handler.post(new Runnable() {
                    public void run() {

                        videoAdapter.notifyDataSetChanged();

                        if(nextPageToken == null)
                            loading = false;

                        progress.hide();
                    }
                });
            }
        }.start();


        recyclerVideos.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(final RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = gridLayoutManager.getChildCount();
                    totalItemCount = gridLayoutManager.getItemCount();
                    pastVisiblesItems = gridLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            searchVideos.add(null);
                            videoAdapter.notifyItemInserted(searchVideos.size());
                            loading = false;

                            new Thread() {
                                public void run() {
                                    final VideoListResponse response = service.searchVideos(query, nextPageToken);
                                    nextPageToken = response.getNextPageToken();

                                    handler.post(new Runnable() {
                                        public void run() {

                                            //Procedimiento para ocultar el progress
                                            searchVideos.remove(searchVideos.size() - 1);
                                            videoAdapter.notifyItemRemoved(searchVideos.size());

                                            final int prevLastPos = searchVideos.size() - 1;
                                            searchVideos.addAll(response.getItems());

                                            //videoAdapter.notifyItemRangeInserted(prevLastPos, response.getItems().size());
                                            videoAdapter.notifyDataSetChanged();
                                            //recyclerView.scrollToPosition(prevLastPos + 1);

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
