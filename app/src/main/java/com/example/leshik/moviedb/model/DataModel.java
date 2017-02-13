package com.example.leshik.moviedb.model;

import android.content.Context;

import com.example.leshik.moviedb.Utils;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by alex on 2/12/17.
 */

public class DataModel {
    private static final String REALM_DB_FILE_NAME = "movies.realm";
    private static final int REALM_DB_SCHEME_VERSION = 1;

    private static boolean isInitialized = false;

    private DataModel() {
    }

    public static Realm getRealmInstance(Context context) {
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

    public static ApiService getServiceInstance() {
        return new Retrofit.Builder()
                .baseUrl(Utils.baseApiSecureUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(ApiService.class);
    }
}
