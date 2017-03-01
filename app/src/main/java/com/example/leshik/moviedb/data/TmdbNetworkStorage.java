package com.example.leshik.moviedb.data;

import com.example.leshik.moviedb.BuildConfig;
import com.example.leshik.moviedb.data.api.ListPageResponse;
import com.example.leshik.moviedb.data.api.MovieResponse;
import com.example.leshik.moviedb.data.api.ReviewsResponse;
import com.example.leshik.moviedb.data.api.VideosResponse;
import com.example.leshik.moviedb.data.interfaces.ApiService;
import com.example.leshik.moviedb.data.interfaces.NetworkStorage;
import com.example.leshik.moviedb.data.model.Movie;
import com.example.leshik.moviedb.data.model.Review;
import com.example.leshik.moviedb.data.model.Video;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Leshik on 01.03.2017.
 */

public class TmdbNetworkStorage implements NetworkStorage {
    private static final String TAG = "TmdbNetworkStorage";

    private Retrofit retrofit;

    public TmdbNetworkStorage(String apiUrl) {
        retrofit = new Retrofit.Builder()
                .baseUrl(apiUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    private ApiService getServiceInstance() {
        return retrofit.create(ApiService.class);
    }

    private String getApiKey() {
        return BuildConfig.THE_MOVIE_DB_API_KEY;
    }

    @Override
    public Observable<Movie> readMovie(long movieId) {
        ApiService service = getServiceInstance();

        Observable<Movie> movie =
                service.getMovie(movieId, getApiKey())
                        .subscribeOn(Schedulers.io())
                        .map(new Function<MovieResponse, Movie>() {
                            @Override
                            public Movie apply(MovieResponse movieResponse) throws Exception {
                                return movieResponse.getMovieInstance();
                            }
                        });

        return movie;
    }

    @Override
    public Observable<List<Video>> readVideoList(long movieId) {
        ApiService service = getServiceInstance();

        Observable<List<Video>> videoList =
                service.getVideos(movieId, getApiKey())
                        .subscribeOn(Schedulers.io())
                        .map(new Function<VideosResponse, List<Video>>() {
                            @Override
                            public List<Video> apply(VideosResponse videosResponse) throws Exception {
                                return videosResponse.getVideoListInstance();
                            }
                        });

        return videoList;
    }

    @Override
    public Observable<List<Review>> readReviewList(long movieId) {
        return readReviewListPagesRecursively(movieId, 1);
    }

    private Observable<List<Review>> readReviewListPagesRecursively(final long movieId, final int startPage) {
        final ApiService service = getServiceInstance();

        Observable<List<Review>> reviewList =
                service.getReviews(movieId, getApiKey(), startPage)
                        .subscribeOn(Schedulers.io())
                        .map(new Function<ReviewsResponse, List<Review>>() {
                            @Override
                            public List<Review> apply(ReviewsResponse reviewsResponse) throws Exception {
                                int totalPages = reviewsResponse.totalPages;
                                final List<Review> newList = new ArrayList<>();

                                newList.addAll(reviewsResponse.getReviewListInstance());

                                if (totalPages > startPage) {
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

        return reviewList;
    }

    @Override
    public Observable<List<Movie>> readPopularListPage(int page) {
        ApiService service = getServiceInstance();

        Observable<List<Movie>> returnList =
                service.getPopular(getApiKey(), page)
                        .subscribeOn(Schedulers.io())
                        .doOnNext(new Consumer<ListPageResponse>() {
                            @Override
                            public void accept(ListPageResponse listPageResponse) throws Exception {
                                updatePopularTotals(listPageResponse.totalPages, listPageResponse.totalResults);
                            }
                        })
                        .map(new Function<ListPageResponse, List<Movie>>() {
                            @Override
                            public List<Movie> apply(ListPageResponse listPageResponse) throws Exception {
                                return listPageResponse.getPopularListPageInstance();
                            }
                        });

        return returnList;
    }

    private void updatePopularTotals(int totalPages, int totalItems) {
        // FIXME: 01.03.2017 We must use unified references storage
        //Utils.setCachePreference(context, R.string.total_popular_pages, totalPages);
        //Utils.setCachePreference(context, R.string.total_popular_items, totalItems);
    }

    @Override
    public Observable<List<Movie>> readTopratedListPage(int page) {
        ApiService service = getServiceInstance();

        Observable<List<Movie>> returnList =
                service.getToprated(getApiKey(), page)
                        .subscribeOn(Schedulers.io())
                        .doOnNext(new Consumer<ListPageResponse>() {
                            @Override
                            public void accept(ListPageResponse listPageResponse) throws Exception {
                                updateTopratedTotals(listPageResponse.totalPages, listPageResponse.totalResults);
                            }
                        })
                        .map(new Function<ListPageResponse, List<Movie>>() {
                            @Override
                            public List<Movie> apply(ListPageResponse listPageResponse) throws Exception {
                                return listPageResponse.getTopratedListPageInstance();
                            }
                        });

        return returnList;
    }

    private void updateTopratedTotals(int totalPages, int totalItems) {
        // FIXME: 01.03.2017 We must use unified references storage
        //Utils.setCachePreference(context, R.string.total_toprated_pages, totalPages);
        //Utils.setCachePreference(context, R.string.total_toprated_items, totalItems);
    }
}
