package com.example.leshik.moviedb.data.interfaces;

import android.net.Uri;

import com.example.leshik.moviedb.data.MovieListType;

/**
 * Created by alex on 3/9/17.
 */

public interface PreferenceInterface {
    long getMovieListUpdateTimestamp(MovieListType listType);

    void setMovieListUpdateTimestamp(MovieListType listType, long updateTime);

    long updateMovieListUpdateTimestampToCurrent(MovieListType listType);

    int getMovieListTotalPages(MovieListType listType);

    void setMovieListTotalPages(MovieListType listType, int totalPages);

    int getMovieListTotalItems(MovieListType listType);

    void setMovieListTotalItems(MovieListType listType, int totalItems);

    String getBaseApiUrl();

    int getCachePageSize();

    Uri getPosterSmallUri(String poster);

    Uri getPosterFullUri(String poster);

    long getCacheUpdateInterval();

    void setCacheUpdateIntervalMillis(long cacheUpdateInterval);

    void setCacheUpdateIntervalHours(int cacheUpdateIntervalHours);

    int getTheme();

    int setTheme(String newTheme);
}
