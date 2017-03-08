package com.example.leshik.moviedb.data;

import android.content.Context;
import android.util.Log;

import com.example.leshik.moviedb.data.interfaces.MovieListInteractor;
import com.example.leshik.moviedb.data.model.Movie;
import com.example.leshik.moviedb.utils.Utils;

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
    private final TmdbNetworkDataSource networkDataSource;
    private final RealmCacheStorage cacheStorage;
    private Context context;

    public MovieListRepository(Context context) {
        networkDataSource = new TmdbNetworkDataSource(context, Utils.getBaseApiUrl());
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
        if (listType == MovieListType.Favorite) return false;

        long currentTime = Calendar.getInstance().getTimeInMillis();
        long cacheUpdateInterval = Utils.getCacheUpdateInterval();
        return (movies == null
                || movies.size() <= 0
                || (getLastUpdateTime(listType) + cacheUpdateInterval) <= currentTime && cacheUpdateInterval > 0)
                || (getLastUpdateTime(listType) <= 0 && cacheUpdateInterval <= 0);
    }

    private long getLastUpdateTime(MovieListType listType) {
        return Utils.getLongCachePreference(context, getLastUpdatePrefKey(listType));
    }

    private String getLastUpdatePrefKey(MovieListType listType) {
        return listType.toString() + "_last_update";
    }

    @Override
    public void forceRefreshList(MovieListType listType) {
        Log.i(TAG, "forceRefreshList: " + listType);
        if (listType == MovieListType.Favorite) return;

        cacheStorage.clearMovieListPositionsAndInsertOrUpdateData(listType,
                networkDataSource.readMovieListPage(listType, 1));

        long currentTime = Calendar.getInstance().getTimeInMillis();
        Utils.setCachePreference(context, getLastUpdatePrefKey(listType), currentTime);
    }

    @Override
    public void loadNextPage(MovieListType listType) {
        if (listType == MovieListType.Favorite) return;

        int lastPageInStorage = cacheStorage.getMovieListLastPageNumber(listType);
        int lastPageInNetwork = networkDataSource.getTotalListPages(listType);
        if (lastPageInStorage < lastPageInNetwork)
            cacheStorage.insertOrUpdateMovieList(listType,
                    networkDataSource.readMovieListPage(listType, lastPageInStorage + 1));

    }
}
