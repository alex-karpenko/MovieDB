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
import com.example.leshik.moviedb.utils.EventsUtils;
import com.example.leshik.moviedb.utils.NetworkUtils;

import java.util.Calendar;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Leshik on 17.02.2017.
 */

public class MovieRepository implements MovieInteractor {
    private static final String TAG = "MovieRepository";
    private final NetworkDataSource networkDataSource;
    private final CacheStorage cacheStorage;
    private final PreferenceInterface prefStorage;
    private final Context context;

    public MovieRepository(Context context) {
        prefStorage = PreferenceStorage.getInstance(context);
        networkDataSource = new TmdbNetworkDataSource(prefStorage.getBaseApiUrl());
        cacheStorage = new RealmCacheStorage(context);
        this.context = context;
    }

    @Override
    public Observable<Movie> getMovie(final long movieId) {
        final Observable<Movie> movieFromCache = cacheStorage.getMovieObservable(movieId);
        final Observable<Movie> movieFromNetwork = buildMovieFromNetworkObservable(movieId);

        Observable<Movie> returnMovie = movieFromCache.flatMap(new Function<Movie, ObservableSource<Movie>>() {
            @Override
            public ObservableSource<Movie> apply(@NonNull Movie movie) throws Exception {

                if (isEmpty(movie)) return movieFromNetwork.map(new Function<Movie, Movie>() {
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
        if (NetworkUtils.isNetworkConnected(context)) {
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
        } else {
            EventsUtils.postEvent(EventsUtils.EventType.NetworkUnavailable);
        }

        return false;
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
