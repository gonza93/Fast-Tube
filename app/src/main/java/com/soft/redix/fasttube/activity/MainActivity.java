package com.soft.redix.fasttube.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.ads.MobileAds;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.api.services.youtube.model.Video;
import com.soft.redix.fasttube.R;
import com.soft.redix.fasttube.adapter.MainPagerAdapter;
import com.soft.redix.fasttube.adapter.SearchPagerAdapter;
import com.soft.redix.fasttube.adapter.SuggestionAdapter;
import com.soft.redix.fasttube.adapter.VideoAdapter;
import com.soft.redix.fasttube.fragment.HomeFragment;
import com.soft.redix.fasttube.fragment.SearchFragment;
import com.soft.redix.fasttube.util.AsyncResult;
import com.soft.redix.fasttube.util.DeveloperKey;
import com.soft.redix.fasttube.util.DiscoverDialog;
import com.soft.redix.fasttube.util.NavigationUtil;
import com.soft.redix.fasttube.util.YouTubeService;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements YouTubePlayer.OnFullscreenListener, DiscoverDialog.OnSimpleDialogListener{

    private BottomNavigationView mTabsBottom;
    private TabLayout mTabLayout;
    private ViewPager mPager;
    private MainPagerAdapter mAdapter;
    //private DraggableView draggableView;
    private RecyclerView recyclerVideoInfo;
    private Context context;
    private boolean isSearch;
    public static Menu mainMenu;
    public static String videoId, radiusDiscover;
    public String query = "";

    public static FrameLayout actualFragment;

    private RecyclerView recyclerSearchSuggestions;
    private String currentActivity = "Home";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
            getLayoutInflater().setFactory(this);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, "ca-app-pub-4686726862174799~3284088268");

        context = this;
        radiusDiscover = "10000";

        recyclerSearchSuggestions = findViewById(R.id.search_suggestions);

        /*raggableView = findViewById(R.id.draggable_view);
        draggableView.closeToRight();
        draggableView.setVisibility(View.GONE);
        draggableView.setDraggableListener(new DraggableListener() {
            @Override
            public void onMaximized() {

            }

            @Override
            public void onMinimized() {

            }

            @Override
            public void onClosedToLeft() {
                MainActivity.VideoFragment videoFragment =
                        (MainActivity.VideoFragment) getSupportFragmentManager().findFragmentById(R.id.video_fragment_container);
                videoFragment.pause();
            }

            @Override
            public void onClosedToRight() {
                MainActivity.VideoFragment videoFragment =
                        (MainActivity.VideoFragment) getSupportFragmentManager().findFragmentById(R.id.video_fragment_container);
                videoFragment.pause();
            }
        });

        recyclerVideoInfo = findViewById(R.id.recyclerVideoInfo);
        recyclerVideoInfo.setLayoutManager(new LinearLayoutManager(this));
        recyclerVideoInfo.setAdapter(new VideoAdapter(this, new ArrayList<Video>()));*/

        /* ------ Configuracion de tabs ------ */
        //Nueva config de Bottom Navigation View
        mTabsBottom = findViewById(R.id.bottom_navigation);
        mTabsBottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int tab = item.getItemId();
                switch (tab){
                    case R.id.tab_home:
                        actualFragment.setVisibility(View.GONE);
                        actualFragment = findViewById(R.id.main_home_container);
                        actualFragment.setVisibility(View.VISIBLE);
                        findViewById(R.id.main_title).setVisibility(View.VISIBLE);
                        findViewById(R.id.main_search_layout).setVisibility(View.GONE);
                        break;
                    case R.id.tab_search:
                        actualFragment.setVisibility(View.GONE);
                        actualFragment = findViewById(R.id.main_search_container);
                        actualFragment.setVisibility(View.VISIBLE);
                        findViewById(R.id.main_title).setVisibility(View.GONE);
                        onClickSearchTab();
                        break;
                    case R.id.tab_discover:
                        actualFragment.setVisibility(View.GONE);
                        actualFragment = findViewById(R.id.main_discover_container);
                        actualFragment.setVisibility(View.VISIBLE);
                        findViewById(R.id.main_title).setVisibility(View.VISIBLE);
                        findViewById(R.id.main_search_layout).setVisibility(View.GONE);
                        break;
                }
                return true;
            }
        });

        //Cargar Trend videos
        actualFragment = findViewById(R.id.main_home_container);
        replaceFragment(new HomeFragment());
        actualFragment = findViewById(R.id.main_search_container);
        replaceFragment(new SearchFragment());

        actualFragment = findViewById(R.id.main_home_container);

        /*mPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new MainPagerAdapter(getSupportFragmentManager(), MainActivity.this);

        mPager.setAdapter(mAdapter);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        mTabsBottom.setSelectedItemId(R.id.tab_home);
                        break;
                    case 1:
                        onClickSearchTab();
                        mTabsBottom.setSelectedItemId(R.id.tab_search);
                        break;
                    case 2:
                        mTabsBottom.setSelectedItemId(R.id.tab_discover);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });*/

        /*
        mPager.setAdapter(mAdapter);
        mPager.setOffscreenPageLimit(0);
        mTabLayout.setupWithViewPager(mPager);
        mTabLayout.setSelectedTabIndicatorColor(Color.RED);

        //Iconos de tabs
        TabLayout.Tab tab = mTabLayout.getTabAt(0);
        if(tab != null)
            tab.setIcon(R.mipmap.ic_main_home);
        tab = mTabLayout.getTabAt(1);
        if(tab != null)
            tab.setIcon(R.mipmap.ic_main_discover);

        ColorStateList colors;
        if (Build.VERSION.SDK_INT >= 23) {
            colors = getResources().getColorStateList(R.color.tab_icon, getTheme());
        }
        else {
            colors = getResources().getColorStateList(R.color.tab_icon);
        }

        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            tab = mTabLayout.getTabAt(i);
            Drawable icon = tab.getIcon();

            if (icon != null) {
                if(Build.VERSION.SDK_INT >= 21)
                    icon.setTintList(colors);
                else {
                    icon = DrawableCompat.wrap(icon);
                    DrawableCompat.setTintList(icon, colors);
                }
            }
        }
        //Fin configuracion tabs

        mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 1){
                    AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
                    appBarLayout.setExpanded(false);
                    mPager.setCurrentItem(1);
                }
                else {
                    AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
                    appBarLayout.setExpanded(true);
                    mPager.setCurrentItem(0);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });*/

        //Enter search and Automcomplete methods
        final EditText searchBar = (EditText) findViewById(R.id.main_search);
        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);
                    query = searchBar.getText().toString();
                    search();
                }
                return false;
            }
        });
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                recyclerSearchSuggestions.setVisibility(View.VISIBLE);
                String searchBarText = s.toString();
                if(searchBarText.equals(""))
                    recyclerSearchSuggestions.setVisibility(View.GONE);
                else{
                    YouTubeService service = new YouTubeService(getApplicationContext());
                    service.getSearchSuggestions(searchBarText, new AsyncResult() {
                        @Override
                        public void onResult(List<String> suggestions) {
                            SuggestionAdapter suggestionAdapter = new SuggestionAdapter(MainActivity.this, suggestions);
                            recyclerSearchSuggestions.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                            recyclerSearchSuggestions.setAdapter(suggestionAdapter);
                        }
                    });
                }
            }
        });
    }

    public void replaceFragment(Fragment fragment) {
        /*FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(actualFragment.getId(), fragment);
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();*/

        getSupportFragmentManager()
                .beginTransaction()
                .add(actualFragment.getId(), fragment)
                .addToBackStack(fragment.toString())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();

        /*FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(actualFragment.getId(), fragment).addToBackStack(null);
        fragmentTransaction.commit();
        fragmentTransaction.addToBackStack(null);*/
    }

    private void onClickSearchTab(){
        /*if(mTabLayout.isShown()) {
            mTabLayout.animate().scaleY(0).setInterpolator(new AccelerateInterpolator()).start();
            mTabLayout.setVisibility(View.GONE);
        }*/

        isSearch = true;

        if(!findViewById(R.id.toolbar_text_layout).isShown()) {
            findViewById(R.id.main_search_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.main_search_layout).animate().alpha(1.0f);
            findViewById(R.id.main_search_layout).setAlpha(1.0f);
        }

        /*EditText yourEditText= (EditText) findViewById(R.id.main_search);
        yourEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(yourEditText, InputMethodManager.SHOW_IMPLICIT);*/
    }

    public void search(){
        if (isSearch) {

            NavigationUtil.getInstance().pushQuery(query);

            SearchFragment searchFragment = (SearchFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.main_search_container);

            searchFragment.searchPagerAdapter = new SearchPagerAdapter(getSupportFragmentManager(), this, query);
            searchFragment.searchViewPager.setAdapter(searchFragment.searchPagerAdapter);

            searchFragment.searchTabLayout.setupWithViewPager(searchFragment.searchViewPager);
            searchFragment.searchTabLayout.setVisibility(View.VISIBLE);
            searchFragment.searchTabLayout.animate().scaleY(1).setInterpolator(new AccelerateInterpolator()).start();

            searchFragment.searchViewPager.setOffscreenPageLimit(0);
            recyclerSearchSuggestions.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public void onBackPressed() {
        /*if (draggableView.isShown() && draggableView.isMaximized())
            draggableView.minimize();
        else {
            //boolean hasNavigatedBack = onBackNavigation();
            //if(!hasNavigatedBack){
                super.onBackPressed();
            //}
        }*/
        super.onBackPressed();
    }


    public void onBackButtonPressed(View view){
        getSupportFragmentManager().popBackStack();

        if(findViewById(R.id.toolbar_text_layout).isShown())
            findViewById(R.id.toolbar_text_layout).setVisibility(View.INVISIBLE);


        if(findViewById(R.id.main_home_container).isShown())
            findViewById(R.id.main_title).setVisibility(View.VISIBLE);
        else
            onClickSearchTab();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        ActionBar actionBar;

        switch (id){
            case R.id.action_settings:
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.action_premium:
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName + "pro")));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName + "pro")));
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /*private boolean onBackNavigation(){
        ActionBar actionBar;
        final View searchLayout = findViewById(R.id.main_search_layout);

        if(recyclerSearchSuggestions.isShown())
            recyclerSearchSuggestions.setVisibility(View.GONE);
        else {
            switch (NavigationUtil.getInstance().navigateBack()) {
                case NavigationUtil.HOME:
                    searchLayout.animate().setListener(null);
                    searchLayout.animate().alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            searchLayout.setVisibility(View.GONE);
                            searchLayout.animate().setListener(null);
                            handleBackSearch();
                        }
                    });
                    break;
                case NavigationUtil.SEARCH:
                    findViewById(R.id.main_search_layout).setVisibility(View.VISIBLE);
                    findViewById(R.id.main_search_layout).animate().alpha(1.0f);
                    findViewById(R.id.main_search_layout).setAlpha(1.0f);
                    search();
                    break;
                case NavigationUtil.CHANNEL:
                    searchLayout.animate().setListener(null);
                    searchLayout.animate().alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            searchLayout.setVisibility(View.GONE);
                            searchLayout.animate().setListener(null);
                        }
                    });
                    final Handler handler = new Handler();
                    new Thread() {
                        public void run() {
                            YouTubeService service = new YouTubeService(context);
                            String channelId = NavigationUtil.getInstance().getLastId();
                            final List<Channel> channel = service.getChannels(channelId);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {

                                    //TabLayout mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
                                    ViewPager mPager = (ViewPager) findViewById(R.id.pager);
                                    //mTabLayout.clearOnTabSelectedListeners();
                                    ActionBar actionBar = getSupportActionBar();
                                    if (actionBar != null)
                                        actionBar.setTitle(channel.get(0).getSnippet().getTitle());

                                    mPager.setVisibility(View.VISIBLE);
                                    mTabLayout.setVisibility(View.VISIBLE);
                                    mTabLayout.animate().scaleY(1).setInterpolator(new AccelerateInterpolator()).start();

                                    ChannelPagerAdapter channelPagerAdapter = new ChannelPagerAdapter(getSupportFragmentManager(), context,
                                            channel.get(0));

                                    mPager.setAdapter(channelPagerAdapter);
                                    //mTabLayout.setSelectedTabIndicatorColor(Color.WHITE);

                                    //mTabLayout.setupWithViewPager(mPager);
                                    //mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
                                    //TabLayout.Tab tab = mTabLayout.getTabAt(0);
                                    //if (tab != null)
                                    //    tab.select();
                                }
                            });
                        }
                    }.start();
                    break;
                case NavigationUtil.PLAYLIST:
                    String infoId = NavigationUtil.getInstance().getLastId();
                    String[] snippet = infoId.split(",");

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    Fragment newFragment = new PlaylistItemsFragment();
                    Bundle args = new Bundle();
                    args.putString("playlist_id", snippet[0]);
                    args.putString("playlist_channel_title", snippet[1]);
                    args.putString("playlist_title", snippet[2]);
                    args.putString("video_count", snippet[3]);
                    args.putString("playlist_thumb", snippet[4]);
                    newFragment.setArguments(args);

                    ViewPager mPager = (ViewPager) findViewById(R.id.pager);
                    mPager.setVisibility(View.GONE);
                    //tabLayout.animate().scaleY(0).setInterpolator(new AccelerateInterpolator()).start();
                    //tabLayout.setVisibility(View.GONE);
                    actionBar = getSupportActionBar();
                    if (actionBar != null)
                        getSupportActionBar().setTitle(snippet[2]);
                    break;
                default:
                    return false;
            }
        }

        return true;
    }*/

    private void handleBackSearch(){

        query = "";
        ((EditText) findViewById(R.id.main_search)).setText("");
        recyclerSearchSuggestions.setVisibility(View.GONE);

        //Hacer visibles las tabs de inicio
        mAdapter = new MainPagerAdapter(getSupportFragmentManager(), context);

        mPager.setAdapter(mAdapter);
        //mTabLayout.setSelectedTabIndicatorColor(Color.RED);

        //mTabLayout.setupWithViewPager(mPager);


        mPager.setVisibility(View.VISIBLE);
        mTabLayout.setVisibility(View.VISIBLE);

        mTabLayout.animate().scaleY(0).setInterpolator(new AccelerateInterpolator()).start();
        mTabLayout.setVisibility(View.GONE);

        mTabLayout.animate().scaleY(1).setInterpolator(new AccelerateInterpolator()).start();
        mTabLayout.setVisibility(View.VISIBLE);

        //Iconos de tabs
        /*TabLayout.Tab tab = mTabLayout.getTabAt(0);
        if(tab != null)
            tab.setIcon(R.mipmap.ic_main_home);
        tab = mTabLayout.getTabAt(1);
        if(tab != null)
            tab.setIcon(R.mipmap.ic_main_discover);

        ColorStateList colors;
        if (Build.VERSION.SDK_INT >= 23) {
            colors = getResources().getColorStateList(R.color.tab_icon, getTheme());
        }
        else {
            colors = getResources().getColorStateList(R.color.tab_icon);
        }

        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            tab = mTabLayout.getTabAt(i);
            Drawable icon = tab.getIcon();

            if (icon != null) {
                if(Build.VERSION.SDK_INT >= 21)
                    icon.setTintList(colors);
                else {
                    icon = DrawableCompat.wrap(icon);
                    DrawableCompat.setTintList(icon, colors);
                }
            }
        }*/

        //mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        /*mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1) {
                    AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
                    appBarLayout.setExpanded(false);
                    mPager.setCurrentItem(1);
                } else {
                    AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
                    appBarLayout.setExpanded(true);
                    mPager.setCurrentItem(0);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });*/
        //Fin configuracion tabs

        /*InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if(!imm.isAcceptingText())
            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);*/
    }


    @Override
    public void onFullscreen(boolean isFullscreen) {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            /*if(videoId != null) {
                VideoFragment videoFragment =
                        (VideoFragment) getSupportFragmentManager().findFragmentById(R.id.video_fragment_container);
                videoFragment.pause();
                if(videoFragment.isVisible()) {
                    Intent intent = new Intent(MainActivity.this, FullscreenActivity.class);
                    intent.putExtra("video_id", videoId);
                    int time = videoFragment.getCurrentTime();
                    intent.putExtra("current_time", time);
                    startActivity(intent);
                }
            }*/
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            /*VideoFragment videoFragment =
                    (VideoFragment) getSupportFragmentManager().findFragmentById(R.id.video_fragment_container);
            videoFragment.setVideoId(videoId);
            videoFragment.pause();*/
        }
    }

    @Override
    public void onPossitiveButtonClick(int radius) {
        radiusDiscover = Integer.toString(radius);
    }

    @Override
    public void onNegativeButtonClick() {

    }


    public static final class VideoFragment extends YouTubePlayerSupportFragment
    implements YouTubePlayer.OnInitializedListener {

        private YouTubePlayer player;
        private String videoId;

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

        public void setVideoId(String videoId) {
            if (videoId != null) {
                this.videoId = videoId;
                if (player != null) {
                    player.cueVideo(videoId);
                }
            }
        }

        public void play(){
            if(videoId != null && player != null)
                player.play();
        }

        public void pause() {
            if (player != null) {
                player.pause();
            }
        }

        public int getCurrentTime(){
            return player.getCurrentTimeMillis();
        }

        @Override
        public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean restored) {
            this.player = player;
            player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
            player.setOnFullscreenListener((MainActivity) getActivity());
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


