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
    // default page size of popular and toprated list
    private static int cachePageSize = 20;

    // is main screen has two panes
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

    // retrieve stored preference variable (Long)
    static long getLongCachePreference(Context context, int key) {
        SharedPreferences prefs = context.getSharedPreferences(CACHE_PREFS_NAME, 0);
        return prefs.getLong(context.getString(key), -1);
    }

    // retrieve stored preference variable (String)
    static String getStringCachePreference(Context context, int key) {
        SharedPreferences prefs = context.getSharedPreferences(CACHE_PREFS_NAME, 0);
        return prefs.getString(context.getString(key), null);
    }

    // Helper method to update shared preferences by string value
    public static void setCachePreference(Context context, int key, String value) {
        SharedPreferences prefs = context.getSharedPreferences(CACHE_PREFS_NAME, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getString(key), value);
        editor.commit();
    }

    // Helper method to update shared preferences by long value
    public static void setCachePreference(Context context, int key, long value) {
        SharedPreferences prefs = context.getSharedPreferences(CACHE_PREFS_NAME, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(context.getString(key), value);
        editor.commit();
    }

    // calculate number of GridLayout columns based on screen width
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        if (isTwoPane()) {
            dpWidth = dpWidth / 2;
        }
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

    // change favorite icons resource IDs based on the current theme
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

    // return URI with small width poster image
    public static Uri getPosterSmallUri(String poster) {
        return Uri.parse(basePosterSecureUrl
                + posterSmallWidthStr
                + poster);
    }

    // return URI with full size poster image
    public static Uri getPosterFullUri(String poster) {
        return Uri.parse(basePosterSecureUrl
                + posterFullWidthStr
                + poster);
    }

    // update favorite icon in the menu
    public static void setFavoriteIcon(boolean flag, Menu menu) {
        int favIcon;
        if (flag) favIcon = iconFavoriteBlack;
        else favIcon = iconFavoriteOutline;
        if (menu != null) {
            MenuItem favMenuItem = menu.findItem(R.id.action_favorite);
            favMenuItem.setIcon(favIcon);
        }
    }

    // change current theme setting based on the theme name from preferences
    // and schedule activity restart if theme changed
    public static void setCurrentTheme(Context context, String themeName) {
        int themeId = R.style.AppThemeDark;

        if (themeName.equals(context.getString(R.string.pref_theme_dark)))
            themeId = R.style.AppThemeDark;
        else if (themeName.equals(context.getString(R.string.pref_theme_light)))
            themeId = R.style.AppThemeLight;

        if (themeId != currentTheme) scheduleActivityRestart();
        currentTheme = themeId;
    }

    // return curent theme resource ID
    public static int getCurrentTheme() {
        return currentTheme;
    }

    // apply current theme to the context and change icons set
    public static void applyCurrentTheme(Context context) {
        context.setTheme(getCurrentTheme());
        setupThemeIcons(context);
    }

    // load preferences from shared preferences file
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

    // return configured interval of cache update
    public static long getCacheUpdateInterval() {
        return cacheUpdateInterval;
    }

    // set cache update interval
    public static void setCacheUpdateInterval(long cacheUpdateInterval) {
        Utils.cacheUpdateInterval = cacheUpdateInterval;
    }

    // set restart activity flag to schedule restart it
    public static void scheduleActivityRestart() {
        restartActivity = true;
    }

    // restart application if this was planned by setting restartActivity flag
    public static void restartActivityIfNeed(Activity context) {
        if (restartActivity) {
            restartActivity = false;
            // start "root" activity with ACTIVITY_CLEAR_TOP flag
            Intent i = context.getBaseContext().getPackageManager().getLaunchIntentForPackage(context.getBaseContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(i);
        }
    }

    // getter/setter for default number of cache preload pages
    public static int getCachePreloadPages() {
        return cachePreloadPages;
    }

    public static int getCachePageSize() {
        return cachePageSize;
    }

    // getter/setter for two pane flag
    public static boolean isTwoPane() {
        return twoPane;
    }

    public static void setTwoPane(boolean twoPane) {
        Utils.twoPane = twoPane;
    }

    // create and return share action intent
    // TODO: 29.01.2017 develop normal method for create fine html-based letter
    public static Intent getShareIntent(Context context, String title, String poster) {
        Intent i = new Intent(Intent.ACTION_SEND);
        // letter subject
        i.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.share_action_subject) + title);

        // letter content
        String content = new StringBuilder()
                .append(title + "\n\n")
                .append(getPosterSmallUri(poster).toString())
                .toString();
        i.putExtra(Intent.EXTRA_TEXT, content);
        i.setType("text/*");

        return i;
    }

    public static String getApiKey() {
        return BuildConfig.THE_MOVIE_DB_API_KEY;
    }

    public static String getBaseApiUrl() {
        return baseApiUrl;
    }

}
