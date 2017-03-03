package com.example.leshik.moviedb.data.interfaces;

import com.example.leshik.moviedb.data.model.Movie;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by Leshik on 01.03.2017.
 */

public interface PersistentStorage {
    Observable<Movie> getMovieObservable(long movieId);

    long updateOrInsertMovie(Movie movie);

    void invertFavorite(long movieId);

    List<Movie> readPopularList();

    List<Movie> readTopratedList();

    List<Movie> readFavoriteList();
}
