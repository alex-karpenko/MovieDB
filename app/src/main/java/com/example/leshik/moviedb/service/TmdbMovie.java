package com.example.leshik.moviedb.service;

/**
 * Created by Leshik on 19.12.2016.
 */

import android.content.ContentValues;

import com.example.leshik.moviedb.model.MoviesContract;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * template class to convert JSON answer from API to data class
 * use it to convert information about specific movie
 */


class BelongsToCollection {

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("poster_path")
    @Expose
    public String posterPath;
    @SerializedName("backdrop_path")
    @Expose
    public String backdropPath;

}

class Genre {

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("name")
    @Expose
    public String name;

}

class ProductionCompany {

    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("id")
    @Expose
    public Integer id;

}

class ProductionCountry {

    @SerializedName("iso_3166_1")
    @Expose
    public String iso31661;
    @SerializedName("name")
    @Expose
    public String name;

}

class SpokenLanguage {

    @SerializedName("iso_639_1")
    @Expose
    public String iso6391;
    @SerializedName("name")
    @Expose
    public String name;

}

public class TmdbMovie {

    @SerializedName("adult")
    @Expose
    public Boolean adult;
    @SerializedName("backdrop_path")
    @Expose
    public String backdropPath;
    @SerializedName("belongs_to_collection")
    @Expose
    public BelongsToCollection belongsToCollection;
    @SerializedName("budget")
    @Expose
    public Float budget;
    @SerializedName("genres")
    @Expose
    public List<Genre> genres = null;
    @SerializedName("homepage")
    @Expose
    public String homepage;
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("imdb_id")
    @Expose
    public String imdbId;
    @SerializedName("original_language")
    @Expose
    public String originalLanguage;
    @SerializedName("original_title")
    @Expose
    public String originalTitle;
    @SerializedName("overview")
    @Expose
    public String overview;
    @SerializedName("popularity")
    @Expose
    public Float popularity;
    @SerializedName("poster_path")
    @Expose
    public String posterPath;
    @SerializedName("production_companies")
    @Expose
    public List<ProductionCompany> productionCompanies = null;
    @SerializedName("production_countries")
    @Expose
    public List<ProductionCountry> productionCountries = null;
    @SerializedName("release_date")
    @Expose
    public String releaseDate;
    @SerializedName("revenue")
    @Expose
    public Float revenue;
    @SerializedName("runtime")
    @Expose
    public Integer runtime;
    @SerializedName("spoken_languages")
    @Expose
    public List<SpokenLanguage> spokenLanguages = null;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("tagline")
    @Expose
    public String tagline;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("video")
    @Expose
    public Boolean video;
    @SerializedName("vote_average")
    @Expose
    public Float voteAverage;
    @SerializedName("vote_count")
    @Expose
    public Integer voteCount;

    // helper class to get values for table row to insert
    public ContentValues getMovieContentValues() {
        ContentValues returnValues = new ContentValues();

        returnValues.put(MoviesContract.Movies.COLUMN_NAME_MOVIE_ID, id);
        returnValues.put(MoviesContract.Movies.COLUMN_NAME_ORIGINAL_TITLE, originalTitle);
        returnValues.put(MoviesContract.Movies.COLUMN_NAME_OVERVIEW, overview);
        returnValues.put(MoviesContract.Movies.COLUMN_NAME_RELEASE_DATE, releaseDate);
        returnValues.put(MoviesContract.Movies.COLUMN_NAME_VOTE_AVERAGE, voteAverage);
        returnValues.put(MoviesContract.Movies.COLUMN_NAME_POPULARITY, popularity);
        returnValues.put(MoviesContract.Movies.COLUMN_NAME_POSTER_PATH, posterPath);
        returnValues.put(MoviesContract.Movies.COLUMN_NAME_HOMEPAGE, homepage);
        returnValues.put(MoviesContract.Movies.COLUMN_NAME_ADULT, adult);
        returnValues.put(MoviesContract.Movies.COLUMN_NAME_VIDEO, video);
        returnValues.put(MoviesContract.Movies.COLUMN_NAME_RUNTIME, runtime);

        return returnValues;
    }
}
