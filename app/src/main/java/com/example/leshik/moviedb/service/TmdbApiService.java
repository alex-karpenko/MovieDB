package com.example.leshik.moviedb.service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Retrofit class for describe API interfaces
 * see Retrofit documentation
 */
public interface TmdbApiService {
    // retrieve configuration block
    @GET("configuration")
    Call<TmdbConfiguration> getConfiguration(@Query("api_key") String apiKey);

    // retrieve details about specific movie
    @GET("movie/{movie_id}")
    Call<TmdbMovie> getMovie(@Path("movie_id") Integer movie_id, @Query("api_key") String apiKey);

    // retrieve list of videos of the movie
    @GET("movie/{movie_id}/videos")
    Call<TmdbVideos> getVideos(@Path("movie_id") Integer movie_id, @Query("api_key") String apiKey);

    // retrieve list of reviews of the movie
    @GET("movie/{movie_id}/reviews")
    Call<TmdbReviews> getReviews(@Path("movie_id") Integer movie_id, @Query("page") Integer page, @Query("api_key") String apiKey);

    // retrieve list of movies sorted by popularity
    @GET("movie/popular")
    Call<TmdbListPage> getPopular(@Query("api_key") String apiKey, @Query("page") Integer page);

    // retrieve list of movies sorted by rating
    @GET("movie/top_rated")
    Call<TmdbListPage> getToprated(@Query("api_key") String apiKey, @Query("page") Integer page);
}
