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
import android.widget.TextView;

import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.Thumbnail;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.neovisionaries.i18n.CountryCode;
import com.soft.redix.fasttube.R;
import com.soft.redix.fasttube.adapter.TrendHeaderAdapter;
import com.soft.redix.fasttube.adapter.TrendVideoAdapter;
import com.soft.redix.fasttube.adapter.ViewType;
import com.soft.redix.fasttube.util.AnimationUtil;
import com.soft.redix.fasttube.util.YouTubeService;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import su.j2e.rvjoiner.JoinableAdapter;
import su.j2e.rvjoiner.RvJoiner;

/**
 * Created by Gonzalo on 23/2/2016.
 */
public class HomeFragment extends Fragment {

    private YouTubeService service;
    private RecyclerView recyclerHome, recyclerCountry;
    private LinearLayoutManager linearLayoutManager, layoutManagerCountry;
    private TrendVideoAdapter videoAdapter;
    private TrendHeaderAdapter headerAdapter;

    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private AVLoadingIndicatorView progress;

    public static String nextPageTokenHome;
    public static boolean loading = true;
    public static List<Video> trendVideos;
    public static String regionCode;

    private View countryLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_home, container, false);

        progress = (AVLoadingIndicatorView) v.findViewById(R.id.progressHome);
        trendVideos = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(getContext());

        recyclerHome = (RecyclerView) v.findViewById(R.id.recyclerHome);

        //Seteo de region
        regionCode = Locale.getDefault().getCountry();
        //((TextView) v.findViewById(R.id.home_default_country_text)).setText(regionCode);

        int flagOffset = 0x1F1E6;
        int asciiOffset = 0x41;

        final String country = CountryCode.valueOf(regionCode).getAlpha2();

        int firstChar = Character.codePointAt(country, 0) - asciiOffset + flagOffset;
        int secondChar = Character.codePointAt(country, 1) - asciiOffset + flagOffset;

        String flag = new String(Character.toChars(firstChar)) + new String(Character.toChars(secondChar));
        //((TextView) v.findViewById(R.id.home_default_country_flag)).setText(flag);
        //Fin seteo de region

        videoAdapter = new TrendVideoAdapter(getContext(), trendVideos);

        recyclerHome.setLayoutManager(linearLayoutManager);
        recyclerHome.setAdapter(videoAdapter);

        nextPageTokenHome = null;

        //Prepare header (Country list)
        /*headerAdapter = new TrendHeaderAdapter(getContext());
        layoutManagerCountry = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        recyclerCountry = (RecyclerView) v.findViewById(R.id.recyclerCountry);
        recyclerCountry.setLayoutManager(layoutManagerCountry);
        recyclerCountry.setAdapter(headerAdapter);

        countryLayout = v.findViewById(R.id.fragment_home_layout_country);*/

        final Handler handler = new Handler();
        new Thread(){
            public void run(){
                service = new YouTubeService(getContext());
                VideoListResponse response = service.getTrendVideos(nextPageTokenHome, regionCode);
                nextPageTokenHome = response.getNextPageToken();
                String channelIds = "";
                for(Video v : response.getItems())
                    channelIds += "," + v.getSnippet().getChannelId();
                channelIds = channelIds.replaceFirst(",","");
                List<Channel> channels = service.getTrendChannelThumbs(channelIds);
                for(Video v : response.getItems()) {
                    for(Channel c : channels) {
                        if (c.getId().equals(v.getSnippet().getChannelId())) {
                            Thumbnail channelThumb = c.getSnippet().getThumbnails().getMedium();
                            v.getSnippet().getThumbnails().setDefault(channelThumb);
                            break;
                        }
                    }
                    trendVideos.add(v);
                }
                handler.post(new Runnable() {
                    public void run() {

                        videoAdapter.notifyDataSetChanged();
                        loading = nextPageTokenHome != null;
                        progress.hide();
                    }
                });
            }
        }.start();

        recyclerHome.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(final RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = linearLayoutManager.getChildCount();
                    totalItemCount = linearLayoutManager.getItemCount();
                    pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            trendVideos.add(null);
                            videoAdapter.notifyItemInserted(trendVideos.size());
                            loading = false;

                            new Thread() {
                                public void run() {
                                    //Trend Videos
                                    final VideoListResponse response = service.getTrendVideos(nextPageTokenHome, regionCode);
                                    nextPageTokenHome = response.getNextPageToken();
                                    final int prevLastPos = trendVideos.size() - 1;


                                    //Channels of trend videos
                                    String channelIds = "";
                                    for(Video v : response.getItems())
                                        channelIds += "," + v.getSnippet().getChannelId();
                                    channelIds = channelIds.replaceFirst(",", "");
                                    final List<Channel> channels = service.getTrendChannelThumbs(channelIds);


                                    handler.post(new Runnable() {
                                        public void run() {

                                            //Remove progress
                                            trendVideos.remove(trendVideos.size() - 1);
                                            videoAdapter.notifyItemRemoved(trendVideos.size());

                                            for(Video v : response.getItems()) {
                                                for(Channel c : channels) {
                                                    if (c.getId().equals(v.getSnippet().getChannelId())) {
                                                        Thumbnail channelThumb = c.getSnippet().getThumbnails().getMedium();
                                                        v.getSnippet().getThumbnails().setDefault(channelThumb);
                                                        break;
                                                    }
                                                }
                                                trendVideos.add(v);
                                            }

                                            videoAdapter.notifyItemRangeInserted(prevLastPos, response.getItems().size());
                                            recyclerView.scrollToPosition(prevLastPos);

                                            loading = nextPageTokenHome != null;
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
