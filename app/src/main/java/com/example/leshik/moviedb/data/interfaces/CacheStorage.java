package com.example.leshik.moviedb.data.interfaces;

import com.example.leshik.moviedb.data.MovieListType;
import com.example.leshik.moviedb.data.model.Movie;
import com.example.leshik.moviedb.data.model.MovieListViewItem;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by Leshik on 01.03.2017.
 *
 * CacheStorage interface definition
 *
 */

public interface CacheStorage {
    Observable<Movie> getMovie(long movieId);

    long updateOrInsertMovie(Movie movie);

    long updateOrInsertMovieAsync(Movie movie);

    void updateOrInsertMoviesFromListAsync(List<Movie> movieList);

    void updateMovieListAsync(MovieListType listType, int page, List<Movie> movieList);

    void invertFavorite(long movieId);

    Observable<List<Movie>> getMovieListObservable(MovieListType listType);

    Observable<List<MovieListViewItem>> getMovieListPage(MovieListType listType, int page);

    void clearMovieListPositionsAndInsertOrUpdateData(MovieListType listType, Observable<List<Movie>> movieList);

    void insertOrUpdateMovieList(MovieListType listType, Observable<List<Movie>> movieList);

    int getMovieListLastPageNumber(MovieListType listType);
}
