package com.example.leshik.moviedb.data.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by alex on 2/13/17.
 */

public class Review extends RealmObject {
    @PrimaryKey
    private String reviewId;

    private long movieId;
    private String author;
    private String content;
    private String url;

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public long getMovieId() {
        return movieId;
    }

    public void setMovieId(long movieId) {
        this.movieId = movieId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
