package com.example.leshik.moviedb.ui.models;

import com.example.leshik.moviedb.data.interfaces.MovieInteractor;
import com.example.leshik.moviedb.data.model.Movie;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by Leshik on 17.02.2017.
 */

public class MovieViewModel {
    private MovieInteractor mMovieInteractor;

    public MovieViewModel(MovieInteractor interactor) {
        mMovieInteractor = interactor;
    }

    public Observable<Movie> getMovie(long movieId, boolean forceRefresh) {
        return mMovieInteractor.getMovie(movieId, forceRefresh)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void setFavoriteFlag(long movieId, boolean flag) {
        mMovieInteractor.setFavoriteFlag(movieId, flag);
    }
}
