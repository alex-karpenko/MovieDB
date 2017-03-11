package com.example.leshik.moviedb.data;

import android.util.Log;

import com.example.leshik.moviedb.BuildConfig;
import com.example.leshik.moviedb.data.api.ConfigurationResponse;
import com.example.leshik.moviedb.data.api.ListPageResponse;
import com.example.leshik.moviedb.data.api.MovieResponse;
import com.example.leshik.moviedb.data.api.ReviewsResponse;
import com.example.leshik.moviedb.data.api.VideosResponse;
import com.example.leshik.moviedb.data.interfaces.ApiService;
import com.example.leshik.moviedb.data.interfaces.NetworkDataSource;
import com.example.leshik.moviedb.data.model.Movie;
import com.example.leshik.moviedb.data.model.NetworkConfig;
import com.example.leshik.moviedb.data.model.Review;
import com.example.leshik.moviedb.data.model.Video;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Leshik on 01.03.2017.
 *
 * Implementation of the NetworkDataSource interface
 * with TheMovieDataBase API via Retrofit2
 */

class TmdbNetworkDataSource implements NetworkDataSource {
    private static final String TAG = "TmdbNetworkDataSource";
    private Retrofit retrofit;
    private Map<MovieListType, Integer> totalPages;
    private Map<MovieListType, Integer> totalItems;

    TmdbNetworkDataSource(String apiUrl) {
        retrofit = new Retrofit.Builder()
                .baseUrl(apiUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        totalPages = new HashMap<>();
        totalItems = new HashMap<>();
    }

    private ApiService getServiceInstance() {
        return retrofit.create(ApiService.class);
    }

    private String getApiKey() {
        return BuildConfig.THE_MOVIE_DB_API_KEY;
    }

    @Override
    public NetworkConfig getNetworkConfig() {
        final NetworkConfig networkConfig = new NetworkConfig();

        ApiService service = getServiceInstance();
        final Observable<NetworkConfig> newConfig = service.getConfiguration(getApiKey())
                .subscribeOn(Schedulers.io())
                .map(new Function<ConfigurationResponse, NetworkConfig>() {
                    @Override
                    public NetworkConfig apply(ConfigurationResponse configurationResponse) throws Exception {
                        return new NetworkConfig(configurationResponse.getImagesBaseUrl(),
                                configurationResponse.getImagesBaseSecureUrl());
                    }
                });

        newConfig.blockingSubscribe(new Consumer<NetworkConfig>() {
            @Override
            public void accept(NetworkConfig config) throws Exception {
                networkConfig.basePosterUrl = config.basePosterUrl;
                networkConfig.basePosterSecureUrl = config.basePosterSecureUrl;
            }
        });

        return networkConfig;
    }

    @Override
    public Observable<Movie> readMovie(long movieId) {
        ApiService service = getServiceInstance();

        return service.getMovie(movieId, getApiKey())
                .subscribeOn(Schedulers.io())
                .map(new Function<MovieResponse, Movie>() {
                    @Override
                    public Movie apply(MovieResponse movieResponse) throws Exception {
                        return movieResponse.getMovieInstance();
                    }
                });
    }

    @Override
    public Observable<List<Video>> readVideoList(long movieId) {
        ApiService service = getServiceInstance();

        return service.getVideos(movieId, getApiKey())
                .subscribeOn(Schedulers.io())
                .map(new Function<VideosResponse, List<Video>>() {
                    @Override
                    public List<Video> apply(VideosResponse videosResponse) throws Exception {
                        return videosResponse.getVideoListInstance();
                    }
                });
    }

    @Override
    public Observable<List<Review>> readReviewList(long movieId) {
        return readReviewListPagesRecursively(movieId, 1);
    }

    private Observable<List<Review>> readReviewListPagesRecursively(final long movieId, final int startPage) {
        final ApiService service = getServiceInstance();

        return service.getReviews(movieId, getApiKey(), startPage)
                .subscribeOn(Schedulers.io())
                .map(new Function<ReviewsResponse, List<Review>>() {
                    @Override
                    public List<Review> apply(ReviewsResponse reviewsResponse) throws Exception {
                        int totalPages1 = reviewsResponse.totalPages;
                        final List<Review> newList = new ArrayList<>();

                        newList.addAll(reviewsResponse.getReviewListInstance());

                        if (totalPages1 > startPage) {
                            readReviewListPagesRecursively(movieId, startPage + 1)
                                    .subscribeOn(Schedulers.io())
                                    .subscribe(new Consumer<List<Review>>() {
                                        @Override
                                        public void accept(List<Review> reviews) throws Exception {
                                            newList.addAll(reviews);
                                        }
                                    });
                        }

                        return newList;
                    }
                });
    }

    @Override
    public Observable<List<Movie>> readMovieListPage(final MovieListType listType, int page) {
        Log.i(TAG, "readMovieListPage: listType = " + listType.toString() + ", page=" + page);

        return getListResponseObservable(listType, page)
                .subscribeOn(Schedulers.io())
                .doOnNext(new Consumer<ListPageResponse>() {
                    @Override
                    public void accept(ListPageResponse listPageResponse) throws Exception {
                        updateListTotals(listType, listPageResponse.totalPages, listPageResponse.totalResults);
                    }
                })
                .map(new Function<ListPageResponse, List<Movie>>() {
                    @Override
                    public List<Movie> apply(ListPageResponse listPageResponse) throws Exception {
                        return listPageResponse.getListPageInstance(listType);
                    }
                });
    }

    private Observable<ListPageResponse> getListResponseObservable(MovieListType listType, int page) {
        ApiService service = getServiceInstance();
        Observable<ListPageResponse> returnObservable;

        switch (listType) {
            case Popular:
                returnObservable = service.getPopular(getApiKey(), page);
                break;
            case Toprated:
                returnObservable = service.getToprated(getApiKey(), page);
                break;
            default:
                throw new IllegalArgumentException("NetworkDataSource: list type does not supported");
        }

        return returnObservable;
    }

    private void updateListTotals(MovieListType listType, int pages, int items) {
        totalPages.put(listType, pages);
        totalItems.put(listType, items);
    }

    @Override
    public int getTotalListPages(MovieListType listType) {
        if (totalPages.containsKey(listType)) return totalPages.get(listType);
        else return 0;
    }

    @Override
    public int getTotalListItems(MovieListType listType) {
        if (totalItems.containsKey(listType)) return totalItems.get(listType);
        else return 0;
    }
}
