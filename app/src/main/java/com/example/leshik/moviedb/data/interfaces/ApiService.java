package com.example.leshik.moviedb.data.interfaces;

import com.example.leshik.moviedb.data.api.ConfigurationResponse;
import com.example.leshik.moviedb.data.api.ListPageResponse;
import com.example.leshik.moviedb.data.api.MovieResponse;
import com.example.leshik.moviedb.data.api.ReviewsResponse;
import com.example.leshik.moviedb.data.api.VideosResponse;
import com.example.leshik.moviedb.service.TmdbListPage;

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
    Observable<ConfigurationResponse> getConfiguration(@Query("api_key") String apiKey);

    // retrieve details about specific movie
    @GET("movie/{movie_id}")
    Observable<MovieResponse> getMovie(@Path("movie_id") long movie_id, @Query("api_key") String apiKey);

    // retrieve list of videos of the movie
    @GET("movie/{movie_id}/videos")
    Observable<VideosResponse> getVideos(@Query("api_key") String apiKey, @Path("movie_id") long movie_id);

    // retrieve list of reviews of the movie
    @GET("movie/{movie_id}/reviews")
    Observable<ReviewsResponse> getReviews(@Query("api_key") String apiKey, @Path("movie_id") long movie_id, @Query("page") int page);

    // retrieve list of movies sorted by popularity
    @GET("movie/popular")
    Observable<ListPageResponse> getPopular(@Query("api_key") String apiKey, @Query("page") int page);

    // retrieve list of movies sorted by rating
    @GET("movie/top_rated")
    Observable<ListPageResponse> getToprated(@Query("api_key") String apiKey, @Query("page") int page);
}
