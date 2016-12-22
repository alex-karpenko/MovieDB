package com.example.leshik.moviedb.service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TmdbApiService {
  @GET("configuration")
  Call<TmdbConfiguration> getConfiguration(@Query("api_key") String apiKey);
  
  @GET("movie/{movie_id}")
  Call<TmdbMovie> getMovie(@Path("movie_id") Integer movie_id, @Query("api_key") String apiKey);
  
  @GET("movie/popular")
  Call<TmdbListPage> getPopular(@Query("api_key") String apiKey, @Query("page") Integer page);
  
  @GET("movie/top_rated")
  Call<TmdbListPage> getToprated(@Query("api_key") String apiKey, @Query("page") Integer page);
}
