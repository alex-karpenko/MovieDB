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
    private static int[] totalListPages;
    private static int[] totalListItems;

    public TmdbNetworkStorage(String apiUrl) {
        retrofit = new Retrofit.Builder()
                .baseUrl(apiUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        // TODO: 3/5/17 Somewhat does not inspire me this approach ...
        totalListPages = new int[]{0, 0, 0};
        totalListItems = new int[]{0, 0, 0};
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
    public Observable<List<Movie>> readMovieListPage(final MovieListType listType, int page) {
        Observable<List<Movie>> returnList =
                getListResponseObservable(listType, page)
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

        return returnList;
    }

    Observable<ListPageResponse> getListResponseObservable(MovieListType listType, int page) {
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
                throw new IllegalArgumentException("NetworkStorage: list type does not supported");
        }

        return returnObservable;
    }

    private void updateListTotals(MovieListType listType, int totalPages, int totalItems) {
        totalListPages[listType.getIndex()] = totalPages;
        totalListItems[listType.getIndex()] = totalItems;
    }

    @Override
    public int getTotalListPages(MovieListType listType) {
        return totalListPages[listType.getIndex()];
    }

    @Override
    public int getTotalListItems(MovieListType listType) {
        return totalListItems[listType.getIndex()];
    }
}
