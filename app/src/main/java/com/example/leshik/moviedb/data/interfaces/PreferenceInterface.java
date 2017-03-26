package com.example.leshik.moviedb.data.interfaces;

import android.net.Uri;

import com.example.leshik.moviedb.data.MovieListType;
import com.example.leshik.moviedb.data.model.NetworkConfig;

/**
 * Created by alex on 3/9/17.
 *
 * PreferenceInterface definition
 *
 */

public interface PreferenceInterface {
    long getMovieListUpdateTimestamp(MovieListType listType);

    void setMovieListUpdateTimestamp(MovieListType listType, long updateTime);

    long setMovieListUpdateTimestampToCurrent(MovieListType listType);

    int getMovieListTotalPages(MovieListType listType);

    void setMovieListTotalPages(MovieListType listType, int totalPages);

    int getMovieListTotalItems(MovieListType listType);

    void setMovieListTotalItems(MovieListType listType, int totalItems);

    String getBaseApiUrl();

    int getCachePageSize();

    Uri getPosterSmallUri(String poster);

    Uri getPosterMediumUri(String poster);

    Uri getPosterFullUri(String poster);

    Uri getOptimalImageUri(NetworkConfig.ImageType type, String imageName);

    long getCacheUpdateInterval();

    void setCacheUpdateIntervalMillis(long cacheUpdateInterval);

    void setCacheUpdateIntervalHours(int cacheUpdateIntervalHours);

    int getTheme();

    int setTheme(String newTheme);
}
