package com.example.leshik.moviedb.data;

import android.content.Context;

import com.example.leshik.moviedb.data.interfaces.CacheStorage;
import com.example.leshik.moviedb.data.interfaces.MovieListInteractor;
import com.example.leshik.moviedb.data.interfaces.NetworkDataSource;
import com.example.leshik.moviedb.data.interfaces.PreferenceInterface;
import com.example.leshik.moviedb.data.model.Movie;
import com.example.leshik.moviedb.data.model.MovieListViewItem;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

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

    public MovieListRepository(Context context) {
        prefStorage = PreferenceStorage.getInstance(context);
        networkDataSource = new TmdbNetworkDataSource(prefStorage.getBaseApiUrl());
        cacheStorage = new RealmCacheStorage(context);
    }

    @Override
    public Observable<MovieListViewItem> getList(final MovieListType listType, Observable<Integer> nextViewItem) {
        final int pageSize = prefStorage.getCachePageSize();

        return nextViewItem
                .map(new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(@NonNull Integer nextViewItem) throws Exception {
                        return (nextViewItem / prefStorage.getCachePageSize()) + 1;
                    }
                })
                .distinctUntilChanged()
                .concatMap(new Function<Integer, ObservableSource<MovieListViewItem>>() {
                    @Override
                    public ObservableSource<MovieListViewItem> apply(@NonNull final Integer pageNumber) throws Exception {
                        Observable<MovieListViewItem> listFromCache = cacheStorage.getMovieListPage(listType, pageNumber)
                                .concatMap(new Function<List<MovieListViewItem>, ObservableSource<MovieListViewItem>>() {
                                    @Override
                                    public ObservableSource<MovieListViewItem> apply(@NonNull List<MovieListViewItem> list) throws Exception {
                                        return Observable.fromIterable(list);
                                    }
                                });

                        if (listType.isLocalOnly()) return listFromCache;

                        Observable<MovieListViewItem> listFromNetwork = networkDataSource.readMovieListPage(listType, pageNumber)
                                .doOnNext(new Consumer<List<Movie>>() {
                                    @Override
                                    public void accept(@NonNull List<Movie> movieList) throws Exception {
                                        cacheStorage.updateOrInsertMoviesFromListAsync(movieList);
                                        cacheStorage.updateMovieListAsync(listType, pageNumber, movieList);
                                    }
                                })
                                .map(new Function<List<Movie>, List<MovieListViewItem>>() {
                                    @Override
                                    public List<MovieListViewItem> apply(@NonNull List<Movie> movieList) throws Exception {
                                        List<MovieListViewItem> newList = new ArrayList<>();
                                        for (int i = 0; i < movieList.size(); i++) {
                                            int position = (pageNumber - 1) * pageSize + i;
                                            newList.add(new MovieListViewItem(movieList.get(i), position));
                                        }
                                        return newList;
                                    }
                                })
                                .concatMap(new Function<List<MovieListViewItem>, ObservableSource<MovieListViewItem>>() {
                                    @Override
                                    public ObservableSource<MovieListViewItem> apply(@NonNull List<MovieListViewItem> list) throws Exception {
                                        return Observable.fromIterable(list);
                                    }
                                });

                        return Observable.concat(listFromCache, listFromNetwork);
                    }
                });
    }
}
