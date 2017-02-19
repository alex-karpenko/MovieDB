package com.example.leshik.moviedb.ui.viewmodels;

import com.example.leshik.moviedb.data.interfaces.MovieInteractor;
import com.example.leshik.moviedb.data.model.Movie;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by Leshik on 17.02.2017.
 */

public class MovieViewModel {
    private MovieInteractor mMovieInteractor;
    private long movieId;

    public MovieViewModel(long movieId, MovieInteractor interactor) {
        this.movieId = movieId;
        mMovieInteractor = interactor;
    }

    public Observable<Movie> getMovie() {
        refresh();
        return mMovieInteractor.getMovieObservable()
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void invertFavorite() {
        mMovieInteractor.sendRequest(new MovieInteractor.MovieRequest(movieId, false, true));
    }

    public void forceRefresh() {
        mMovieInteractor.sendRequest(new MovieInteractor.MovieRequest(movieId, true, false));
    }

    public void refresh() {
        mMovieInteractor.sendRequest(new MovieInteractor.MovieRequest(movieId, false, false));
    }
}
