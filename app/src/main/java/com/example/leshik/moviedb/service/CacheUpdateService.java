package com.example.leshik.moviedb.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.example.leshik.moviedb.BuildConfig;
import com.example.leshik.moviedb.R;
import com.example.leshik.moviedb.utils.Utils;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class CacheUpdateService extends IntentService {
    private static final String LOG_TAG = CacheUpdateService.class.getSimpleName();
    // IntentService can perform next actions
    static final String ACTION_UPDATE_CONFIGURATION = "com.example.leshik.moviedb.service.action.UPDATE_CONFIGURATION";
    // File name to store shared preferences by cache update service
    public static final String CACHE_PREFS_NAME = "cache_prefs";

    // Default constructor
    public CacheUpdateService() {
        super("CacheUpdateService");
    }

    /**
     * Starts this service to perform action UpdateConfiguration with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @param context - calling context
     * @see IntentService
     */
    public static void startActionUpdateConfiguration(Context context) {
        Intent intent = new Intent(context, CacheUpdateService.class);
        intent.setAction(ACTION_UPDATE_CONFIGURATION);
        context.startService(intent);
    }

    // entry method
    // it receives intent, checks actions and call handlers with proper parameters
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            if (ACTION_UPDATE_CONFIGURATION.equals(action)) {
                handleActionUpdateConfiguration();
            }
        }
    }

    /**
     * Handle action UpdateConfiguration in the provided background thread with the provided
     * parameters.
     */
    private void handleActionUpdateConfiguration() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utils.baseApiSecureUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TmdbApiService service = retrofit.create(TmdbApiService.class);
        Call<TmdbConfiguration> configCall = service.getConfiguration(BuildConfig.THE_MOVIE_DB_API_KEY);
        try {
            // Execute call and update values in the helper class
            TmdbConfiguration config = configCall.execute().body();
            Utils.basePosterUrl = config.images.baseUrl;
            Utils.basePosterSecureUrl = config.images.secureBaseUrl;

            Utils.posterSizes = new String[config.images.posterSizes.size()];
            for (int i = 0; i < config.images.posterSizes.size(); i++)
                Utils.posterSizes[i] = config.images.posterSizes.get(i);

            // Store config values into shared preferences
            Utils.setCachePreference(getBaseContext(), R.string.base_poster_url, config.images.baseUrl);
            Utils.setCachePreference(getBaseContext(), R.string.base_poster_secure_url, config.images.secureBaseUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

