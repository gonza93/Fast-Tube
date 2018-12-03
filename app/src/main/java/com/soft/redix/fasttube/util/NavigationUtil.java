package com.soft.redix.fasttube.util;

import com.soft.redix.fasttube.adapter.ChannelListAdapter;

import java.util.Stack;

/**
 * Created by gonzalo on 21/7/17.
 */

public class NavigationUtil {

    private static NavigationUtil instance;

    private Stack<String> backStackQuery;
    private Stack<String> backStackId;
    private static String currentActivity;
    private static String currentId;

    public static final String HOME = "Home", SEARCH = "Search", CHANNEL = "Channel", PLAYLIST = "Playlist";

    public static NavigationUtil getInstance(){
        if(instance == null) {
            instance = new NavigationUtil();
            instance.backStackQuery = new Stack<>();
        }
        return instance;
    }

    public void pushQuery(String query){
        backStackQuery.push(query);
    }

    public String popQuery(){
        if(!backStackQuery.empty())
            return backStackQuery.pop();
        else
            return "";
    }

}
