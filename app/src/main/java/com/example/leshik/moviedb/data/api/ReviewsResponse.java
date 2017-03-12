package com.example.leshik.moviedb.data.api;

/**
 * Created by Leshik on 08.01.2017.
 */

/**
 * template class to convert JSON answer from API to data class
 * use it to convert lists of reviews of specific movie
 */

import com.example.leshik.moviedb.data.model.Review;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
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

public class ReviewsResponse {

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

    public List<Review> getReviewListInstance() {
        List<Review> reviewList = new ArrayList<>();

        for (ReviewsResult r : reviewsResults) {
            Review review = new Review();

            review.setMovieId(id);
            review.setReviewId(r.id);
            review.setAuthor(r.author);
            review.setContent(r.content);
            review.setUrl(r.url);

            reviewList.add(review);
        }
        return reviewList;
    }
}
