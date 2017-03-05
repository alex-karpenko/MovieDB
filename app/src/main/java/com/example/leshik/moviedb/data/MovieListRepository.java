package com.example.leshik.moviedb.data;

import android.content.Context;

import com.example.leshik.moviedb.Utils;
import com.example.leshik.moviedb.data.interfaces.MovieListInteractor;
import com.example.leshik.moviedb.data.model.Movie;

import java.util.Calendar;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * Created by alex on 3/5/17.
 */

public class MovieListRepository implements MovieListInteractor {
    private static final String TAG = "MovieListRepository";
    private final TmdbNetworkStorage networkStorage;
    private final RealmPersistentStorage persistentStorage;
    private MovieListType listType;

    public MovieListRepository(Context context, MovieListType listType) {
        networkStorage = new TmdbNetworkStorage(Utils.getBaseApiUrl());
        persistentStorage = new RealmPersistentStorage(context);
        this.listType = listType;
    }

    @Override
    public Observable<List<Movie>> getList() {
        Observable<List<Movie>> listFromCache = persistentStorage.getMovieListObservable(listType);

        return listFromCache.doOnNext(new Consumer<List<Movie>>() {
            @Override
            public void accept(List<Movie> movies) throws Exception {
                if (isExpiredOrEmptyList(movies))
                    forceRefreshList();
            }
        })
                .filter(new Predicate<List<Movie>>() {
                    @Override
                    public boolean test(List<Movie> movies) throws Exception {
                        return movies.size() > 0;
                    }
                });
    }

    private boolean isExpiredOrEmptyList(List<Movie> movies) {
        if (listType == MovieListType.Favorite) return false;

        long currentTime = Calendar.getInstance().getTimeInMillis();
        long cacheUpdateInterval = Utils.getCacheUpdateInterval();
        return (movies == null
                || movies.size() <= 0
                || (getLastUpdateTime() + cacheUpdateInterval) <= currentTime && cacheUpdateInterval > 0)
                || (getLastUpdateTime() <= 0 && cacheUpdateInterval <= 0);
    }

    private long getLastUpdateTime() {
        // FIXME: 3/5/17 Have to implement saving and reading update time
        return 0;
    }

    @Override
    public void forceRefreshList() {
        // TODO: 3/5/17  Implement this

    }

    @Override
    public void loadNextPage() {

    }
}
