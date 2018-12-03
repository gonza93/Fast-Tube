package com.soft.redix.fasttube.adapter;

import android.content.Context;
import android.os.Handler;
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
import com.soft.redix.fasttube.fragment.HomeFragment;
import com.soft.redix.fasttube.util.AnimationUtil;
import com.soft.redix.fasttube.util.YouTubeService;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gonzalo on 24/8/17.
 */

public class TrendHeaderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements ItemClickListener {

    private final Context context;
    private CountryCode[] countryList = CountryCode.values();
    private YouTubeService service;
    private View recycler;


    public static class SimpleViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        // Campos respectivos de un item
        public TextView flag;
        public ItemClickListener listener;

        public SimpleViewHolder(View v, ItemClickListener listener) {
            super(v);
            flag = (TextView) v.findViewById(R.id.countryFlag);
            this.listener = listener;
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(v, getAdapterPosition());
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recycler = recyclerView;
    }

    public TrendHeaderAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return countryList.length;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_country, viewGroup, false);
        return new SimpleViewHolder(v, this);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        SimpleViewHolder holder = (SimpleViewHolder) viewHolder;
        int flagOffset = 0x1F1E6;
        int asciiOffset = 0x41;

        String country = countryList[position].getAlpha2();

        int firstChar = Character.codePointAt(country, 0) - asciiOffset + flagOffset;
        int secondChar = Character.codePointAt(country, 1) - asciiOffset + flagOffset;

        String flag = new String(Character.toChars(firstChar)) + new String(Character.toChars(secondChar));
         holder.flag.setText(flag);
    }

    /**
     * Sobrescritura del método de la interfaz {@link ItemClickListener}
     *
     * @param view     item actual
     * @param position posición del item actual
     */
    @Override
    public void onItemClick(View view, final int position) {
        HomeFragment.nextPageTokenHome = null;
        final Handler handler = new Handler();

        final String selectedCountry = countryList[position].getAlpha2();
        HomeFragment.regionCode = selectedCountry;

        View parent = (View) recycler.getParent().getParent().getParent();
        RecyclerView recyclerTrendVideos = (RecyclerView) parent.findViewById(R.id.recyclerHome);

        final TrendVideoAdapter trendVideoAdapter = (TrendVideoAdapter) recyclerTrendVideos.getAdapter();

        final AVLoadingIndicatorView progress = (AVLoadingIndicatorView) parent.findViewById(R.id.progressHome);
        progress.show();

        HomeFragment.trendVideos.clear();
        trendVideoAdapter.notifyDataSetChanged();

        final View errorView = parent.findViewById(R.id.error_message);
        errorView.setVisibility(View.GONE);

        TextView selectedFlag = (TextView) view;
        /*View defaultFlag = parent.findViewById(R.id.home_default_country_flag);
        View defaultCountry = parent.findViewById(R.id.home_default_country_text);
        defaultFlag.setVisibility(View.INVISIBLE);
        defaultCountry.setVisibility(View.INVISIBLE);
        ((TextView) defaultFlag).setText(selectedFlag.getText());
        ((TextView) defaultCountry).setText(selectedCountry);

        TextView flagAnim = (TextView) parent.findViewById(R.id.countryFlagAnim);
        flagAnim.setText(selectedFlag.getText());

        int[] location = new int[2];
        view.getLocationOnScreen(location);
        flagAnim.setTranslationX(location[0]);
        flagAnim.setTranslationY(view.getY());

        AnimationUtil.translateView(flagAnim, defaultFlag, defaultCountry, defaultFlag, 500);

        new Thread(){
            public void run(){
                service = new YouTubeService(context);
                final VideoListResponse response = service.getTrendVideos(null, selectedCountry);
                if(response != null) {
                    HomeFragment.nextPageTokenHome = response.getNextPageToken();
                    String channelIds = "";
                    for (Video v : response.getItems())
                        channelIds += "," + v.getSnippet().getChannelId();
                    channelIds = channelIds.replaceFirst(",", "");
                    List<Channel> channels = service.getTrendChannelThumbs(channelIds);
                    for (Video v : response.getItems()) {
                        for (Channel c : channels) {
                            if (c.getId().equals(v.getSnippet().getChannelId())) {
                                Thumbnail channelThumb = c.getSnippet().getThumbnails().getDefault();
                                try {
                                    v.getSnippet().getThumbnails().setDefault(channelThumb);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            }
                        }
                        HomeFragment.trendVideos.add(v);
                    }
                }

                handler.post(new Runnable() {
                    public void run() {

                        trendVideoAdapter.notifyDataSetChanged();
                        HomeFragment.loading = HomeFragment.nextPageTokenHome != null;
                        if(response == null){
                            errorView.setVisibility(View.VISIBLE);
                            progress.hide();
                        }
                        else{
                            errorView.setVisibility(View.GONE);
                            progress.hide();
                        }
                    }
                });
            }
        }.start();*/
    }
}