package com.example.leshik.moviedb.data.interfaces;

import com.example.leshik.moviedb.data.MovieListType;
import com.example.leshik.moviedb.data.model.Movie;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by Leshik on 17.02.2017.
 *
 * MovieListInteractor interface definition
 *
 */

public interface MovieListInteractor {
    Observable<List<Movie>> getList(MovieListType listType);

    boolean forceRefreshList(MovieListType listType);

    boolean loadNextPage(MovieListType listType);
}
