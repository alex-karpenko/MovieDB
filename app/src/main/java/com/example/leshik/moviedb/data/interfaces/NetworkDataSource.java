package com.example.leshik.moviedb.data.interfaces;

import com.example.leshik.moviedb.data.MovieListType;
import com.example.leshik.moviedb.data.model.Movie;
import com.example.leshik.moviedb.data.model.Review;
import com.example.leshik.moviedb.data.model.Video;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by Leshik on 01.03.2017.
 */

public interface NetworkDataSource {
    Observable<Movie> readMovie(long movieId);

    Observable<List<Video>> readVideoList(long movieId);

    Observable<List<Review>> readReviewList(long movieId);

    Observable<List<Movie>> readMovieListPage(MovieListType listType, int page);

    int getTotalListPages(MovieListType listType);

    int getTotalListItems(MovieListType listType);
}
