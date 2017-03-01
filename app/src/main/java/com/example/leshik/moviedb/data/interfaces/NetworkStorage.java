package com.example.leshik.moviedb.data.interfaces;

import com.example.leshik.moviedb.data.model.Movie;
import com.example.leshik.moviedb.data.model.Review;
import com.example.leshik.moviedb.data.model.Video;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by Leshik on 01.03.2017.
 */

public interface NetworkStorage {
    Observable<Movie> readMovie(long movieId);

    Observable<List<Video>> readVideoList(long movieId);

    Observable<List<Review>> readReviewList(long movieId);

    Observable<List<Movie>> readPopularListPage(int page);

    Observable<List<Movie>> readTopratedListPage(int page);
}
