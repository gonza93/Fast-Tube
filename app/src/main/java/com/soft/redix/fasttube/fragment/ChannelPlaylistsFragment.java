package com.soft.redix.fasttube.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistListResponse;
import com.soft.redix.fasttube.R;
import com.soft.redix.fasttube.adapter.PlaylistAdapter;
import com.soft.redix.fasttube.adapter.ViewType;
import com.soft.redix.fasttube.util.YouTubeService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gonzalo on 20/3/2016.
 */
public class ChannelPlaylistsFragment extends Fragment {

    private RecyclerView recyclerPlaylists;
    private boolean loading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private PlaylistAdapter playlistAdapter;
    private LinearLayoutManager linearLayoutManager;
    private YouTubeService service;
    private List<Playlist> playlists;
    private String nextPageToken;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_playlists, container, false);

        final String channelId = getArguments().getString("channel_id");

        playlists = new ArrayList<>();
        playlistAdapter = new PlaylistAdapter(getContext(), playlists);
        linearLayoutManager = new LinearLayoutManager(getContext());

        recyclerPlaylists = (RecyclerView) v.findViewById(R.id.recyclerChannelPlaylist);
        recyclerPlaylists.setLayoutManager(linearLayoutManager);
        recyclerPlaylists.setAdapter(playlistAdapter);

        final Handler handler = new Handler();
        new Thread(){
            public void run(){

                service = new YouTubeService(getContext());
                PlaylistListResponse response = service.getChannelPlaylists(channelId, nextPageToken);
                nextPageToken = response.getNextPageToken();
                for(Playlist p : response.getItems())
                    playlists.add(p);

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        playlistAdapter.notifyDataSetChanged();
                        v.findViewById(R.id.progressChannelPlaylist).setVisibility(View.GONE);
                        if(nextPageToken == null)
                            loading = false;
                    }
                });
            }
        }.start();

        recyclerPlaylists.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(final RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = linearLayoutManager.getChildCount();
                    totalItemCount = linearLayoutManager.getItemCount();
                    pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            playlists.add(null);
                            playlistAdapter.notifyItemInserted(playlists.size());
                            loading = false;

                            new Thread() {
                                public void run() {
                                    final PlaylistListResponse response = service.getChannelPlaylists(channelId, nextPageToken);
                                    nextPageToken = response.getNextPageToken();
                                    final int prevLastPos = playlists.size() - 1;

                                    handler.post(new Runnable() {
                                        public void run() {

                                            //Remove progress
                                            playlists.remove(playlists.size() - 1);
                                            playlistAdapter.notifyItemRemoved(playlists.size());

                                            for (Playlist p : response.getItems()) {
                                                playlists.add(p);
                                            }

                                            playlistAdapter.notifyItemRangeInserted(prevLastPos, response.getItems().size());
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
