package com.example.leshik.moviedb.data;

import android.content.Context;
import android.util.Log;

import com.example.leshik.moviedb.data.interfaces.CacheStorage;
import com.example.leshik.moviedb.data.interfaces.MovieListInteractor;
import com.example.leshik.moviedb.data.interfaces.NetworkDataSource;
import com.example.leshik.moviedb.data.interfaces.PreferenceInterface;
import com.example.leshik.moviedb.data.model.Movie;
import com.example.leshik.moviedb.utils.EventsUtils;
import com.example.leshik.moviedb.utils.NetworkUtils;

import java.util.Calendar;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * Created by alex on 3/5/17.
 * <p>
 * Implementation of the MovieListInteractor
 * with cache in Realm DB and network source from TheMovieDataBase
 */

public class MovieListRepository implements MovieListInteractor {
    private static final String TAG = "MovieListRepository";
    private final NetworkDataSource networkDataSource;
    private final CacheStorage cacheStorage;
    private final PreferenceInterface prefStorage;
    private final Context context;

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
                if (isExpiredOrEmptyList(movies, listType)) {
                    EventsUtils.postEvent(EventsUtils.EventType.Refreshing);
                    forceRefreshList(listType);
                }
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
    public boolean forceRefreshList(MovieListType listType) {
        Log.i(TAG, "forceRefreshList: " + listType);
        if (listType.isLocalOnly()) return false;

        if (listType.isFromNetwork()) {
            if (NetworkUtils.isNetworkConnected(context)) {
                cacheStorage.clearMovieListPositionsAndInsertOrUpdateData(listType,
                        networkDataSource.readMovieListPage(listType, 1));
                prefStorage.setMovieListUpdateTimestampToCurrent(listType);
                prefStorage.setMovieListTotalPages(listType, networkDataSource.getTotalListPages(listType));
                prefStorage.setMovieListTotalItems(listType, networkDataSource.getTotalListItems(listType));
                return true;
            } else {
                EventsUtils.postEvent(EventsUtils.EventType.NetworkUnavailable);
            }
        }

        return false;
    }

    @Override
    public boolean loadNextPage(MovieListType listType) {
        if (!listType.isFromNetwork()) return false;

        int lastPageInStorage = cacheStorage.getMovieListLastPageNumber(listType);
        int lastPageInNetwork = networkDataSource.getTotalListPages(listType);
        Log.i(TAG, "loadNextPage: last_cache_page=" + lastPageInStorage);
        Log.i(TAG, "loadNextPage: last_network_page=" + lastPageInNetwork);

        if (lastPageInNetwork <= 0) {
            lastPageInNetwork = prefStorage.getMovieListTotalPages(listType);
            Log.i(TAG, "loadNextPage: last_network_page from prefs=" + lastPageInNetwork);
        }

        if ((lastPageInStorage < lastPageInNetwork || lastPageInNetwork <= 0)
                && NetworkUtils.isNetworkConnected(context)) {
            cacheStorage.insertOrUpdateMovieList(listType,
                    networkDataSource.readMovieListPage(listType, lastPageInStorage + 1));
            prefStorage.setMovieListTotalPages(listType, networkDataSource.getTotalListPages(listType));
            prefStorage.setMovieListTotalItems(listType, networkDataSource.getTotalListItems(listType));
            return true;
        } else {
            return false;
        }
    }
}
