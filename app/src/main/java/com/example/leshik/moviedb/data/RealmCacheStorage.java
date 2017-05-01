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
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposables;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmChangeListener;
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
                .greaterThanOrEqualTo(listType.getModelColumnName(), 0)
                .findAllSorted(listType.getModelColumnName());
    }

    @Override
    public Observable<List<MovieListViewItem>> getMovieListPage(final MovieListType listType, final int page) {
        return Observable.create(new ObservableOnSubscribe<List<MovieListViewItem>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<MovieListViewItem>> emitter) throws Exception {
                Realm realm = getRealmInstance();

                try {
                    RealmResults<MovieListItem> movieListResult = realm.where(MovieListItem.class)
                            .equalTo(MovieListItem.LIST_TYPE_COLUMN, listType.getIndex())
                            .equalTo(MovieListItem.PAGE_COLUMN, page)
                            .findAllSorted(MovieListItem.POSITION_COLUMN);

                    if (movieListResult.size() > 0 && movieListResult.isValid()) {
                        List<MovieListItem> movieList = realm.copyFromRealm(movieListResult);
                        List<MovieListViewItem> returnList = new ArrayList<>();
                        int pageSize = prefStorage.getCachePageSize();

                        for (MovieListItem item : movieList) {
                            Movie movie = findMovie(realm, item.movieId);
                            if (movie != null) {
                                MovieListViewItem viewListItem = new MovieListViewItem(movie, item.getAbsolutePosition(pageSize));
                                returnList.add(item.position, viewListItem);
                            }
                        }

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
                .greaterThanOrEqualTo(listType.getModelColumnName(), 0)
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
                case Upcoming:
                    m.setUpcomingPosition(0);
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
        int lastPosition;

        lastPosition = realm.where(Movie.class)
                .greaterThanOrEqualTo(listType.getModelColumnName(), 0)
                .findAll()
                .size();

        realm.close();
        return lastPosition / prefStorage.getCachePageSize();
    }
}
