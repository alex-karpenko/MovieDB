package com.example.leshik.moviedb.data;

import android.content.Context;
import android.util.Log;

import com.example.leshik.moviedb.Utils;
import com.example.leshik.moviedb.data.interfaces.MovieInteractor;
import com.example.leshik.moviedb.data.model.Movie;
import com.example.leshik.moviedb.data.model.Review;
import com.example.leshik.moviedb.data.model.Video;

import java.util.Calendar;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

import static io.reactivex.Observable.just;

/**
 * Created by Leshik on 17.02.2017.
 */

public class MovieRepository implements MovieInteractor {
    private static final String TAG = "MovieRepository";
    private final TmdbNetworkStorage mNetworkStorage;
    private final RealmPersistentStorage mPersistentStorage;
    private static BehaviorSubject<MovieRequest> movieSubject = null;
    private static Observable<Movie> movieObservable = null;

    public MovieRepository(Context context) {
        // TODO: 01.03.2017 Must be refactored to reduce size of the such large method
        mNetworkStorage = new TmdbNetworkStorage(Utils.getBaseApiUrl());
        mPersistentStorage = new RealmPersistentStorage(context);

        if (movieSubject == null || movieObservable == null) {
            movieSubject = BehaviorSubject.create();
            movieObservable = movieSubject
                    .flatMap(new Function<MovieRequest, ObservableSource<Movie>>() {
                        @Override
                        public ObservableSource<Movie> apply(MovieRequest request) throws Exception {
                            Observable<Movie> movieFromNetwork = mNetworkStorage.readMovie(request.getMovieId())
                                    .zipWith(mNetworkStorage.readVideoList(request.getMovieId()),
                                            new BiFunction<Movie, List<Video>, Movie>() {
                                                @Override
                                                public Movie apply(Movie movie, List<Video> videos) throws Exception {
                                                    movie.setVideos(videos);
                                                    return movie;
                                                }
                                            })
                                    .zipWith(mNetworkStorage.readReviewList(request.getMovieId()),
                                            new BiFunction<Movie, List<Review>, Movie>() {
                                                @Override
                                                public Movie apply(Movie movie, List<Review> reviews) throws Exception {
                                                    movie.setReviews(reviews);
                                                    return movie;
                                                }
                                            })
                                    .doOnNext(new Consumer<Movie>() {
                                        @Override
                                        public void accept(Movie movie) throws Exception {
                                            mPersistentStorage.writeMovie(movie);
                                        }
                                    });

                            if (request.isForceRefresh()) {
                                Log.i(TAG, "apply: from API");
                                return movieFromNetwork;
                            }

                            Movie movieFromCache = mPersistentStorage.readMovie(request.getMovieId());
                            if (movieFromCache == null) {
                                Log.i(TAG, "apply: from API");
                                return movieFromNetwork;
                            }

                            if (request.isInvertFavorite()) {
                                mPersistentStorage.setFavoriteFlag(request.getMovieId(), !movieFromCache.isFavorite());
                                movieFromCache = mPersistentStorage.readMovie(request.getMovieId());
                                Log.i(TAG, "apply: from DB, invert favorite");
                                return Observable.just(movieFromCache);
                            }

                            long currentTime = Calendar.getInstance().getTimeInMillis();
                            long cacheUpdateInterval = Utils.getCacheUpdateInterval();
                            if (((movieFromCache.getLastUpdate() + cacheUpdateInterval) <= currentTime && cacheUpdateInterval > 0)
                                    || (movieFromCache.getLastUpdate() <= 0 && cacheUpdateInterval <= 0)) {
                                // 1) cache exist but expired
                                // 2) cache has never been updated and update interval is "never"
                                //    - return cache data, and refresh cache from api
                                Log.i(TAG, "apply: from DB+API");
                                return movieFromNetwork.mergeWith(just(movieFromCache));
                            }

                            Log.i(TAG, "apply: from DB");
                            return Observable.just(movieFromCache);
                        }
                    });
        }
    }

    @Override
    public Observable<Movie> getMovieObservable() {
        return movieObservable.subscribeOn(Schedulers.io());
    }

    @Override
    public void sendRequest(MovieRequest request) {
        movieSubject.onNext(request);
    }
}
