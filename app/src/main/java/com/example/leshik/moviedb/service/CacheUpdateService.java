package com.example.leshik.moviedb.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.example.leshik.moviedb.BuildConfig;
import com.example.leshik.moviedb.MovieUtils;
import com.example.leshik.moviedb.model.MoviesContract;

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
    // IntentService can perform
    static final String ACTION_UPDATE_POPULAR = "com.example.leshik.moviedb.service.action.UPDATE_POPULAR";
    static final String ACTION_UPDATE_TOPRATED = "com.example.leshik.moviedb.service.action.UPDATE_TOPRATED";
    static final String ACTION_UPDATE_CONFIGURATION = "com.example.leshik.moviedb.service.action.UPDATE_CONFIGURATION";

    static final String EXTRA_PARAM_PAGE = "com.example.leshik.moviedb.service.extra.PAGE";

    public CacheUpdateService() {
        super("CacheUpdateService");
    }

    /**
     * Starts this service to perform action UpdatePopular with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionUpdatePopular(Context context, int page) {
        Intent intent = new Intent(context, CacheUpdateService.class);
        intent.setAction(ACTION_UPDATE_POPULAR);
        intent.putExtra(EXTRA_PARAM_PAGE, page);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action UpdateToprated with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionUpdateToprated(Context context, int page) {
        Intent intent = new Intent(context, CacheUpdateService.class);
        intent.setAction(ACTION_UPDATE_TOPRATED);
        intent.putExtra(EXTRA_PARAM_PAGE, page);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action UpdateConfiguration with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionUpdateConfiguration(Context context) {
        Intent intent = new Intent(context, CacheUpdateService.class);
        intent.setAction(ACTION_UPDATE_CONFIGURATION);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_POPULAR.equals(action)) {
                final int page = intent.getIntExtra(EXTRA_PARAM_PAGE, -1);
                handleActionUpdatePopular(page);
            } else if (ACTION_UPDATE_TOPRATED.equals(action)) {
                final int page = intent.getIntExtra(EXTRA_PARAM_PAGE, -1);
                handleActionUpdateToprated(page);
            } else if (ACTION_UPDATE_CONFIGURATION.equals(action)) {
                handleActionUpdateConfiguration();
            }
        }
    }

    /**
     * Handle action UpdatePopular in the provided background thread with the provided
     * parameters.
     */
    private void handleActionUpdatePopular(int page) {
        int requestPage = page <= 0 ? 1 : page;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MovieUtils.baseApiSecureUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TmdbApiService service = retrofit.create(TmdbApiService.class);
        Call<TmdbListPage> popularCall = service.getPopular(BuildConfig.THE_MOVIE_DB_API_KEY, requestPage);
        TmdbListPage listPage = null;
        try {
            listPage = popularCall.execute().body();
        } catch (IOException e) {
            // TODO: show network error activity
            e.printStackTrace();
            return;
        }

        // TODO: store values into database via content provider
        if (page <= 0) getContentResolver().delete(MoviesContract.BASE_CONTENT_URI, null, null);
    }

    /**
     * Handle action UpdateToprated in the provided background thread with the provided
     * parameters.
     */
    private void handleActionUpdateToprated(int page) {
        // TODO: Handle action UpdateToprated
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action UpdateConfiguration in the provided background thread with the provided
     * parameters.
     */
    private void handleActionUpdateConfiguration() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MovieUtils.baseApiSecureUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TmdbApiService service = retrofit.create(TmdbApiService.class);
        Call<TmdbConfiguration> configCall = service.getConfiguration(BuildConfig.THE_MOVIE_DB_API_KEY);
        try {
            TmdbConfiguration config = configCall.execute().body();
            MovieUtils.basePosterUrl = config.images.baseUrl;
            MovieUtils.basePosterSecureUrl = config.images.secureBaseUrl;
            MovieUtils.posterSizes = new String[config.images.posterSizes.size()];
            for (int i = 0; i < config.images.posterSizes.size(); i++)
                MovieUtils.posterSizes[i] = config.images.posterSizes.get(i);
        } catch (IOException e) {
            // TODO: show network error activity
            e.printStackTrace();
            return;
        }

        // TODO: store config values into shared preferences
    }

}
