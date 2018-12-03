package com.soft.redix.fasttube.util;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.PlaylistListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gonzalo Cham on 24/2/2016.
 */
public class YouTubeService {

    final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    final JsonFactory jsonFactory = new JacksonFactory();

    public static YouTube youtube;
    private YouTube.Search.List searchQuery;
    private YouTube.Videos.List videosQuery;
    private YouTube.Channels.List channelsQuery;
    private YouTube.Playlists.List playlistsQuery;
    private YouTube.PlaylistItems.List playlistItemsQuery;
    private Context context;


    public YouTubeService(Context context) {
        this.context = context;
        youtube = new YouTube.Builder(transport, jsonFactory, new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest httpRequest) throws IOException {

            }
        })      .setApplicationName("Power Tube")
                .build();
    }

    public VideoListResponse searchVideos(String query, String pageToken) {
        try{
            searchQuery = youtube.search().list("id");
            searchQuery.setKey(DeveloperKey.KEY);
            searchQuery.setType("video");
            searchQuery.setMaxResults((long) 16);
            searchQuery.setFields("nextPageToken,items(id/videoId)");
            searchQuery.setPageToken(pageToken);
        }catch(IOException e){
            Log.d("YS", "Could not initialize: " + e);
        }

        searchQuery.setQ(query);
        try {
            SearchListResponse response = searchQuery.execute();
            List<SearchResult> results = response.getItems();

            List<String> ids = new ArrayList<>();
            for (SearchResult result : results) {
                ids.add(result.getId().getVideoId());
            }

            //Inicializar query para videos
            videosQuery = youtube.videos().list("snippet,contentDetails,statistics");
            videosQuery.setKey(DeveloperKey.KEY);
            videosQuery.setFields("items(id,snippet,contentDetails/duration,statistics)");

            String id = "";
            for(String videoId : ids){
                id += "," + videoId;
            }

            id = id.replaceFirst(",","");
            videosQuery.setId(id);
            VideoListResponse videoResponse = videosQuery.execute();
            videoResponse.setNextPageToken(response.getNextPageToken());
            return videoResponse;

        } catch (IOException e) {
            Log.d("YS", "Could not search: " + e);
            return null;
        }
    }

    public ChannelListResponse searchChannels(String query, String pageToken){
        try {
            searchQuery = youtube.search().list("snippet");
            searchQuery.setQ(query);
            searchQuery.setType("channel");
            searchQuery.setMaxResults((long) 10);
            searchQuery.setKey(DeveloperKey.KEY);
            searchQuery.setFields("nextPageToken,items(id/channelId)");
            searchQuery.setPageToken(pageToken);

            SearchListResponse response = searchQuery.execute();

            List<String> ids = new ArrayList<>();
            for (SearchResult result : response.getItems()) {
                ids.add(result.getId().getChannelId());
            }

            String id = "";
            for(String channelId : ids){
                id += "," + channelId;
            }

            channelsQuery = youtube.channels().list("snippet,contentDetails,statistics,brandingSettings");
            channelsQuery.setKey(DeveloperKey.KEY);
            channelsQuery.setFields("items(id,snippet,contentDetails/relatedPlaylists/likes,contentDetails/relatedPlaylists/uploads" +
                    ",statistics,brandingSettings/image/bannerImageUrl,brandingSettings/image/bannerMobileImageUrl)");
            channelsQuery.setId(id);

            ChannelListResponse channelResponse = channelsQuery.execute();
            channelResponse.setNextPageToken(response.getNextPageToken());

            return channelResponse;

        }catch (IOException e){
            Log.d("YS", "Error: " + e);
            return null;
        }
    }

    public PlaylistListResponse searchPlaylists(String query, String pageToken){
        try {
            searchQuery = youtube.search().list("snippet");
            searchQuery.setQ(query);
            searchQuery.setType("playlist");
            searchQuery.setMaxResults((long) 12);
            searchQuery.setKey(DeveloperKey.KEY);
            searchQuery.setFields("items(id/playlistId)");
            searchQuery.setPageToken(pageToken);

            SearchListResponse response = searchQuery.execute();

            List<String> ids = new ArrayList<>();
            for (SearchResult result : response.getItems()) {
                ids.add(result.getId().getPlaylistId());
            }

            String id = "";
            for(String playlistId : ids){
                id += "," + playlistId;
            }

            playlistsQuery = youtube.playlists().list("id,snippet,contentDetails");
            playlistsQuery.setKey(DeveloperKey.KEY);
            playlistsQuery.setFields("items(id,snippet,contentDetails)");
            playlistsQuery.setId(id);
            playlistsQuery.setMaxResults((long) 8);

            PlaylistListResponse responsePlaylist = playlistsQuery.execute();
            responsePlaylist.setNextPageToken(response.getNextPageToken());

            return responsePlaylist;



        }catch (IOException e){
            Log.d("YS", "Error: " + e);
            return null;
        }
    }

    public List<Video> getRelatedVideos(String relatedId){
        try {
            searchQuery = youtube.search().list("id");
            searchQuery.setKey(DeveloperKey.KEY);
            searchQuery.setMaxResults((long) 14);
            searchQuery.setRelatedToVideoId(relatedId);
            searchQuery.setType("video");
            searchQuery.setFields("items(id/videoId)");

            SearchListResponse response = searchQuery.execute();
            List<SearchResult> results = response.getItems();

            List<String> ids = new ArrayList<>();
            for (SearchResult result : results) {
                ids.add(result.getId().getVideoId());
            }

            //Inicializar query para videos
            videosQuery = youtube.videos().list("snippet,contentDetails,statistics");
            videosQuery.setKey(DeveloperKey.KEY);
            videosQuery.setFields("items(id,snippet,contentDetails/duration,statistics)");

            String id = "";
            for(String videoId : ids){
                id += "," + videoId;
            }

            id = id.replaceFirst(",","");
            videosQuery.setId(id);
            VideoListResponse videoResponse = videosQuery.execute();

            return videoResponse.getItems();

        }catch (IOException e){
            Log.d("YS", "Could not get: " + e);
            return null;
        }
    }

    public List<Channel> getChannels(String id){
        try{
            channelsQuery = youtube.channels().list("snippet,contentDetails,statistics,brandingSettings");
            channelsQuery.setKey(DeveloperKey.KEY);
            channelsQuery.setFields("items(id,snippet,contentDetails/relatedPlaylists/likes,contentDetails/relatedPlaylists/uploads" +
                    ",statistics,brandingSettings/image/bannerImageUrl,brandingSettings/image/bannerMobileImageUrl)");
            channelsQuery.setId(id);
        }catch(IOException e){
            Log.d("YS", "Could not initialize: " + e);
        }

        try {
            ChannelListResponse response = channelsQuery.execute();

            return response.getItems();

        } catch (IOException e) {
            Log.d("YS", "Could not get: " + e);
            return null;


        }
    }

    public VideoListResponse getTrendVideos(String nextPageToken, String regionCode){
        try{
            videosQuery = youtube.videos().list("snippet,contentDetails,statistics");
            videosQuery.setKey(DeveloperKey.KEY);
            videosQuery.setFields("nextPageToken,items(id,snippet,contentDetails/duration,statistics)");
            videosQuery.setChart("mostPopular");
            videosQuery.setMaxResults((long) 16);
            videosQuery.setRegionCode(regionCode);
            videosQuery.setPageToken(nextPageToken);

            VideoListResponse response = videosQuery.execute();

            return response;

        }catch (IOException e){
            Log.d("YS", "Error: " + e);
            return null;
        }
    }

    public List<Channel> getTrendChannelThumbs(String channelIds){
        try{
            channelsQuery = youtube.channels().list("snippet");
            channelsQuery.setKey(DeveloperKey.KEY);
            channelsQuery.setFields("items(id,snippet/thumbnails)");
            channelsQuery.setId(channelIds);

            ChannelListResponse response = channelsQuery.execute();

            return response.getItems();
        }catch(IOException e){
            Log.d("YS", "Could not initialize: " + e);
            return null;
        }
    }

    public List<Video> getChannelMostPopularVideos(String channelId){
        try {
            searchQuery = youtube.search().list("snippet");
            searchQuery.setChannelId(channelId);
            searchQuery.setKey(DeveloperKey.KEY);
            searchQuery.setMaxResults((long) 12);
            searchQuery.setFields("items(id/videoId)");
            searchQuery.setType("video");
            searchQuery.setOrder("viewCount");

            SearchListResponse response = searchQuery.execute();

            List<String> ids = new ArrayList<>();
            for (SearchResult result : response.getItems()) {
                ids.add(result.getId().getVideoId());
            }

            //Inicializar query para videos
            videosQuery = youtube.videos().list("snippet,contentDetails,statistics");
            videosQuery.setKey(DeveloperKey.KEY);
            videosQuery.setFields("items(id,snippet,contentDetails/duration,statistics)");

            String id = "";
            for(String videoId : ids){
                id += "," + videoId;
            }

            id = id.replaceFirst(",","");
            videosQuery.setId(id);
            VideoListResponse videoResponse = videosQuery.execute();

            return videoResponse.getItems();

        }catch (IOException e){
            Log.d("YS", "Error: " + e);
            return null;
        }
    }

    public List<Video> getChannelLikedVideos(String playlistId){
        try{

            playlistItemsQuery = youtube.playlistItems().list("snippet");
            playlistItemsQuery.setKey(DeveloperKey.KEY);
            playlistItemsQuery.setPlaylistId(playlistId);
            playlistItemsQuery.setMaxResults((long) 10);
            playlistItemsQuery.setFields("items(snippet/resourceId/videoId)");

            PlaylistItemListResponse playlistResp = playlistItemsQuery.execute();

            if(playlistResp.getItems().isEmpty())
                return null;

            List<String> ids = new ArrayList<>();
            for (PlaylistItem playlistItem : playlistResp.getItems())
                ids.add(playlistItem.getSnippet().getResourceId().getVideoId());

            String id = "";
            for (String videoId : ids )
                id += "," + videoId;

            id = id.replaceFirst(",","");
            videosQuery.setId(id);
            VideoListResponse videoResponse = videosQuery.execute();

            return videoResponse.getItems();

        } catch (IOException e2){
            Log.d("Y2", "Error: " + e2);
            return null;
        }
    }

    public VideoListResponse getChannelVideos(String channelId, String nextPageToken){
        try {
            searchQuery = youtube.search().list("snippet");
            searchQuery.setChannelId(channelId);
            searchQuery.setKey(DeveloperKey.KEY);
            searchQuery.setMaxResults((long) 12);
            searchQuery.setType("video");
            searchQuery.setOrder("date");
            searchQuery.setPageToken(nextPageToken);

            SearchListResponse response = searchQuery.execute();

            List<String> ids = new ArrayList<>();
            for (SearchResult result : response.getItems()) {
                ids.add(result.getId().getVideoId());
            }

            //Inicializar query para videos
            videosQuery = youtube.videos().list("snippet,contentDetails,statistics");
            videosQuery.setKey(DeveloperKey.KEY);
            videosQuery.setFields("items(id,snippet,contentDetails/duration,statistics)");

            String id = "";
            for(String videoId : ids){
                id += "," + videoId;
            }

            id = id.replaceFirst(",","");
            videosQuery.setId(id);
            VideoListResponse videoResponse = videosQuery.execute();
            videoResponse.setNextPageToken(response.getNextPageToken());

            return videoResponse;

        }catch (IOException e){
            Log.d("YS", "Error: " + e);
            return null;
        }
    }

    public PlaylistListResponse getChannelPlaylists(String channelId, String nextPageToken){
        try {
            playlistsQuery = youtube.playlists().list("id,snippet,contentDetails");
            playlistsQuery.setKey(DeveloperKey.KEY);
            playlistsQuery.setFields("nextPageToken,items(id,snippet,contentDetails)");
            playlistsQuery.setChannelId(channelId);
            playlistsQuery.setMaxResults((long) 8);
            playlistsQuery.setPageToken(nextPageToken);

            PlaylistListResponse response = playlistsQuery.execute();

            return response;

        }catch (IOException e){
            Log.d("YS", "Error: " + e);
            return null;
        }
    }

    public VideoListResponse getPlaylistItems(String playlistId, String nextPageToken){
        try{
            playlistItemsQuery = youtube.playlistItems().list("snippet");
            playlistItemsQuery.setPlaylistId(playlistId);
            playlistItemsQuery.setKey(DeveloperKey.KEY);
            playlistItemsQuery.setMaxResults((long) 12);
            playlistItemsQuery.setFields("nextPageToken,items(snippet/resourceId/videoId)");
            playlistItemsQuery.setPageToken(nextPageToken);

            PlaylistItemListResponse playlistItemListResponse = playlistItemsQuery.execute();

            List<String> ids = new ArrayList<>();
            for (PlaylistItem item : playlistItemListResponse.getItems()){
                ids.add(item.getSnippet().getResourceId().getVideoId());
            }

            String id = "";
            for(String videoId : ids){
                id += "," + videoId;
            }

            id = id.replaceFirst(",","");

            videosQuery = youtube.videos().list("snippet,contentDetails,statistics");
            videosQuery.setKey(DeveloperKey.KEY);
            videosQuery.setFields("items(id,snippet,contentDetails/duration,statistics)");
            videosQuery.setId(id);

            VideoListResponse response = videosQuery.execute();
            response.setNextPageToken(playlistItemListResponse.getNextPageToken());

            return response;

        }catch (IOException e){
            Log.d("YS", "Error : " + e);
            return null;
        }
    }

    public List<Video> getVideo(String videoId){
        try{
            videosQuery = youtube.videos().list("snippet,contentDetails,statistics");
            videosQuery.setKey(DeveloperKey.KEY);
            videosQuery.setFields("items(id,snippet,contentDetails/duration,statistics)");
            videosQuery.setId(videoId);
            VideoListResponse videoResponse = videosQuery.execute();

            return videoResponse.getItems();
        }catch (IOException e){
            Log.d("YS", "Error: " + e);
            return null;
        }
    }

    public VideoListResponse getLocationVideos(String location, String radius){

        try{
            searchQuery = youtube.search().list("snippet");
            searchQuery.setType("video");
            searchQuery.setMaxResults((long) 20);
            searchQuery.setKey(DeveloperKey.KEY);
            searchQuery.setFields("nextPageToken,items(id/videoId)");
            searchQuery.setLocation(location);
            searchQuery.setLocationRadius(radius + "m");

            SearchListResponse searchListResponse = searchQuery.execute();

            List<String> ids = new ArrayList<>();
            for (SearchResult result : searchListResponse.getItems()) {
                ids.add(result.getId().getVideoId());
            }

            String id = "";
            for(String videoId : ids){
                id += "," + videoId;
            }
            id = id.replaceFirst(",","");

            videosQuery = youtube.videos().list("snippet,contentDetails,statistics");
            videosQuery.setKey(DeveloperKey.KEY);
            videosQuery.setFields("items(id,snippet,contentDetails/duration,statistics)");
            videosQuery.setId(id);

            return videosQuery.execute();

        }catch (IOException e){
            Log.d("YS", "Error: " + e);
            return null;
        }

    }

    public void getSearchSuggestions(String query, final AsyncResult result){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        String url ="http://suggestqueries.google.com/complete/search?client=firefox&ds=yt&q=" + query;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            List<String> suggestions = new ArrayList<>();
                            JSONArray jsonSuggestions = new JSONArray(response).getJSONArray(1);
                            for (int i = 0; i < jsonSuggestions.length(); i++)
                                suggestions.add(jsonSuggestions.getString(i));

                            result.onResult(suggestions);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

}