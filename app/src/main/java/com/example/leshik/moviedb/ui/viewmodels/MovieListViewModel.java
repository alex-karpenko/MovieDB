package com.example.leshik.moviedb.ui.viewmodels;

import com.example.leshik.moviedb.data.MovieListType;
import com.example.leshik.moviedb.data.interfaces.MovieListInteractor;
import com.example.leshik.moviedb.data.model.MovieListViewItem;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by alex on 3/7/17.
 *
 * ViewModel class for MovieList loading
 *
 */

public class MovieListViewModel {
    private MovieListInteractor movieListInteractor;
    private MovieListType listType;

    public MovieListViewModel(MovieListType listType, MovieListInteractor movieListInteractor) {
        this.listType = listType;
        this.movieListInteractor = movieListInteractor;
    }

    public Observable<MovieListViewItem> getMovieList(Observable<Integer> nextViewItem) {
        return movieListInteractor.getList(listType, nextViewItem)
                .observeOn(AndroidSchedulers.mainThread());
    }
}
