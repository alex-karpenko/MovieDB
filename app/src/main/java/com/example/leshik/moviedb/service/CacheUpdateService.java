package com.example.leshik.moviedb.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class CacheUpdateService extends IntentService {
    // IntentService can perform
    private static final String ACTION_UPDATE_POPULAR = "com.example.leshik.moviedb.service.action.UPDATE_POPULAR";
    private static final String ACTION_UPDATE_TOPRATED = "com.example.leshik.moviedb.service.action.UPDATE_TOPRATED";
    private static final String ACTION_UPDATE_CONFIGURATION = "com.example.leshik.moviedb.service.action.UPDATE_CONFIGURATION";

    private static final String EXTRA_PARAM_PAGE = "com.example.leshik.moviedb.service.extra.PAGE";

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
        // TODO: Handle action UpdatePopular
        throw new UnsupportedOperationException("Not yet implemented");
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
        // TODO: Handle action UpdateConfiguration
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
