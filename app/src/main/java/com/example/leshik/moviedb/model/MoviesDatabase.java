package com.example.leshik.moviedb.model;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by alex on 2/12/17.
 */

public class MoviesDatabase {
    private static final String REALM_DB_FILE_NAME = "movies.realm";
    private static final int REALM_DB_SCHEME_VERSION = 1;

    private static boolean isInitialized = false;

    private MoviesDatabase() {
    }

    public static Realm getRealm(Context context) {
        if (!isInitialized) {
            // Init Realm instance
            Realm.init(context);
            RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                    .name(REALM_DB_FILE_NAME)
                    .schemaVersion(REALM_DB_SCHEME_VERSION)
                    .deleteRealmIfMigrationNeeded()
                    .build();
            Realm.setDefaultConfiguration(realmConfig);
            isInitialized = true;
        }

        return Realm.getDefaultInstance();
    }
}
