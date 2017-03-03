package com.example.leshik.moviedb.data;

import android.content.Context;

import com.example.leshik.moviedb.Utils;
import com.example.leshik.moviedb.data.interfaces.MovieInteractor;
import com.example.leshik.moviedb.data.model.Movie;
import com.example.leshik.moviedb.data.model.Review;
import com.example.leshik.moviedb.data.model.Video;

import java.util.Calendar;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Leshik on 17.02.2017.
 */

public class MovieRepository implements MovieInteractor {
    private static final String TAG = "MovieRepository";
    private final TmdbNetworkStorage mNetworkStorage;
    private final RealmPersistentStorage mPersistentStorage;

    public MovieRepository(Context context) {
        // TODO: 01.03.2017 Must be refactored to reduce size of the such large method
        mNetworkStorage = new TmdbNetworkStorage(Utils.getBaseApiUrl());
        mPersistentStorage = new RealmPersistentStorage(context);
    }

    @Override
    public Observable<Movie> getMovieObservable(long movieId) {
        // TODO: 03.03.2017 check movie expiration time and update movie from API only when need
        Observable<Movie> movieFromCache = mPersistentStorage.getMovieObservable(movieId);
        Observable<Movie> movieFromNetwork = buildMovieFromNetworkObservable(movieId)
                .subscribeOn(Schedulers.io())
                .doOnNext(new Consumer<Movie>() {
                    @Override
                    public void accept(Movie movie) throws Exception {
                        mPersistentStorage.updateOrInsertMovie(movie);
                    }
                });

        return movieFromNetwork.mergeWith(movieFromCache);
    }

    private Observable<Movie> buildMovieFromNetworkObservable(long movieId) {
        return mNetworkStorage.readMovie(movieId)
                .zipWith(mNetworkStorage.readVideoList(movieId),
                        new BiFunction<Movie, List<Video>, Movie>() {
                            @Override
                            public Movie apply(Movie movie, List<Video> videos) throws Exception {
                                movie.setVideos(videos);
                                return movie;
                            }
                        })
                .zipWith(mNetworkStorage.readReviewList(movieId),
                        new BiFunction<Movie, List<Review>, Movie>() {
                            @Override
                            public Movie apply(Movie movie, List<Review> reviews) throws Exception {
                                movie.setReviews(reviews);
                                return movie;
                            }
                        });
    }

    private boolean isExpiredMovie(Movie movie) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        long cacheUpdateInterval = Utils.getCacheUpdateInterval();

        // 1) cache exist but expired
        // 2) cache has never been updated and update interval is "never"
        return ((movie.getLastUpdate() + cacheUpdateInterval) <= currentTime && cacheUpdateInterval > 0)
                || (movie.getLastUpdate() <= 0 && cacheUpdateInterval <= 0);
    }

    @Override
    public void invertFavorite(long movieId) {
        mPersistentStorage.invertFavorite(movieId);
    }

    @Override
    public void forceRefresh(long movieId) {
        // TODO: 03.03.2017
    }
}
