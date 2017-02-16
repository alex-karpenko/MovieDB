package com.example.leshik.moviedb.data.interfaces;

import com.example.leshik.moviedb.data.model.Review;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by alex on 2/14/17.
 */

public interface ReviewsInteractor {
    Observable<List<Review>> getReviewList(long movieId);
    Observable<Review> getReview(String reviewId);
}
