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

import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistListResponse;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.soft.redix.fasttube.R;
import com.soft.redix.fasttube.adapter.PlaylistAdapter;
import com.soft.redix.fasttube.util.YouTubeService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gonzalo on 29/3/2016.
 */
public class SearchPlaylistsFragment extends Fragment {

    private YouTubeService service;
    private RecyclerView recyclerVideos;
    private Handler handler;
    private String query;
    private List<Playlist> searchPlaylists;
    private PlaylistAdapter playlistAdapter;
    private LinearLayoutManager linearLayoutManager;

    private boolean loading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;

    private String nextPageToken;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_playlists, container, false);

        query = getArguments().getString("query");
        linearLayoutManager = new LinearLayoutManager(getContext());
        searchPlaylists = new ArrayList<>();
        playlistAdapter = new PlaylistAdapter(getContext(), searchPlaylists);

        recyclerVideos = (RecyclerView) v.findViewById(R.id.recyclerChannelPlaylist);
        recyclerVideos.setLayoutManager(linearLayoutManager);
        recyclerVideos.setAdapter(playlistAdapter);

        //Query via Youtube Data Api v3
        handler = new Handler();
        new Thread(){
            public void run(){
                service = new YouTubeService(getContext());
                PlaylistListResponse response = service.searchPlaylists(query, nextPageToken);
                nextPageToken = response.getNextPageToken();
                for (Playlist p : response.getItems())
                    searchPlaylists.add(p);
                handler.post(new Runnable() {
                    public void run() {

                        playlistAdapter.notifyDataSetChanged();

                        v.findViewById(R.id.progressChannelPlaylist).setVisibility(View.GONE);
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
                            searchPlaylists.add(null);
                            playlistAdapter.notifyItemInserted(searchPlaylists.size());
                            loading = false;

                            new Thread() {
                                public void run() {
                                    final PlaylistListResponse response = service.searchPlaylists(query, nextPageToken);
                                    nextPageToken = response.getNextPageToken();
                                    final int prevLastPos = searchPlaylists.size() - 1;

                                    handler.post(new Runnable() {
                                        public void run() {

                                            //Procedimiento para ocultar el progress
                                            searchPlaylists.remove(searchPlaylists.size() - 1);
                                            playlistAdapter.notifyItemRemoved(searchPlaylists.size());

                                            for (Playlist p : response.getItems()) {
                                                searchPlaylists.add(p);
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
