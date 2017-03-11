package com.example.leshik.moviedb.data;

import android.content.Context;
import android.util.Log;

import com.example.leshik.moviedb.data.interfaces.CacheStorage;
import com.example.leshik.moviedb.data.interfaces.MovieListInteractor;
import com.example.leshik.moviedb.data.interfaces.NetworkDataSource;
import com.example.leshik.moviedb.data.interfaces.PreferenceInterface;
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
    private final NetworkDataSource networkDataSource;
    private final CacheStorage cacheStorage;
    private final PreferenceInterface prefStorage;
    private Context context;

    public MovieListRepository(Context context) {
        prefStorage = PreferenceStorage.getInstance(context);
        networkDataSource = new TmdbNetworkDataSource(prefStorage.getBaseApiUrl());
        cacheStorage = new RealmCacheStorage(context);
        this.context = context;
    }

    @Override
    public Observable<List<Movie>> getList(final MovieListType listType) {
        Observable<List<Movie>> listFromCache = cacheStorage.getMovieListObservable(listType);

        return listFromCache.doOnNext(new Consumer<List<Movie>>() {
            @Override
            public void accept(List<Movie> movies) throws Exception {
                if (isExpiredOrEmptyList(movies, listType))
                    forceRefreshList(listType);
            }
        })
                .filter(new Predicate<List<Movie>>() {
                    @Override
                    public boolean test(List<Movie> movies) throws Exception {
                        return movies.size() > 0;
                    }
                });
    }

    private boolean isExpiredOrEmptyList(List<Movie> movies, MovieListType listType) {
        if (listType.isLocalOnly()) return false;

        long currentTime = Calendar.getInstance().getTimeInMillis();
        long cacheUpdateInterval = prefStorage.getCacheUpdateInterval();
        return (movies == null
                || movies.size() <= 0
                || (getLastUpdateTime(listType) + cacheUpdateInterval) <= currentTime && cacheUpdateInterval > 0)
                || (getLastUpdateTime(listType) <= 0 && cacheUpdateInterval <= 0);
    }

    private long getLastUpdateTime(MovieListType listType) {
        return prefStorage.getMovieListUpdateTimestamp(listType);
    }

    @Override
    public void forceRefreshList(MovieListType listType) {
        Log.i(TAG, "forceRefreshList: " + listType);
        if (listType.isLocalOnly()) return;

        if (listType.isFromNetwork()) {
            cacheStorage.clearMovieListPositionsAndInsertOrUpdateData(listType,
                    networkDataSource.readMovieListPage(listType, 1));
            prefStorage.updateMovieListUpdateTimestampToCurrent(listType);
            prefStorage.setMovieListTotalPages(listType, networkDataSource.getTotalListPages(listType));
            prefStorage.setMovieListTotalItems(listType, networkDataSource.getTotalListItems(listType));
        }
    }

    @Override
    public void loadNextPage(MovieListType listType) {
        if (listType.isFromNetwork()) {
            int lastPageInStorage = cacheStorage.getMovieListLastPageNumber(listType);
            int lastPageInNetwork = networkDataSource.getTotalListPages(listType);

            if (lastPageInNetwork <= 0)
                lastPageInNetwork = prefStorage.getMovieListTotalPages(listType);

            if (lastPageInStorage < lastPageInNetwork) {
                cacheStorage.insertOrUpdateMovieList(listType,
                        networkDataSource.readMovieListPage(listType, lastPageInStorage + 1));
                prefStorage.setMovieListTotalPages(listType, networkDataSource.getTotalListPages(listType));
                prefStorage.setMovieListTotalItems(listType, networkDataSource.getTotalListItems(listType));
            }
        }
    }
}
