package com.example.leshik.moviedb.service;

public interface TmdbApiService {
  @GET("configuration")
  Call<TmdbConfiguration> getConfiguration(@Query("api_key") String apiKey);
  
  @GET("movie/{movie_id}")
  Call<TmdbMovie> getMovie(@Path("movie_id") Integer movie_id, @Query("api_key") String apiKey);
  
  @GET("movie/popular")
  Call<TmdbMovie> getPopular(@Query("api_key") String apiKey, @Query("page") Integer page);
  
  @GET("movie/top_rated")
  Call<TmdbMovie> getToprated(@Query("api_key") String apiKey, @Query("page") Integer page);
}
