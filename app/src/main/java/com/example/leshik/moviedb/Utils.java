package com.example.leshik.moviedb;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;

import static com.example.leshik.moviedb.service.CacheUpdateService.CACHE_PREFS_NAME;

/**
 * Utility class for provide some common (project-wide) methods, variables and constants
 */

public final class Utils {
    // Base URLs to deal with TMBD API
    public static final String baseApiUrl = "http://api.themoviedb.org/3/";
    public static final String baseApiSecureUrl = "https://api.themoviedb.org/3/";
    // Number of pages to preload
    public static int CACHE_PRELOAD_PAGES = 5;

    // cache update interval in milliseconds
    // 5 min for debug
    private static long CACHE_UPDATE_INTERVAL = 1000 * 60 * 5; // 5 minutes

    // getter/setter
    public static long getCacheUpdateInterval() {
        return CACHE_UPDATE_INTERVAL;
    }

    public static void setCacheUpdateInterval(long cacheUpdateInterval) {
        CACHE_UPDATE_INTERVAL = cacheUpdateInterval;
    }


    // Common variables, we fill its by fetching configuration from TMDB (in MovieListFragment class)
    public static String basePosterUrl = null;
    public static String basePosterSecureUrl = null;
    public static String[] posterSizes = null;

    // Current favorite icons
    static int iconFavoriteBlack = R.drawable.ic_favorite_black_light;
    static int iconFavoriteOutline = R.drawable.ic_favorite_outline_light;

    // Default image width
    private static String posterSmallWidthStr = "w185";
    private static String posterFullWidthStr = "original";

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
        if (noOfColumns == 1) noOfColumns = 2;
        return noOfColumns;
    }

    // Start youtube activity - first try app, if error - start via web
    // Source: http://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent
    public static void watchYoutubeVideo(Context context, String id) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }

    static void setupThemeIcons(Context context) {
        TypedValue val = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.isLightTheme, val, true);
        if (val.type == TypedValue.TYPE_INT_BOOLEAN && val.data == 0) {
            // Dark
            iconFavoriteBlack = R.drawable.ic_favorite_black_dark;
            iconFavoriteOutline = R.drawable.ic_favorite_outline_dark;
        } else {
            // Light
            iconFavoriteBlack = R.drawable.ic_favorite_black_light;
            iconFavoriteOutline = R.drawable.ic_favorite_outline_light;
        }
    }

    static Uri getPosterSmallUri(String poster) {
        return Uri.parse(basePosterSecureUrl
                + posterSmallWidthStr
                + poster);
    }

    static Uri getPosterFullUri(String poster) {
        return Uri.parse(basePosterSecureUrl
                + posterFullWidthStr
                + poster);
    }

    static void setFavoriteIcon(boolean flag, Menu menu) {
        int favIcon;
        if (flag) favIcon = iconFavoriteBlack;
        else favIcon = iconFavoriteOutline;
        if (menu != null) {
            MenuItem favMenuItem = menu.findItem(R.id.action_favorite);
            favMenuItem.setIcon(favIcon);
        }
    }
}
