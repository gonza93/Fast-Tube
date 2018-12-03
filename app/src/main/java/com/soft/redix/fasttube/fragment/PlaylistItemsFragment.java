package com.soft.redix.fasttube.fragment;

import android.content.ContentValues;
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
import com.google.api.services.youtube.model.VideoListResponse;
import com.soft.redix.fasttube.R;
import com.soft.redix.fasttube.adapter.PlaylistAdapter;
import com.soft.redix.fasttube.adapter.PlaylistItemsAdapter;
import com.soft.redix.fasttube.adapter.PlaylistItemsHeaderAdapter;
import com.soft.redix.fasttube.adapter.RelatedAdapter;
import com.soft.redix.fasttube.util.AnimationUtil;
import com.soft.redix.fasttube.util.YouTubeService;

import java.util.ArrayList;
import java.util.List;

import su.j2e.rvjoiner.JoinableAdapter;
import su.j2e.rvjoiner.RvJoiner;

/**
 * Created by Gonzalo on 25/3/2016.
 */
public class PlaylistItemsFragment extends Fragment {

    private RecyclerView recyclerPlaylistItems;
    private RvJoiner joiner;
    private LinearLayoutManager linearLayoutManager;
    private PlaylistItemsAdapter playlistItemsAdapter;
    private List<Video> playlistItems;
    private String nextPageToken;
    private YouTubeService service;
    private boolean loading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;

    private ContentValues values;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_playlist_items, container, false);

        values = new ContentValues();

        values.put("playlistId", getArguments().getString("playlist_id"));
        values.put("playlistTitle", getArguments().getString("playlist_title"));
        values.put("channelTitle", getArguments().getString("playlist_channel_title"));
        values.put("videoCount", getArguments().getString("video_count"));
        values.put("playlistThumb", getArguments().getString("playlist_thumb"));

        service = new YouTubeService(getContext());

        joiner = new RvJoiner();
        playlistItems = new ArrayList<>();

        linearLayoutManager = new LinearLayoutManager(getContext());
        playlistItemsAdapter = new PlaylistItemsAdapter(getContext(), playlistItems, values);

        //Espacio para el header
        playlistItems.add(new Video());

        recyclerPlaylistItems = (RecyclerView) v.findViewById(R.id.recyclerPlaylistItems);
        recyclerPlaylistItems.setLayoutManager(linearLayoutManager);
        recyclerPlaylistItems.setAdapter(playlistItemsAdapter);

        final Handler handler = new Handler();
        new Thread(){
            public void run(){
                VideoListResponse response = service.getPlaylistItems(values.getAsString("playlistId"), nextPageToken);
                for(Video v : response.getItems())
                    playlistItems.add(v);
                nextPageToken = response.getNextPageToken();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        playlistItemsAdapter.notifyDataSetChanged();
                        v.findViewById(R.id.progressPlaylistItems).setVisibility(View.GONE);
                        if(nextPageToken == null)
                            loading = false;

                        AnimationUtil.enterReveal(v.findViewById(R.id.playlistItemsFab));

                    }
                });
            }
        }.start();

        recyclerPlaylistItems.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(final RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = linearLayoutManager.getChildCount();
                    totalItemCount = linearLayoutManager.getItemCount();
                    pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            playlistItems.add(null);
                            playlistItemsAdapter.notifyItemInserted(playlistItems.size());
                            loading = false;

                            new Thread() {
                                public void run() {
                                    final VideoListResponse response = service.getPlaylistItems(values.getAsString("playlistId"), nextPageToken);
                                    nextPageToken = response.getNextPageToken();
                                    final int prevLastPos = playlistItems.size() - 1;

                                    handler.post(new Runnable() {
                                        public void run() {

                                            //Remove progress
                                            playlistItems.remove(playlistItems.size() - 1);
                                            playlistItemsAdapter.notifyItemRemoved(playlistItems.size());

                                            for (Video v : response.getItems()) {
                                                playlistItems.add(v);
                                            }

                                            playlistItemsAdapter.notifyItemRangeInserted(prevLastPos, response.getItems().size());
                                            recyclerView.scrollToPosition(prevLastPos + 2);

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
