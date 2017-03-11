package com.example.leshik.moviedb.data;

import android.content.Context;
import android.util.Log;

import com.example.leshik.moviedb.data.interfaces.CacheStorage;
import com.example.leshik.moviedb.data.interfaces.MovieInteractor;
import com.example.leshik.moviedb.data.interfaces.NetworkDataSource;
import com.example.leshik.moviedb.data.interfaces.PreferenceInterface;
import com.example.leshik.moviedb.data.model.Movie;
import com.example.leshik.moviedb.data.model.Review;
import com.example.leshik.moviedb.data.model.Video;

import java.util.Calendar;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Leshik on 17.02.2017.
 */

public class MovieRepository implements MovieInteractor {
    private static final String TAG = "MovieRepository";
    private final NetworkDataSource networkDataSource;
    private final CacheStorage cacheStorage;
    private final PreferenceInterface prefStorage;

    public MovieRepository(Context context) {
        prefStorage = PreferenceStorage.getInstance(context);
        networkDataSource = new TmdbNetworkDataSource(prefStorage.getBaseApiUrl());
        cacheStorage = new RealmCacheStorage(context);
    }

    @Override
    public Observable<Movie> getMovie(final long movieId) {
        Observable<Movie> movieFromCache = cacheStorage.getMovieObservable(movieId);

        return movieFromCache.doOnNext(new Consumer<Movie>() {
            @Override
            public void accept(Movie movie) throws Exception {
                if (isExpiredOrEmpty(movie)) {
                    Log.i(TAG, "expired or empty movie, refreshing...");
                    forceRefresh(movieId);
                }
            }
        })
                .filter(new Predicate<Movie>() {
                    @Override
                    public boolean test(Movie movie) throws Exception {
                        return !movie.isEmpty();
                    }
                });
    }

    private boolean isExpiredOrEmpty(Movie movie) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        long cacheUpdateInterval = prefStorage.getCacheUpdateInterval();

        // 1) obviously :)
        // 2) ...
        // 3) cache exist but expired
        // 4) cache has never been updated and update interval is "never"
        return (movie == null
                || movie.isEmpty()
                || (movie.getLastUpdate() + cacheUpdateInterval) <= currentTime && cacheUpdateInterval > 0)
                || (movie.getLastUpdate() <= 0 && cacheUpdateInterval <= 0);
    }

    @Override
    public void invertFavorite(long movieId) {
        cacheStorage.invertFavorite(movieId);
    }

    @Override
    public void forceRefresh(long movieId) {
        // TODO: 04.03.2017 Must be checked for memory leaks
        buildMovieFromNetworkObservable(movieId)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<Movie>() {
                    @Override
                    public void accept(Movie movie) throws Exception {
                        Log.i(TAG, "forceRefresh: +");
                        cacheStorage.updateOrInsertMovie(movie);
                    }
                });
    }

    private Observable<Movie> buildMovieFromNetworkObservable(long movieId) {
        return networkDataSource.readMovie(movieId)
                .zipWith(networkDataSource.readVideoList(movieId),
                        new BiFunction<Movie, List<Video>, Movie>() {
                            @Override
                            public Movie apply(Movie movie, List<Video> videos) throws Exception {
                                movie.setVideos(videos);
                                return movie;
                            }
                        })
                .zipWith(networkDataSource.readReviewList(movieId),
                        new BiFunction<Movie, List<Review>, Movie>() {
                            @Override
                            public Movie apply(Movie movie, List<Review> reviews) throws Exception {
                                movie.setReviews(reviews);
                                return movie;
                            }
                        });
    }
}
