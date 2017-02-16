package com.example.leshik.moviedb.data.api;

/**
 * Created by Leshik on 19.12.2016.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * template class to convert JSON answer from API to data class
 * use it to convert lists of popular and top rated movies
 */


class ListResult {

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

public class ListPageResponse {

    @SerializedName("page")
    @Expose
    public Integer page;
    @SerializedName("results")
    @Expose
    public List<ListResult> listResults = null;
    @SerializedName("total_results")
    @Expose
    public Integer totalResults;
    @SerializedName("total_pages")
    @Expose
    public Integer totalPages;

}
