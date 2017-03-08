package com.example.leshik.moviedb.data;

import android.app.Application;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.leshik.moviedb.data.interfaces.CacheStorage;
import com.example.leshik.moviedb.data.model.Movie;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposables;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by Leshik on 01.03.2017.
 */

public class RealmCacheStorage implements CacheStorage {
    private static final String TAG = "RealmCacheStorage";
    private static final String REALM_DB_FILE_NAME = "movies.realm";
    private static final int REALM_DB_SCHEME_VERSION = 1;
    private static boolean isRealmConfigured = false;

    RealmCacheStorage(Context context) {
        if (context instanceof Application) {
            buildDefaultRealmConfiguration(context);
        } else {
            throw new IllegalArgumentException("RealmCacheStorage: context is not Application");
        }
    }

    private synchronized void buildDefaultRealmConfiguration(Context context) {
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

    private Realm getRealmInstance() {
        return Realm.getDefaultInstance();
    }

    private Observable<Realm> getRealmObservable() {
        return Observable.create(new ObservableOnSubscribe<Realm>() {
            @Override
            public void subscribe(final ObservableEmitter<Realm> emitter)
                    throws Exception {
                final Realm observableRealm = getRealmInstance();

                final RealmChangeListener<Realm> listener = new RealmChangeListener<Realm>() {
                    @Override
                    public void onChange(Realm element) {
                        emitter.onNext(element);
                    }
                };

                emitter.setDisposable(Disposables.fromRunnable(new Runnable() {
                    @Override
                    public void run() {
                        observableRealm.removeChangeListener(listener);
                        observableRealm.close();
                    }
                }));
                observableRealm.addChangeListener(listener);
                emitter.onNext(observableRealm);
            }
        });
    }

    @Override
    public Observable<Movie> getMovieObservable(final long movieId) {
        return Observable.create(new ObservableOnSubscribe<Movie>() {
            @Override
            public void subscribe(final ObservableEmitter<Movie> emitter) throws Exception {
                final Realm realm = getRealmInstance();
                final RealmResults<Movie> movie = findMovieAsRealmResults(realm, movieId);

                final RealmChangeListener<RealmResults<Movie>> listener = new RealmChangeListener<RealmResults<Movie>>() {
                    @Override
                    public void onChange(RealmResults<Movie> element) {
                        if (!emitter.isDisposed() && element.size() > 0)
                            emitter.onNext(element.get(0));
                    }
                };

                emitter.setDisposable(Disposables.fromRunnable(new Runnable() {
                    @Override
                    public void run() {
                        movie.removeChangeListener(listener);
                        if (!realm.isClosed())
                            realm.close();
                    }
                }));

                movie.addChangeListener(listener);
                if (movie.size() > 0) emitter.onNext(movie.get(0));
                else emitter.onNext(new Movie());
            }
        });
    }

    private RealmResults<Movie> findMovieAsRealmResults(Realm realm, long movieId) {
        return realm.where(Movie.class)
                .equalTo("movieId", movieId)
                .findAll();
    }

    @Override
    public long updateOrInsertMovie(final Movie newMovie) {
        Realm realm = getRealmInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm transactionRealm) {
                Log.i(TAG, "updateOrInsertMovie: +");
                updateOrInsertMovie(transactionRealm, newMovie);
                Log.i(TAG, "updateOrInsertMovie: -");
            }
        });

        realm.close();
        return newMovie.getMovieId();
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

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Log.i(TAG, "invertFavorite: +");
                Movie movie = realm.where(Movie.class).equalTo("movieId", movieId).findFirst();
                if (movie != null) {
                    if (movie.isFavorite()) {
                        movie.setFavoritePosition(-1);
                    } else {
                        Integer newFavoriteListPosition;
                        Number maxCurrentFavoritePosition = realm.where(Movie.class)
                                .max(getListTypeColumnName(MovieListType.Favorite));
                        if (maxCurrentFavoritePosition == null) newFavoriteListPosition = 1;
                        else newFavoriteListPosition = maxCurrentFavoritePosition.intValue() + 1;
                        movie.setFavoritePosition(newFavoriteListPosition);
                    }
                }
                Log.i(TAG, "invertFavorite: -");
            }
        });

        realm.close();
    }

    String getListTypeColumnName(MovieListType listType) {
        String columnName = "";
        switch (listType) {
            case Popular:
                columnName = "popularPosition";
                break;
            case Toprated:
                columnName = "topratedPosition";
                break;
            case Favorite:
                columnName = "favoritePosition";
                break;
        }

        return columnName;
    }

    @Override
    public Observable<List<Movie>> getMovieListObservable(final MovieListType listType) {
        return Observable.create(new ObservableOnSubscribe<List<Movie>>() {
            @Override
            public void subscribe(final ObservableEmitter<List<Movie>> emitter) throws Exception {
                final Realm realm = getRealmInstance();
                final RealmResults<Movie> movieList = findMovieListAsRealmResults(realm, listType);

                final RealmChangeListener<RealmResults<Movie>> listener = new RealmChangeListener<RealmResults<Movie>>() {
                    @Override
                    public void onChange(RealmResults<Movie> element) {
                        if (!emitter.isDisposed())
                            emitter.onNext(element);
                    }
                };

                emitter.setDisposable(Disposables.fromRunnable(new Runnable() {
                    @Override
                    public void run() {
                        movieList.removeChangeListener(listener);
                        if (!realm.isClosed())
                            realm.close();
                    }
                }));

                movieList.addChangeListener(listener);
                emitter.onNext(movieList);
            }
        });
    }

    private RealmResults<Movie> findMovieListAsRealmResults(Realm realm, MovieListType listType) {
        return realm.where(Movie.class)
                .greaterThan(getListTypeColumnName(listType), 0)
                .findAllSorted(getListTypeColumnName(listType));
    }

    @Override
    public void insertOrUpdateMovieList(final MovieListType listType, final Observable<List<Movie>> movieList) {
        Realm realm = getRealmInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm transactionRealm) {
                Log.i(TAG, "insertOrUpdateMovieList: +");
                updateOrInsertMovieList(transactionRealm, movieList);
                Log.i(TAG, "insertOrUpdateMovieList: -");
            }
        });

        realm.close();
    }

    @Override
    public void clearMovieListPositionsAndInsertOrUpdateData(final MovieListType listType, final Observable<List<Movie>> movieList) {
        Realm realm = getRealmInstance();

        if (realm.isInTransaction())
            clearMovieListPositionsAndInsertOrUpdateData(realm, listType, movieList);
        else realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm transactionRealm) {
                clearMovieListPositionsAndInsertOrUpdateData(transactionRealm, listType, movieList);
            }
        });

        realm.close();
    }

    private void clearMovieListPositionsAndInsertOrUpdateData(Realm transactionRealm, final MovieListType listType, final Observable<List<Movie>> movieList) {
        Log.i(TAG, "clearMovieListPositionsAndInsertOrUpdateData: +, " + listType);
        final RealmResults<Movie> result = transactionRealm.where(Movie.class)
                .greaterThan(getListTypeColumnName(listType), 0)
                .findAll();
        clearMovieListPosition(listType, result);
        updateOrInsertMovieList(transactionRealm, movieList);
        Log.i(TAG, "clearMovieListPositionsAndInsertOrUpdateData: -");
    }

    private void clearMovieListPosition(MovieListType listType, RealmResults<Movie> movieList) {
        for (Movie m : movieList) {
            // TODO: 3/6/17 Optimize this, in the Movie model class. Maybe define array of the list types
            switch (listType) {
                case Popular:
                    m.setPopularPosition(0);
                    break;
                case Toprated:
                    m.setTopratedPosition(0);
                    break;
                default:
                    throw new IllegalArgumentException("Clearing not supported for the list type");
            }
        }
    }

    private void updateOrInsertMovieList(final Realm realm, Observable<List<Movie>> movieObservable) {
        movieObservable
                .flatMap(new Function<List<Movie>, ObservableSource<Movie>>() {
                    @Override
                    public ObservableSource<Movie> apply(List<Movie> movieList) throws Exception {
                        return Observable.fromIterable(movieList);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Movie>() {
                    @Override
                    public void accept(Movie newMovie) throws Exception {
                        updateOrInsertMovie(realm, newMovie);
                    }
                });
    }

    @Override
    public int getMovieListLastPageNumber(MovieListType listType) {
        Realm realm = getRealmInstance();
        int lastPageNumber;

        lastPageNumber = realm.where(Movie.class)
                .greaterThan(getListTypeColumnName(listType), 0)
                .findAll()
                .size();

        realm.close();
        return lastPageNumber;
    }
}
