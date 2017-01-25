package com.example.leshik.moviedb;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;

import static com.example.leshik.moviedb.service.CacheUpdateService.CACHE_PREFS_NAME;

/**
 * Utility class for provide some common (project-wide) methods, variables and constants
 */

public final class Utils {
    private static final String TAG = "Utils";
    // Base URLs to deal with TMBD API
    public static final String baseApiUrl = "http://api.themoviedb.org/3/";
    public static final String baseApiSecureUrl = "https://api.themoviedb.org/3/";

    // Common variables, we fill its by fetching configuration from TMDB (in MovieListFragment class)
    public static String basePosterUrl = null;
    public static String basePosterSecureUrl = null;
    public static String[] posterSizes = null;

    // Number of pages to preload if cache is empty
    private static int cachePreloadPages = 2;

    private static int cachePageSize = 20;

    private static boolean twoPane = false;

    // cache update interval in milliseconds
    private static long cacheUpdateInterval = 1000 * 60 * 60 * 24; // 24 hours

    // current theme id
    private static int currentTheme = R.style.AppThemeDark;

    // Current favorite icons
    private static int iconFavoriteBlack = R.drawable.ic_favorite_black_light;
    private static int iconFavoriteOutline = R.drawable.ic_favorite_outline_light;

    // Default image width
    private static String posterSmallWidthStr = "w185";
    private static String posterFullWidthStr = "original";

    // flag to know is activity needs to restart after theme switch
    private static boolean restartActivity = false;

    // private constructor to avoid creation on instance
    private Utils() {
    }

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

        if (isTwoPane()) {
            dpWidth = dpWidth / 2;
        }
        int noOfColumns = Math.round(dpWidth / 180);
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

    static void setCurrentTheme(Context context, String themeName) {
        int themeId = R.style.AppThemeDark;

        if (themeName.equals(context.getString(R.string.pref_theme_dark)))
            themeId = R.style.AppThemeDark;
        else if (themeName.equals(context.getString(R.string.pref_theme_light)))
            themeId = R.style.AppThemeLight;

        if (themeId != currentTheme) scheduleActivityRestart();
        currentTheme = themeId;
    }

    public static int getCurrentTheme() {
        return currentTheme;
    }

    static void applyCurrentTheme(Context context) {
        context.setTheme(getCurrentTheme());
        setupThemeIcons(context);
    }

    public static void loadDefaultPreferences(Context context) {
        // set defaults, if need
        PreferenceManager.setDefaultValues(context, R.xml.pref_general, false);

        // get theme and apply it
        setCurrentTheme(context, PreferenceManager.getDefaultSharedPreferences(context).
                getString(context.getString(R.string.pref_theme_key), context.getString(R.string.pref_theme_default)));
        applyCurrentTheme(context);

        // get cache update interval
        setCacheUpdateInterval(Long.valueOf(PreferenceManager.getDefaultSharedPreferences(context).
                getString(context.getString(R.string.pref_cache_key), "0")) * 60 * 60 * 1000);
    }

    public static long getCacheUpdateInterval() {
        return cacheUpdateInterval;
    }

    public static void setCacheUpdateInterval(long cacheUpdateInterval) {
        Utils.cacheUpdateInterval = cacheUpdateInterval;
    }

    public static void scheduleActivityRestart() {
        restartActivity = true;
    }

    public static void restartActivityIfNeed(Activity context) {
        if (restartActivity) {
            restartActivity = false;
            Intent i = context.getBaseContext().getPackageManager().getLaunchIntentForPackage(context.getBaseContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(i);
        }
    }

    public static int getCachePreloadPages() {
        return cachePreloadPages;
    }

    public static int getCachePageSize() {
        return cachePageSize;
    }

    public static void startFullPosterActivity(Context context, int movieId, String posterName) {
        Intent intent = new Intent(context, FullPosterActivity.class);
        intent.putExtra(FullPosterActivity.ARG_POSTER_NAME, posterName);
        intent.putExtra(FullPosterActivity.ARG_MOVIE_ID, movieId);
        context.startActivity(intent);

    }

    public static boolean isTwoPane() {
        return twoPane;
    }

    public static void setTwoPane(boolean twoPane) {
        Utils.twoPane = twoPane;
    }


}
