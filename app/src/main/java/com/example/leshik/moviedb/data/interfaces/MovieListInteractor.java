package com.example.leshik.moviedb.data.interfaces;

import com.example.leshik.moviedb.data.MovieListType;
import com.example.leshik.moviedb.data.model.MovieListViewItem;

import io.reactivex.Observable;

/**
 * Created by Leshik on 17.02.2017.
 *
 * MovieListInteractor interface definition
 *
 */

public interface MovieListInteractor {
    Observable<MovieListViewItem> getList(MovieListType listType, Observable<Integer> nextViewItem);
}
