package com.example.leshik.moviedb.data;

import android.app.Application;
import android.content.Context;

import com.example.leshik.moviedb.data.interfaces.PersistentStorage;
import com.example.leshik.moviedb.data.model.Movie;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Leshik on 01.03.2017.
 */

public class RealmPersistentStorage implements PersistentStorage {
    private static final String TAG = "RealmPersistentStorage";
    private static final String REALM_DB_FILE_NAME = "movies.realm";
    private static final int REALM_DB_SCHEME_VERSION = 1;

    private Context context;

    RealmPersistentStorage(Context context) {
        if (!(context instanceof Application)) {
            throw new IllegalArgumentException("RealmPersistentStorage: context is not Application");
        }

        this.context = context;

        Realm.init(context);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name(REALM_DB_FILE_NAME)
                .schemaVersion(REALM_DB_SCHEME_VERSION)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfig);
    }

    private Realm getRealmInstance() {
        return Realm.getDefaultInstance();
    }

    @Override
    public Movie readMovie(long movieId) {
        Realm realm = getRealmInstance();
        Movie movie = findMovieFromDb(realm, movieId);
        realm.close();

        return movie;
    }

    private Movie findMovieFromDb(Realm realm, long movieId) {
        Movie movie = realm.where(Movie.class).equalTo("movieId", movieId).findFirst();
        if (movie != null && movie.isValid())
            return realm.copyFromRealm(movie);
        else return null;
    }


    @Override
    public long writeMovie(final Movie newMovie) {
        Realm realm = getRealmInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm transactionRealm) {
                Movie movie = findMovieFromDb(transactionRealm, newMovie.getMovieId());
                if (movie != null) newMovie.updateNullFields(movie);
                transactionRealm.copyToRealmOrUpdate(newMovie);
            }
        });

        realm.close();

        return newMovie.getMovieId();
    }

    @Override
    public void setFavoriteFlag(final long movieId, final boolean favoriteFlag) {
        Realm realm = getRealmInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Movie movie = realm.where(Movie.class).equalTo("movieId", movieId).findFirst();
                if (movie != null) {
                    if (favoriteFlag) {
                        Integer newFavoriteListPosition;
                        Number maxCurrentFavoritePosition = realm.where(Movie.class).max("favoritePosition");
                        if (maxCurrentFavoritePosition == null) newFavoriteListPosition = 1;
                        else newFavoriteListPosition = maxCurrentFavoritePosition.intValue() + 1;
                        movie.setFavoritePosition(newFavoriteListPosition);
                    } else {
                        movie.setFavoritePosition(-1);
                    }
                }
            }
        });

        realm.close();
    }

    @Override
    public List<Movie> readPopularList() {
        throw new UnsupportedOperationException("readPopularList not implemented");
    }

    @Override
    public List<Movie> readTopratedList() {
        throw new UnsupportedOperationException("readTopratedList not implemented");
    }

    @Override
    public List<Movie> readFavoriteList() {
        throw new UnsupportedOperationException("readFavoriteList not implemented");
    }
}
