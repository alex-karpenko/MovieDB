package com.example.leshik.moviedb.data;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.example.leshik.moviedb.R;
import com.example.leshik.moviedb.Utils;
import com.example.leshik.moviedb.data.api.ListPageResponse;
import com.example.leshik.moviedb.data.api.MovieResponse;
import com.example.leshik.moviedb.data.api.ReviewsResponse;
import com.example.leshik.moviedb.data.api.VideosResponse;
import com.example.leshik.moviedb.data.interfaces.ApiService;
import com.example.leshik.moviedb.data.model.Movie;
import com.example.leshik.moviedb.data.model.Review;
import com.example.leshik.moviedb.data.model.Video;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;

/**
 * Created by alex on 2/14/17.
 */

class DataStorage {
    private static final String TAG = "DataStorage";
    private Context context;

    public DataStorage(Context context) {
        if (!(context instanceof Application)) {
            Log.i(TAG, "DataStorage: context is not Application");
            throw new IllegalArgumentException("DataStorage: context is not Application");
        }
        this.context = context;
    }

    private ApiService getApiServiceInstance() {
        return DataUtils.getServiceInstance(DataUtils.getRetrofitInstance(Utils.getBaseApiUrl()));
    }

    Movie readMovieFromDb(long movieId) {
        Realm realm = DataUtils.getRealmInstance(context);
        Movie movie = findMovieFromDb(realm, movieId);
        realm.close();

        return movie;
    }

    private Movie findMovieFromDb(Realm realm, long movieId) {
        Movie movie = realm.where(Movie.class).equalTo("movieId", movieId).findFirst();
        if (movie != null && movie.isValid())
            return realm.copyFromRealm(movie);
        else return null;
    }

    long writeMovieToDb(final Movie newMovie) {
        Realm realm = DataUtils.getRealmInstance(context);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm transactionRealm) {
                Movie movie = findMovieFromDb(transactionRealm, newMovie.getMovieId());
                if (movie != null) newMovie.updateNullFields(movie);
                transactionRealm.copyToRealmOrUpdate(newMovie);
            }
        });

        realm.close();

        return newMovie.getMovieId();
    }

    Observable<Movie> readMovieFromApi(long movieId) {
        ApiService service = getApiServiceInstance();

        Observable<Movie> movie =
                service.getMovie(movieId, Utils.getApiKey())
                        .subscribeOn(Schedulers.io())
                        .map(new Function<MovieResponse, Movie>() {
                            @Override
                            public Movie apply(MovieResponse movieResponse) throws Exception {
                                return movieResponse.getMovieInstance();
                            }
                        });

        return movie;
    }

    Observable<List<Video>> readVideoListFromApi(long movieId) {
        ApiService service = getApiServiceInstance();

        Observable<List<Video>> videoList =
                service.getVideos(movieId, Utils.getApiKey())
                        .subscribeOn(Schedulers.io())
                        .map(new Function<VideosResponse, List<Video>>() {
                            @Override
                            public List<Video> apply(VideosResponse videosResponse) throws Exception {
                                return videosResponse.getVideoListInstance();
                            }
                        });

        return videoList;
    }

    Observable<List<Review>> readReviewListFromApi(final long movieId) {
        return readReviewListPageFromApi(movieId, 1);
    }

    private Observable<List<Review>> readReviewListPageFromApi(final long movieId, final int startPage) {
        final ApiService service = getApiServiceInstance();

        Observable<List<Review>> reviewList =
                service.getReviews(movieId, Utils.getApiKey(), startPage)
                        .subscribeOn(Schedulers.io())
                        .map(new Function<ReviewsResponse, List<Review>>() {
                            @Override
                            public List<Review> apply(ReviewsResponse reviewsResponse) throws Exception {
                                int totalPages = reviewsResponse.totalPages;
                                final List<Review> newList = new ArrayList<>();

                                newList.addAll(reviewsResponse.getReviewListInstance());

                                if (totalPages > startPage) {
                                    readReviewListPageFromApi(movieId, startPage + 1)
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

    void setFavoriteFlag(final long movieId, final boolean isFavorite) {
        Realm realm = DataUtils.getRealmInstance(context);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Movie movie = realm.where(Movie.class).equalTo("movieId", movieId).findFirst();
                if (movie != null) {
                    if (isFavorite) {
                        Integer newPosition;
                        Number max = realm.where(Movie.class).max("favoritePosition");
                        if (max == null) newPosition = 1;
                        else newPosition = max.intValue() + 1;
                        movie.setFavoritePosition(newPosition);
                    } else {
                        movie.setFavoritePosition(-1);
                    }
                }
            }
        });

        realm.close();
    }

    Observable<List<Movie>> readPopularListPageFromApi(int page) {
        ApiService service = getApiServiceInstance();

        Observable<List<Movie>> returnList =
                service.getPopular(Utils.getApiKey(), page)
                        .subscribeOn(Schedulers.io())
                        .doOnNext(new Consumer<ListPageResponse>() {
                            @Override
                            public void accept(ListPageResponse listPageResponse) throws Exception {
                                Utils.setCachePreference(context, R.string.total_popular_pages, listPageResponse.totalPages);
                                Utils.setCachePreference(context, R.string.total_popular_items, listPageResponse.totalResults);
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

}
