package com.soft.redix.fasttube.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.soft.redix.fasttube.R;
import com.soft.redix.fasttube.util.DeveloperKey;

/**
 * Created by Gonzalo on 30/3/2016.
 */
public class FullscreenActivity extends AppCompatActivity implements YouTubePlayer.OnFullscreenListener {

    private String id;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        getLayoutInflater().setFactory(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);
        id = getIntent().getStringExtra("video_id");
        final int currentTime = getIntent().getIntExtra("current_time", 0);
        final VideoFragment videoFragment = (VideoFragment) getSupportFragmentManager().findFragmentById(R.id.fullscreen_fragment);
        videoFragment.setVideoId(id, currentTime);
        final Handler handler = new Handler();
        new Thread(){
            public void run(){
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        videoFragment.seek(currentTime);
                        videoFragment.play();
                    }
                }, 2000);
            }
        }.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFullscreen(boolean b) {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            finish();
        }
    }

    public static final class VideoFragment extends YouTubePlayerSupportFragment
            implements YouTubePlayer.OnInitializedListener {

        private YouTubePlayer player;
        private String videoId;
        private int currentTime;

        public static VideoFragment newInstance() {
            return new VideoFragment();
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            initialize(DeveloperKey.KEY, this);
        }

        @Override
        public void onDestroy() {
            if (player != null) {
                player.release();
            }
            super.onDestroy();
        }

        public void setVideoId(String videoId, int currentTime) {
            if (videoId != null) {
                this.videoId = videoId;
                this.currentTime = currentTime;
                if (player != null) {
                    player.cueVideo(videoId);
                    player.play();
                    player.seekToMillis(currentTime);
                }
            }
        }

        public void play(){
            if(videoId != null)
                player.play();
        }

        public void pause() {
            if (player != null) {
                player.pause();
            }
        }

        public void seek(int time){
            player.seekToMillis(time);
        }

        @Override
        public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean restored) {
            this.player = player;
            player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE);
            player.setOnFullscreenListener((FullscreenActivity) getActivity());
            if (!restored && videoId != null) {
                player.cueVideo(videoId);
            }
        }

        @Override
        public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result) {
            this.player = null;
        }

    }

}
