package com.example.leshik.moviedb.data;

import android.content.Context;

import com.example.leshik.moviedb.data.interfaces.ApiService;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by alex on 2/12/17.
 *
 */

public class DataUtils {
    private static final String REALM_DB_FILE_NAME = "movies.realm";
    private static final int REALM_DB_SCHEME_VERSION = 1;

    private static boolean isInitialized = false;
    private static Retrofit retrofit = null;

    private DataUtils() {
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

    public static Retrofit getRetrofitInstance(String apiUrl) {
        if(retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(apiUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static ApiService getServiceInstance(Retrofit retrofit) {
        return retrofit.create(ApiService.class);
    }
}
