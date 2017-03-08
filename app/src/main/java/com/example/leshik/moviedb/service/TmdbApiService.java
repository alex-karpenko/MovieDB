package com.example.leshik.moviedb.service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Retrofit class for describe API interfaces
 * see Retrofit documentation
 */
public interface TmdbApiService {
    // retrieve configuration block
    @GET("configuration")
    Call<TmdbConfiguration> getConfiguration(@Query("api_key") String apiKey);
}
