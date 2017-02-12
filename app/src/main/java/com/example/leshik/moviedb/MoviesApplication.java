package com.example.leshik.moviedb;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by alex on 2/12/17.
 */

public class MoviesApplication extends Application {
    private static final String REALM_DB_FILE_NAME = "movies.realm";
    private static final int REALM_DB_SCHEME_VERSION = 1;

    @Override
    public void onCreate() {
        super.onCreate();

        // Init Realm instance
        Realm.init(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name(REALM_DB_FILE_NAME)
                .schemaVersion(REALM_DB_SCHEME_VERSION)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfig);
    }
}
