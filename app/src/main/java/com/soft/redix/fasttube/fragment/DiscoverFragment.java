package com.soft.redix.fasttube.fragment;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.soft.redix.fasttube.R;
import com.soft.redix.fasttube.activity.MainActivity;
import com.soft.redix.fasttube.adapter.LocationVideoAdapter;
import com.soft.redix.fasttube.adapter.LocationVideoHeaderAdapter;
import com.soft.redix.fasttube.util.AnimationUtil;
import com.soft.redix.fasttube.util.DiscoverDialog;
import com.soft.redix.fasttube.util.SettingsHelper;
import com.soft.redix.fasttube.util.YouTubeService;

import java.util.ArrayList;
import java.util.List;

import su.j2e.rvjoiner.JoinableAdapter;
import su.j2e.rvjoiner.RvJoiner;

/**
 * Created by Gonzalo on 23/2/2016.
 */
public class DiscoverFragment extends Fragment implements   GoogleMap.OnMarkerClickListener,
                                                            GoogleMap.OnMapClickListener,
                                                            OnMapReadyCallback,
                                                            View.OnClickListener {

    private MapView mMapView;
    private GoogleMap googleMap;
    private Marker currentMarker;
    private Circle currentCircle;
    private int strokeColor, shadeColor;
    private YouTubeService service;
    private List<Video> locationVideos;
    private RecyclerView recyclerLocationVideos;

    private View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_discover, container,
                false);
        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();

        mMapView.getMapAsync(this);

        /*AdView mAdView = (AdView) v.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/

        boolean dontShow = SettingsHelper.getDiscoverInfoSetting(getContext());
        if(dontShow)
            v.findViewById(R.id.discoverInfoLayout).setVisibility(View.GONE);

        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        strokeColor = 0xffff0000;
        shadeColor = 0x44ff0000;

        locationVideos = new ArrayList<>();
        /*recyclerLocationVideos = (RecyclerView) getActivity().findViewById(R.id.recyclerVideoInfo);
        recyclerLocationVideos.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerLocationVideos.setAdapter(new LocationVideoAdapter(getContext(), new ArrayList<Video>()));*/

        ImageView parameter = (ImageView) v.findViewById(R.id.discoverParameters);
        parameter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DiscoverDialog discoverDialog = new DiscoverDialog();
                Bundle args = new Bundle();
                args.putString("radius", MainActivity.radiusDiscover);
                discoverDialog.setArguments(args);
                discoverDialog.show(getActivity().getSupportFragmentManager(), "DiscoverDialog");
            }
        });

        try {
            MapsInitializer.initialize(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnMapClickListener(this);

        v.findViewById(R.id.discoverCloseDesc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AnimationUtil.hideReveal(v.findViewById(R.id.discoverInfoLayout));
                SettingsHelper.dontShowDiscoverInfo(getContext());
            }
        });
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (currentMarker != null) {
            currentMarker.remove();
            currentCircle.remove();
        }
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(200, 50, conf);
        currentMarker = googleMap.addMarker(
                new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromBitmap(bmp)));

        CircleOptions circleOptions = new CircleOptions()
                .center(latLng)
                .radius(Double.parseDouble(MainActivity.radiusDiscover))
                .fillColor(shadeColor)
                .strokeColor(strokeColor)
                .strokeWidth(2);

        currentCircle = googleMap.addCircle(circleOptions);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng).zoom(getProperZoom()).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        /*final DraggableView draggableView = (DraggableView) getActivity().findViewById(R.id.draggable_view);
        draggableView.closeToLeft();

        locationVideos = new ArrayList<>();
        final String location = String.valueOf(latLng.latitude) + "," + String.valueOf(latLng.longitude);
        getActivity().findViewById(R.id.progressLocation).setVisibility(View.VISIBLE);


        final Handler handler = new Handler();
        new Thread(){
            public void run(){

                service = new YouTubeService(getContext());
                final VideoListResponse response = service.getLocationVideos(location, MainActivity.radiusDiscover);
                for (Video v : response.getItems())
                    locationVideos.add(v);

                handler.post(new Runnable() {
                    public void run() {

                        if(locationVideos.size() == 0){
                            Toast.makeText(getContext(), R.string.location_warn, Toast.LENGTH_SHORT).show();
                            getActivity().findViewById(R.id.progressLocation).setVisibility(View.GONE);
                        }
                        else {
                            MainActivity.VideoFragment videoFragment =
                                    (MainActivity.VideoFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.video_fragment_container);
                            videoFragment.setVideoId(locationVideos.get(0).getId());
                            locationVideos.remove(0);

                            RvJoiner rvJoiner = new RvJoiner();
                            LocationVideoAdapter newAdapter = new LocationVideoAdapter(getContext(), locationVideos);
                            LocationVideoHeaderAdapter headerAdapter = new LocationVideoHeaderAdapter(getContext());

                            JoinableAdapter joinableLayout = new JoinableAdapter(headerAdapter);
                            JoinableAdapter joinableAdapter = new JoinableAdapter(newAdapter);

                            rvJoiner.add(joinableLayout);
                            rvJoiner.add(joinableAdapter);

                            recyclerLocationVideos.setAdapter(rvJoiner.getAdapter());

                            getActivity().findViewById(R.id.progressLocation).setVisibility(View.GONE);
                            draggableView.setVisibility(View.VISIBLE);
                            draggableView.setHorizontalAlphaEffectEnabled(false);
                            draggableView.setClickToMinimizeEnabled(false);
                            draggableView.minimize();
                        }
                    }
                });
            }
        }.start();*/
    }

    private float getProperZoom(){
        int radius = Integer.parseInt(MainActivity.radiusDiscover);
        if(radius < 1000)
            return 13;
        if(radius < 3000)
            return 12;
        if(radius < 5000)
            return 11;
        if (radius < 12000)
            return 10;
        if(radius <= 20000)
            return 9;
        else
            return 10;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onClick(View view) {

    }
}
