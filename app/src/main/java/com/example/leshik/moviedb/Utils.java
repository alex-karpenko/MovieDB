package com.example.leshik.moviedb;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;

import static com.example.leshik.moviedb.service.CacheUpdateService.CACHE_PREFS_NAME;

/**
 * Utility class for provide some common (project-wide) methods, variables and constants
 */

public final class Utils {
    // Base URLs to deal with TMBD API
    public static final String baseApiUrl = "http://api.themoviedb.org/3/";
    public static final String baseApiSecureUrl = "https://api.themoviedb.org/3/";

    // Common variables, we fill its by fetching configuration from TMDB (in MovieListFragment class)
    public static String basePosterUrl = null;
    public static String basePosterSecureUrl = null;
    public static String[] posterSizes = null;

    static long getLongCachePreference(Context context, int key) {
        SharedPreferences prefs = context.getSharedPreferences(CACHE_PREFS_NAME, 0);
        return prefs.getLong(context.getString(key), -1);
    }

    static String getStringCachePreference(Context context, int key) {
        SharedPreferences prefs = context.getSharedPreferences(CACHE_PREFS_NAME, 0);
        return prefs.getString(context.getString(key), null);
    }

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        return noOfColumns;
    }
}
