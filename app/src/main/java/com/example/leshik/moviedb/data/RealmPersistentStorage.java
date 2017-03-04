package com.example.leshik.moviedb.data;

import android.app.Application;
import android.content.Context;

import com.example.leshik.moviedb.data.interfaces.PersistentStorage;
import com.example.leshik.moviedb.data.model.Movie;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposables;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by Leshik on 01.03.2017.
 */

public class RealmPersistentStorage implements PersistentStorage {
    private static final String REALM_DB_FILE_NAME = "movies.realm";
    private static final int REALM_DB_SCHEME_VERSION = 1;
    private static boolean isRealmConfigured = false;

    RealmPersistentStorage(Context context) {
        if (context instanceof Application) {
            buildDefaultRealmConfiguration(context);
        } else {
            throw new IllegalArgumentException("RealmPersistentStorage: context is not Application");
        }
    }

    private void buildDefaultRealmConfiguration(Context context) {
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
                Movie movie = findMovie(transactionRealm, newMovie.getMovieId());
                if (movie != null) newMovie.updateNullFields(movie);
                transactionRealm.copyToRealmOrUpdate(newMovie);
            }
        });

        realm.close();

        return newMovie.getMovieId();
    }

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
                Movie movie = realm.where(Movie.class).equalTo("movieId", movieId).findFirst();
                if (movie != null) {
                    if (movie.isFavorite()) {
                        movie.setFavoritePosition(-1);
                    } else {
                        Integer newFavoriteListPosition;
                        Number maxCurrentFavoritePosition = realm.where(Movie.class).max("favoritePosition");
                        if (maxCurrentFavoritePosition == null) newFavoriteListPosition = 1;
                        else newFavoriteListPosition = maxCurrentFavoritePosition.intValue() + 1;
                        movie.setFavoritePosition(newFavoriteListPosition);
                    }
                }
            }
        });

        realm.close();
    }

    @Override
    public Observable<List<Movie>> getPopularListObservable() {
        return Observable.create(new ObservableOnSubscribe<List<Movie>>() {
            @Override
            public void subscribe(final ObservableEmitter<List<Movie>> emitter) throws Exception {
                final Realm realm = getRealmInstance();
                final RealmResults<Movie> movieList = findPopularAsRealmResults(realm);

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

    private RealmResults<Movie> findPopularAsRealmResults(Realm realm) {
        return realm.where(Movie.class)
                .greaterThan("popularPosition", 0)
                .findAllSorted("popularPosition");
    }

    @Override
    public Observable<List<Movie>> getTopratedListObservable() {
        throw new UnsupportedOperationException("getTopratedListObservable not implemented");
    }

    @Override
    public Observable<List<Movie>> getFavoriteListObservable() {
        throw new UnsupportedOperationException("getFavoriteListObservable not implemented");
    }
}
