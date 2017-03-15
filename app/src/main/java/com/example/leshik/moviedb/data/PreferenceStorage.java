package com.example.leshik.moviedb.data;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.example.leshik.moviedb.R;
import com.example.leshik.moviedb.data.interfaces.NetworkDataSource;
import com.example.leshik.moviedb.data.interfaces.PreferenceInterface;
import com.example.leshik.moviedb.data.model.NetworkConfig;

import java.util.Calendar;

/**
 * Created by alex on 3/9/17.
 *
 * Implementation of the PreferenceInterface
 * using SharedPreferences
 *
 */

public class PreferenceStorage implements PreferenceInterface {
    private static final long ONE_HOUR_MILLIS = 60 * 60 * 1000;
    private static final String BASE_POSTER_URL_KEY = "base_poster_url";
    private static final String BASE_POSTER_SECURE_URL_KEY = "base_poster_secure_url";

    private static final PreferenceStorage ourInstance = new PreferenceStorage();
    private static Context context;
    private static volatile Boolean isInitialized = false;
    // Base URLs to deal with TMDB API
    private static final String baseApiSecureUrl = "https://api.themoviedb.org/3/";
    // Common variables, we fill its by fetching configuration from TMDB
    private static NetworkConfig networkConfig;
    // default page size of popular and toprated list
    private int cachePageSize = 20;
    // cache update interval in milliseconds
    private static long cacheUpdateInterval = ONE_HOUR_MILLIS * 24; // 24 hours
    // Default image width
    private String posterSmallWidthStr = "w185";
    private String posterFullWidthStr = "original";
    // current theme id
    private static int currentTheme = R.style.AppThemeDark;

    private PreferenceStorage() {
    }

    public static PreferenceStorage getInstance(Context newContext) {
        if (!isInitialized) {
            if (newContext instanceof Application) {
                synchronized (isInitialized) {
                    if (!isInitialized) {
                        context = newContext;
                        loadDefaultPreferences();
                        isInitialized = true;
                    }
                }
            } else
                throw new IllegalArgumentException("Context must be instance of Application class");
        }
        return ourInstance;
    }

    // load preferences from shared preferences file
    private static void loadDefaultPreferences() {
        // set defaults, if need
        PreferenceManager.setDefaultValues(context, R.xml.pref_general, false);
        // Theme
        String themeName = getCurrentThemeNameFromPreferenceFile();
        currentTheme = getThemeIdFromName(themeName);
        // Update interval
        cacheUpdateInterval = getCacheUpdateIntervalHoursFromPreferenceFile() * ONE_HOUR_MILLIS;

        updateNetworkConfiguration();
    }

    private static void updateNetworkConfiguration() {
        // load early cache data
        networkConfig = new NetworkConfig(getStringPreference(BASE_POSTER_URL_KEY),
                getStringPreference(BASE_POSTER_SECURE_URL_KEY));
        // update config from network
        try {
            NetworkDataSource networkDataSource = new TmdbNetworkDataSource(baseApiSecureUrl);
            NetworkConfig newConfig = networkDataSource.getNetworkConfig();
            if (newConfig.isValid()) {
                networkConfig = newConfig;
                setPreference(BASE_POSTER_URL_KEY, networkConfig.basePosterUrl);
                setPreference(BASE_POSTER_SECURE_URL_KEY, networkConfig.basePosterSecureUrl);
            }
        } catch (Exception e) {
        }
    }

    private static String getStringPreference(String key) {
        return getCurrentPreferences().getString(key, null);
    }

    private static String getCurrentThemeNameFromPreferenceFile() {
        return getCurrentPreferences().getString(context.getString(R.string.pref_theme_key), context.getString(R.string.pref_theme_default));
    }

    @Override
    public int getTheme() {
        return currentTheme;
    }

    @Override
    public int setTheme(String newTheme) {
        currentTheme = getThemeIdFromName(newTheme);
        return currentTheme;
    }

    private static SharedPreferences getCurrentPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    private static int getThemeIdFromName(String themeName) {
        int themeId = R.style.AppThemeDark;

        if (themeName.equals(context.getString(R.string.pref_theme_dark)))
            themeId = R.style.AppThemeDark;
        else if (themeName.equals(context.getString(R.string.pref_theme_light)))
            themeId = R.style.AppThemeLight;

        return themeId;
    }

    private static long getCacheUpdateIntervalHoursFromPreferenceFile() {
        return Long.valueOf(getCurrentPreferences().getString(context.getString(R.string.pref_cache_key), "0"));
    }

    @Override
    public long getMovieListUpdateTimestamp(MovieListType listType) {
        return getCurrentPreferences().getLong(listType.getUpdateTimestampKey(), -1L);
    }

    @Override
    public void setMovieListUpdateTimestamp(MovieListType listType, long updateTime) {
        setPreference(listType.getUpdateTimestampKey(), updateTime);
    }

    private void setPreference(String key, long value) {
        SharedPreferences.Editor editor = getPreferenceEditor();
        editor.putLong(key, value);
        editor.commit();
    }

    private static SharedPreferences.Editor getPreferenceEditor() {
        SharedPreferences prefs = getCurrentPreferences();
        return prefs.edit();
    }

    private void setPreference(String key, int value) {
        SharedPreferences.Editor editor = getPreferenceEditor();
        editor.putInt(key, value);
        editor.commit();
    }

    private static void setPreference(String key, String value) {
        SharedPreferences.Editor editor = getPreferenceEditor();
        editor.putString(key, value);
        editor.commit();
    }

    @Override
    public long setMovieListUpdateTimestampToCurrent(MovieListType listType) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        setMovieListUpdateTimestamp(listType, currentTime);

        return currentTime;
    }

    @Override
    public int getMovieListTotalPages(MovieListType listType) {
        return getCurrentPreferences().getInt(listType.getTotalPagesKey(), -1);
    }

    @Override
    public void setMovieListTotalPages(MovieListType listType, int totalPages) {
        setPreference(listType.getTotalPagesKey(), totalPages);
    }

    @Override
    public int getMovieListTotalItems(MovieListType listType) {
        return getCurrentPreferences().getInt(listType.getTotalItemsKey(), -1);
    }

    @Override
    public void setMovieListTotalItems(MovieListType listType, int totalItems) {
        setPreference(listType.getTotalItemsKey(), totalItems);
    }

    @Override
    public String getBaseApiUrl() {
        return baseApiSecureUrl;
    }

    @Override
    public int getCachePageSize() {
        return cachePageSize;
    }

    @Override
    public Uri getPosterSmallUri(String poster) {
        return Uri.parse(networkConfig.basePosterSecureUrl
                + posterSmallWidthStr
                + poster);
    }

    @Override
    public Uri getPosterFullUri(String poster) {
        return Uri.parse(networkConfig.basePosterSecureUrl
                + posterFullWidthStr
                + poster);
    }

    @Override
    public long getCacheUpdateInterval() {
        return cacheUpdateInterval;
    }

    @Override
    public void setCacheUpdateIntervalMillis(long cacheUpdateInterval) {
        PreferenceStorage.cacheUpdateInterval = cacheUpdateInterval;
    }

    @Override
    public void setCacheUpdateIntervalHours(int cacheUpdateIntervalHours) {
        setCacheUpdateIntervalMillis(cacheUpdateIntervalHours * ONE_HOUR_MILLIS);
    }
}
