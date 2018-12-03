package com.soft.redix.fasttube.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by gonzalo on 26/8/17.
 */

public class SettingsHelper {

    private static final String FILE = "file";
    private static final String CLOSEINFO = "closeinfo";

    public static void dontShowDiscoverInfo(Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit();
        editor.putBoolean(CLOSEINFO, true);
        editor.apply();
    }

    public static boolean getDiscoverInfoSetting(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(CLOSEINFO, false);
    }


}
