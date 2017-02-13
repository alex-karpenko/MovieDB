package com.example.leshik.moviedb.model;

import com.example.leshik.moviedb.service.TmdbConfiguration;
import com.example.leshik.moviedb.service.TmdbListPage;
import com.example.leshik.moviedb.service.TmdbMovie;
import com.example.leshik.moviedb.service.TmdbReviews;
import com.example.leshik.moviedb.service.TmdbVideos;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by alex on 2/13/17.
 * <p>
 * Retrofit class for describe API interfaces
 * see Retrofit documentation
 */

public interface ApiService {
    // retrieve configuration block
    @GET("configuration")
    Observable<TmdbConfiguration> getConfiguration(@Query("api_key") String apiKey);

    // retrieve details about specific movie
    @GET("movie/{movie_id}")
    Observable<TmdbMovie> getMovie(@Query("api_key") String apiKey, @Path("movie_id") Integer movie_id);

    // retrieve list of videos of the movie
    @GET("movie/{movie_id}/videos")
    Observable<TmdbVideos> getVideos(@Query("api_key") String apiKey, @Path("movie_id") Integer movie_id);

    // retrieve list of reviews of the movie
    @GET("movie/{movie_id}/reviews")
    Observable<TmdbReviews> getReviews(@Query("api_key") String apiKey, @Path("movie_id") Integer movie_id, @Query("page") Integer page);

    // retrieve list of movies sorted by popularity
    @GET("movie/popular")
    Observable<TmdbListPage> getPopular(@Query("api_key") String apiKey, @Query("page") Integer page);

    // retrieve list of movies sorted by rating
    @GET("movie/top_rated")
    Observable<TmdbListPage> getToprated(@Query("api_key") String apiKey, @Query("page") Integer page);
}
