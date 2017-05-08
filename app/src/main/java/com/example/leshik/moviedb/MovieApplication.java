package com.example.leshik.moviedb;

import android.app.Application;

import com.example.leshik.moviedb.data.MovieListType;
import com.example.leshik.moviedb.ui.main.MovieListAdapter;

/**
 * Created by alex on 5/8/17.
 */

public class MovieApplication extends Application {
    private static MovieListAdapter.AdapterState movieListAdapterStates[] = {null, null, null, null}; // 4 pages

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static MovieListAdapter.AdapterState getMovieListAdapterState(MovieListType listType) {
        return movieListAdapterStates[listType.getIndex()];
    }

    public static void setMovieListAdapterStates(MovieListType listType, MovieListAdapter.AdapterState state) {
        movieListAdapterStates[listType.getIndex()] = state;
    }
}
