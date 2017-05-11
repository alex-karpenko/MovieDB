package com.example.leshik.moviedb.data.interfaces;

import com.example.leshik.moviedb.data.model.Movie;

import io.reactivex.Observable;

/**
 * Created by alex on 2/14/17.
 *
 * MovieInteractor interface definition
 *
 */

public interface MovieInteractor {
    Observable<Movie> getMovie(long movieId);

    void invertFavorite(long movieId);
}
