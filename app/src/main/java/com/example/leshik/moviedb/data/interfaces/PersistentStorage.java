package com.example.leshik.moviedb.data.interfaces;

import com.example.leshik.moviedb.data.model.Movie;

import java.util.List;

/**
 * Created by Leshik on 01.03.2017.
 */

public interface PersistentStorage {
    Movie readMovie(long movieId);

    long writeMovie(Movie movie);

    void setFavoriteFlag(long movieId, boolean favoriteFlag);

    List<Movie> readPopularList();

    List<Movie> readTopratedList();

    List<Movie> readFavoriteList();
}
