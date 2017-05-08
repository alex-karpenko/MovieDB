package com.example.leshik.moviedb.data;

import android.content.Context;

import com.example.leshik.moviedb.data.interfaces.CacheStorage;
import com.example.leshik.moviedb.data.interfaces.MovieInteractor;
import com.example.leshik.moviedb.data.interfaces.NetworkDataSource;
import com.example.leshik.moviedb.data.interfaces.PreferenceInterface;
import com.example.leshik.moviedb.data.model.Movie;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

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
        final Observable<Movie> movieFromCache = cacheStorage.getMovie(movieId)
                .filter(new Predicate<Movie>() {
                    @Override
                    public boolean test(@NonNull Movie movie) throws Exception {
                        return !movie.isEmpty();
                    }
                });

        final Observable<Movie> movieFromNetwork = networkDataSource.readMovieFull(movieId)
                .doOnNext(new Consumer<Movie>() {
                    @Override
                    public void accept(@NonNull Movie movie) throws Exception {
                        cacheStorage.updateOrInsertMovieAsync(movie);
                    }
                });

        return Observable.concat(movieFromCache, movieFromNetwork);
    }

    @Override
    public void invertFavorite(long movieId) {
        cacheStorage.invertFavorite(movieId);
    }
}
