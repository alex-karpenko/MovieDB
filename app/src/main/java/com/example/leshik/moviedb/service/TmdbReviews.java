package com.example.leshik.moviedb.service;

/**
 * Created by Leshik on 08.01.2017.
 */

import android.content.ContentValues;

import com.example.leshik.moviedb.model.MoviesContract;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

class ReviewsResult {

    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("author")
    @Expose
    public String author;
    @SerializedName("content")
    @Expose
    public String content;
    @SerializedName("url")
    @Expose
    public String url;

}

public class TmdbReviews {

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("page")
    @Expose
    public Integer page;
    @SerializedName("results")
    @Expose
    public List<ReviewsResult> reviewsResults = null;
    @SerializedName("total_pages")
    @Expose
    public Integer totalPages;
    @SerializedName("total_results")
    @Expose
    public Integer totalResults;

    public ContentValues[] getReviewsContentValues() {
        if (reviewsResults == null) return null;

        int resultsSize = reviewsResults.size();
        if (resultsSize == 0) return null;

        ContentValues[] returnValues = new ContentValues[resultsSize];

        for (int i = 0; i < resultsSize; i++) {
            returnValues[i] = new ContentValues();
            returnValues[i].put(MoviesContract.Reviews.COLUMN_NAME_MOVIE_ID, id);
            returnValues[i].put(MoviesContract.Reviews.COLUMN_NAME_REVIEW_ID, reviewsResults.get(i).id);
            returnValues[i].put(MoviesContract.Reviews.COLUMN_NAME_AUTHOR, reviewsResults.get(i).author);
            returnValues[i].put(MoviesContract.Reviews.COLUMN_NAME_CONTENT, reviewsResults.get(i).content);
            returnValues[i].put(MoviesContract.Reviews.COLUMN_NAME_URL, reviewsResults.get(i).url);
        }

        return returnValues;
    }

}
