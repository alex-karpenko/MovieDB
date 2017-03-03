package com.example.leshik.moviedb.ui.viewmodels;

import com.example.leshik.moviedb.data.interfaces.MovieInteractor;
import com.example.leshik.moviedb.data.model.Movie;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by Leshik on 17.02.2017.
 * <p>
 * ViewModel to access movie data
 */

public class MovieViewModel {
    private MovieInteractor mMovieInteractor;
    private long movieId;

    public MovieViewModel(long movieId, MovieInteractor interactor) {
        this.movieId = movieId;
        mMovieInteractor = interactor;
    }

    public Observable<Movie> getMovie() {
        return mMovieInteractor.getMovieObservable(movieId)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void invertFavorite() {
        mMovieInteractor.invertFavorite(movieId);
    }

    public void forceRefresh() {
        mMovieInteractor.forceRefresh(movieId);
    }
}
