package com.example.leshik.moviedb.service;

/**
 * Created by Leshik on 19.12.2016.
 */

import android.content.ContentValues;

import com.example.leshik.moviedb.model.MoviesContract;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

class Result {

    @SerializedName("poster_path")
    @Expose
    public String posterPath;
    @SerializedName("adult")
    @Expose
    public Boolean adult;
    @SerializedName("overview")
    @Expose
    public String overview;
    @SerializedName("release_date")
    @Expose
    public String releaseDate;
    @SerializedName("genre_ids")
    @Expose
    public List<Integer> genreIds = null;
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("original_title")
    @Expose
    public String originalTitle;
    @SerializedName("original_language")
    @Expose
    public String originalLanguage;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("backdrop_path")
    @Expose
    public String backdropPath;
    @SerializedName("popularity")
    @Expose
    public Float popularity;
    @SerializedName("vote_count")
    @Expose
    public Integer voteCount;
    @SerializedName("video")
    @Expose
    public Boolean video;
    @SerializedName("vote_average")
    @Expose
    public Float voteAverage;

}

public class TmdbListPage {

    @SerializedName("page")
    @Expose
    public Integer page;
    @SerializedName("results")
    @Expose
    public List<Result> results = null;
    @SerializedName("total_results")
    @Expose
    public Integer totalResults;
    @SerializedName("total_pages")
    @Expose
    public Integer totalPages;

    public ContentValues[] getPopularContentValues() {
        if (results == null) return null;

        int resultsSize = results.size();
        if (resultsSize == 0) return null;

        int basePage = (page - 1) * resultsSize;
        ContentValues[] returnValues = new ContentValues[resultsSize];

        for (int i = 0; i < resultsSize; i++) {
            returnValues[i] = new ContentValues();
            returnValues[i].put(MoviesContract.Popular.COLUMN_NAME_SORT_ID, basePage + i + 1);
            returnValues[i].put(MoviesContract.Popular.COLUMN_NAME_MOVIE_ID, results.get(i).id);
        }

        return returnValues;
    }

    public ContentValues[] getTopratedContentValues() {
        if (results == null) return null;

        int resultsSize = results.size();
        if (resultsSize == 0) return null;

        int basePage = (page - 1) * resultsSize;
        ContentValues[] returnValues = new ContentValues[results.size()];

        for (int i = 0; i < resultsSize; i++) {
            returnValues[i] = new ContentValues();
            returnValues[i].put(MoviesContract.Toprated.COLUMN_NAME_SORT_ID, basePage + i + 1);
            returnValues[i].put(MoviesContract.Toprated.COLUMN_NAME_MOVIE_ID, results.get(i).id);
        }

        return returnValues;
    }

    public ContentValues[] getMoviesContentValues() {
        if (results == null) return null;

        int resultsSize = results.size();
        if (resultsSize == 0) return null;

        ContentValues[] returnValues = new ContentValues[results.size()];

        for (int i = 0; i < resultsSize; i++) {
            Result res = results.get(i);
            returnValues[i] = new ContentValues();
            returnValues[i].put(MoviesContract.Movies.COLUMN_NAME_MOVIE_ID, res.id);
            returnValues[i].put(MoviesContract.Movies.COLUMN_NAME_ORIGINAL_TITLE, res.originalTitle);
            returnValues[i].put(MoviesContract.Movies.COLUMN_NAME_OVERVIEW, res.overview);
            returnValues[i].put(MoviesContract.Movies.COLUMN_NAME_RELEASE_DATE, res.releaseDate);
            returnValues[i].put(MoviesContract.Movies.COLUMN_NAME_VOTE_AVERAGE, res.voteAverage);
            returnValues[i].put(MoviesContract.Movies.COLUMN_NAME_POPULARITY, res.popularity);
            returnValues[i].put(MoviesContract.Movies.COLUMN_NAME_POSTER_PATH, res.posterPath);
            returnValues[i].put(MoviesContract.Movies.COLUMN_NAME_ADULT, res.adult);
            returnValues[i].put(MoviesContract.Movies.COLUMN_NAME_VIDEO, res.video);
        }

        return returnValues;
    }
}
