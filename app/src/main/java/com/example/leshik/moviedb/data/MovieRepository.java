package com.example.leshik.moviedb.data;

import android.content.Context;

import com.example.leshik.moviedb.data.interfaces.CacheStorage;
import com.example.leshik.moviedb.data.interfaces.MovieInteractor;
import com.example.leshik.moviedb.data.interfaces.NetworkDataSource;
import com.example.leshik.moviedb.data.interfaces.PreferenceInterface;
import com.example.leshik.moviedb.data.model.Movie;
import com.example.leshik.moviedb.utils.EventsUtils;

import java.util.Calendar;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

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
        final Observable<Movie> movieFromCache = cacheStorage.getMovie(movieId);
        final Observable<Movie> movieFromNetwork = networkDataSource.readMovieFull(movieId);


        Observable<Movie> returnMovie = movieFromCache.flatMap(new Function<Movie, ObservableSource<Movie>>() {
            @Override
            public ObservableSource<Movie> apply(@NonNull Movie movie) throws Exception {

                if (isEmpty(movie)) return movieFromNetwork
                        .map(new Function<Movie, Movie>() {
                            @Override
                            public Movie apply(@NonNull Movie movie) throws Exception {
                                cacheStorage.updateOrInsertMovieAsync(movie);
                                return movie;
                            }
                        });

                if (isExpired(movie))
                    return movieFromCache.concatWith(movieFromNetwork.map(new Function<Movie, Movie>() {
                        @Override
                        public Movie apply(@NonNull Movie movie) throws Exception {
                            cacheStorage.updateOrInsertMovieAsync(movie);
                            return movie;
                        }
                    }));

                return movieFromCache;
            }
        });

        return returnMovie;
    }

    private boolean isEmpty(Movie movie) {
        return movie == null || movie.isEmpty();
    }

    private boolean isExpired(Movie movie) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        long cacheUpdateInterval = prefStorage.getCacheUpdateInterval();

        // 1) cache exist but expired
        // 2) cache has never been updated and update interval is "never"
        return ((movie.getLastUpdate() + cacheUpdateInterval) <= currentTime && cacheUpdateInterval > 0)
                || (movie.getLastUpdate() <= 0 && cacheUpdateInterval <= 0);

    }

    @Override
    public void invertFavorite(long movieId) {
        cacheStorage.invertFavorite(movieId);
    }

    @Override
    public boolean forceRefresh(long movieId) {
        // Simply set last update time to 0
        // after this we have to unsubscribe from movie and subscribe again
        // TODO: 3/30/17 May be delete this method???
        try {
            cacheStorage.getMovie(movieId)
                    .flatMap(new Function<Movie, ObservableSource<Movie>>() {
                        @Override
                        public ObservableSource<Movie> apply(@NonNull Movie movie) throws Exception {
                            movie.setLastUpdate(0L);
                            return Observable.just(movie);
                        }
                    })
                    .map(new Function<Movie, Long>() {
                        @Override
                        public Long apply(@NonNull Movie movie) throws Exception {
                            return cacheStorage.updateOrInsertMovie(movie);
                        }
                    })
                    .blockingSubscribe();
            return true;
        } catch (Exception e) {
            EventsUtils.postEvent(EventsUtils.EventType.NetworkUnavailable);
        }

        return false;
    }
}
