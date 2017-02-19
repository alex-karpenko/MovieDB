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
    private final DataStorage mDataStorage;
    private static BehaviorSubject<MovieRequest> movieSubject = null;
    private static Observable<Movie> movieObservable = null;

    public MovieRepository(Context context) {
        mDataStorage = new DataStorage(context);

        if (movieSubject == null || movieObservable == null) {
            movieSubject = BehaviorSubject.create();
            movieObservable = movieSubject
                    .flatMap(new Function<MovieRequest, ObservableSource<Movie>>() {
                        @Override
                        public ObservableSource<Movie> apply(MovieRequest request) throws Exception {
                            Observable<Movie> movieFromApi = mDataStorage.readMovieFromApi(request.getMovieId())
                                    .zipWith(mDataStorage.readVideoListFromApi(request.getMovieId()),
                                            new BiFunction<Movie, List<Video>, Movie>() {
                                                @Override
                                                public Movie apply(Movie movie, List<Video> videos) throws Exception {
                                                    movie.setVideos(videos);
                                                    return movie;
                                                }
                                            })
                                    .zipWith(mDataStorage.readReviewListFromApi(request.getMovieId()),
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
                                            mDataStorage.writeMovieToDb(movie);
                                        }
                                    });

                            if (request.isForceRefresh()) {
                                Log.i(TAG, "apply: from API");
                                return movieFromApi;
                            }

                            Movie movieFromDb = mDataStorage.readMovieFromDb(request.getMovieId());
                            if (movieFromDb == null) {
                                Log.i(TAG, "apply: from API");
                                return movieFromApi;
                            }

                            if (request.isInvertFavorite()) {
                                mDataStorage.setFavoriteFlag(request.getMovieId(), !movieFromDb.isFavorite());
                                movieFromDb = mDataStorage.readMovieFromDb(request.getMovieId());
                                Log.i(TAG, "apply: from DB, invert favorite");
                                return Observable.just(movieFromDb);
                            }

                            long currentTime = Calendar.getInstance().getTimeInMillis();
                            long cacheUpdateInterval = Utils.getCacheUpdateInterval();
                            if (((movieFromDb.getLastUpdate() + cacheUpdateInterval) <= currentTime && cacheUpdateInterval > 0)
                                    || (movieFromDb.getLastUpdate() <= 0 && cacheUpdateInterval <= 0)) {
                                // 1) cache exist but expired
                                // 2) cache has never been updated and update interval is "never"
                                //    - return cache data, and refresh cache from api
                                Log.i(TAG, "apply: from DB+API");
                                return movieFromApi.mergeWith(just(movieFromDb));
                            }

                            Log.i(TAG, "apply: from DB");
                            return Observable.just(movieFromDb);
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
