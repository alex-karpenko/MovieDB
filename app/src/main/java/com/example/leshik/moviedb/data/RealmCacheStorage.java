package com.example.leshik.moviedb.data;

import android.app.Application;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.leshik.moviedb.data.interfaces.CacheStorage;
import com.example.leshik.moviedb.data.interfaces.PreferenceInterface;
import com.example.leshik.moviedb.data.model.Movie;
import com.example.leshik.moviedb.data.model.MovieListItem;
import com.example.leshik.moviedb.data.model.MovieListViewItem;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by Leshik on 01.03.2017.
 * <p>
 * Implementation of CacheStorage interface
 * with Realm DB
 */

class RealmCacheStorage implements CacheStorage {
    private static final String TAG = "RealmCacheStorage";
    private static final String REALM_DB_FILE_NAME = "movies.realm";
    private static final int REALM_DB_SCHEME_VERSION = 1;
    private static volatile Boolean isRealmConfigured = false;
    private PreferenceInterface prefStorage;

    RealmCacheStorage(Context context) {
        if (context instanceof Application) {
            buildDefaultRealmConfiguration(context);
        } else {
            throw new IllegalArgumentException("RealmCacheStorage: context is not Application");
        }
        prefStorage = PreferenceStorage.getInstance(context);
    }

    private void buildDefaultRealmConfiguration(Context context) {
        if (!isRealmConfigured) {
            synchronized (isRealmConfigured) {
                if (!isRealmConfigured) {
                    Realm.init(context);
                    RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                            .name(REALM_DB_FILE_NAME)
                            .schemaVersion(REALM_DB_SCHEME_VERSION)
                            .deleteRealmIfMigrationNeeded()
                            .build();
                    Realm.setDefaultConfiguration(realmConfig);
                    isRealmConfigured = true;
                }
            }
        }
    }

    private Realm getRealmInstance() {
        return Realm.getDefaultInstance();
    }

    @Override
    public Observable<Movie> getMovie(final long movieId) {
        return Observable.create(new ObservableOnSubscribe<Movie>() {
            @Override
            public void subscribe(final ObservableEmitter<Movie> emitter) throws Exception {
                Realm realm = getRealmInstance();

                try {
                    Movie movie = findMovie(realm, movieId);

                    if (movie != null) emitter.onNext(movie);
                    emitter.onComplete();

                } catch (Exception e) {
                    emitter.onError(e);
                } finally {
                    if (!realm.isClosed()) realm.close();
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public long updateOrInsertMovieAsync(final Movie newMovie) {
        Realm realm = getRealmInstance();

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm transactionRealm) {
                Log.i(TAG, "updateOrInsertMovieAsync: +");
                updateOrInsertMovie(transactionRealm, newMovie);
                Log.i(TAG, "updateOrInsertMovieAsync: -");
            }
        });

        realm.close();
        return newMovie.getMovieId();
    }

    @Override
    public void updateOrInsertMoviesFromListAsync(final List<Movie> movieList) {
        Realm realm = getRealmInstance();

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm transactionRealm) {
                Log.i(TAG, "updateOrInsertMoviesFromListAsync: +");
                for (Movie movie : movieList) updateOrInsertMovie(transactionRealm, movie);
                Log.i(TAG, "updateOrInsertMoviesFromListAsync: -");
            }
        });

        realm.close();
    }

    @Override
    public void updateMovieListAsync(final MovieListType listType, final int page, final List<Movie> movieList) {
        Realm realm = getRealmInstance();

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm transactionRealm) {
                Log.i(TAG, "updateMovieListAsync: +");
                RealmResults<MovieListItem> listItems = transactionRealm.where(MovieListItem.class)
                        .equalTo(MovieListItem.LIST_TYPE_COLUMN, listType.getIndex())
                        .equalTo(MovieListItem.PAGE_COLUMN, page)
                        .findAll();
                listItems.deleteAllFromRealm();
                for (int i = 0; i < movieList.size(); i++) {
                    MovieListItem newItem = new MovieListItem(listType.getIndex(),
                            page, i, movieList.get(i).getMovieId());
                    transactionRealm.copyToRealm(newItem);
                }
                Log.i(TAG, "updateMoviesListAsync: -");
            }
        });

        realm.close();
    }

    private void updateOrInsertMovie(final Realm realm, final Movie newMovie) {
        Movie oldMovie = findMovie(realm, newMovie.getMovieId());
        if (oldMovie != null)
            newMovie.updateNullFields(oldMovie);

        if (realm.isInTransaction()) {
            realm.copyToRealmOrUpdate(newMovie);
        } else {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm transactionRealm) {
                    Log.i(TAG, "updateOrInsertMovie: +");
                    transactionRealm.copyToRealmOrUpdate(newMovie);
                    Log.i(TAG, "updateOrInsertMovie: -");
                }
            });
        }

    }

    @Nullable
    private Movie findMovie(Realm realm, long movieId) {
        Movie movie = realm.where(Movie.class).equalTo("movieId", movieId).findFirst();
        if (movie != null && movie.isValid())
            return realm.copyFromRealm(movie);
        else return null;
    }

    @Override
    public void invertFavorite(final long movieId) {
        Realm realm = getRealmInstance();

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm transactRealm) {
                Log.i(TAG, "invertFavorite: +");
                Movie movie = transactRealm.where(Movie.class).equalTo("movieId", movieId).findFirst();
                if (movie != null) movie.invertFavorite();
                Log.i(TAG, "invertFavorite: -");
            }
        });

        realm.close();
    }

    @Override
    public Observable<List<MovieListViewItem>> getMovieListPage(final MovieListType listType, final int page) {
        return Observable.create(new ObservableOnSubscribe<List<MovieListViewItem>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<MovieListViewItem>> emitter) throws Exception {
                Realm realm = getRealmInstance();
                List<MovieListItem> movieList;
                List<MovieListViewItem> returnList = new ArrayList<>();

                try {
                    if (listType.isLocalOnly()) {
                        RealmResults<Movie> movieResult;
                        switch (listType) {
                            case Favorite:
                                movieResult = realm.where(Movie.class)
                                        .greaterThan(listType.getModelColumnName(), 0L)
                                        .findAllSorted(listType.getModelColumnName());
                                break;
                            default:
                                throw new IllegalArgumentException("Local list with unknown type");
                        }

                        for (int i = 0; i < movieResult.size(); i++) {
                            returnList.add(i, new MovieListViewItem(movieResult.get(i), i));
                        }
                    } else {
                        RealmResults<MovieListItem> movieListResult = realm.where(MovieListItem.class)
                                .equalTo(MovieListItem.LIST_TYPE_COLUMN, listType.getIndex())
                                .equalTo(MovieListItem.PAGE_COLUMN, page)
                                .findAllSorted(MovieListItem.POSITION_COLUMN);
                        if (movieListResult.size() > 0 && movieListResult.isValid()) {
                            movieList = realm.copyFromRealm(movieListResult);
                            int pageSize = prefStorage.getCachePageSize();

                            for (MovieListItem item : movieList) {
                                Movie movie = findMovie(realm, item.movieId);
                                if (movie != null) {
                                    MovieListViewItem viewListItem = new MovieListViewItem(movie, item.getAbsolutePosition(pageSize));
                                    returnList.add(item.position, viewListItem);
                                }
                            }
                        }
                    }

                    if (returnList.size() > 0) {
                        if (!emitter.isDisposed()) emitter.onNext(returnList);
                    }
                    if (!emitter.isDisposed()) emitter.onComplete();
                } catch (Exception exception) {
                    if (!emitter.isDisposed()) emitter.onError(exception);
                } finally {
                    if (!realm.isClosed()) realm.close();
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public int getMovieListLastPageNumber(MovieListType listType) {
        Realm realm = getRealmInstance();
        int lastPage;

        if (listType.isLocalOnly()) {
            int lastPosition = realm.where(Movie.class)
                    .greaterThan(listType.getModelColumnName(), 0L)
                    .findAll()
                    .size();
            lastPage = lastPosition / prefStorage.getCachePageSize();
            if (lastPosition % prefStorage.getCachePageSize() != 0) lastPage++;
        } else {
            lastPage = realm.where(MovieListItem.class)
                    .equalTo(MovieListItem.LIST_TYPE_COLUMN, listType.getIndex())
                    .max(MovieListItem.PAGE_COLUMN)
                    .intValue();
        }

        realm.close();
        return lastPage;
    }
}
