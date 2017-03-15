package com.example.leshik.moviedb.ui.viewmodels;

import com.example.leshik.moviedb.data.MovieListType;
import com.example.leshik.moviedb.data.interfaces.MovieListInteractor;
import com.example.leshik.moviedb.data.model.Movie;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by alex on 3/7/17.
 */

public class MovieListViewModel {
    private MovieListInteractor movieListInteractor;
    private MovieListType listType;

    public MovieListViewModel(MovieListType listType, MovieListInteractor movieListInteractor) {
        this.listType = listType;
        this.movieListInteractor = movieListInteractor;
    }

    public Observable<List<Movie>> getMovieList() {
        return movieListInteractor.getList(listType)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void loadNextPage() {
        movieListInteractor.loadNextPage(listType);
    }

    public void forceRefresh() {
        movieListInteractor.forceRefreshList(listType);
    }
}
