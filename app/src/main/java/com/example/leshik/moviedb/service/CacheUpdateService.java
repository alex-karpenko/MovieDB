package com.example.leshik.moviedb.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.leshik.moviedb.BuildConfig;
import com.example.leshik.moviedb.R;
import com.example.leshik.moviedb.Utils;
import com.example.leshik.moviedb.data.MoviesContract;

import java.io.IOException;
import java.util.Calendar;

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
    static final String ACTION_UPDATE_MOVIE = "com.example.leshik.moviedb.service.action.UPDATE_MOVIE";
    static final String ACTION_UPDATE_POPULAR = "com.example.leshik.moviedb.service.action.UPDATE_POPULAR";
    static final String ACTION_UPDATE_TOPRATED = "com.example.leshik.moviedb.service.action.UPDATE_TOPRATED";
    static final String ACTION_UPDATE_CONFIGURATION = "com.example.leshik.moviedb.service.action.UPDATE_CONFIGURATION";
    static final String ACTION_UPDATE_FAVORITE = "com.example.leshik.moviedb.service.action.UPDATE_FAVORITE";
    static final String ACTION_UPDATE_VIDEOS = "com.example.leshik.moviedb.service.action.UPDATE_VIDEOS";
    static final String ACTION_UPDATE_REVIEWS = "com.example.leshik.moviedb.service.action.UPDATE_REVIEWS";
    // Parameters for actions
    // page number for update popular or toprated
    static final String EXTRA_PARAM_PAGE = "com.example.leshik.moviedb.service.extra.PAGE";
    // movie_id for update movies
    static final String EXTRA_PARAM_MOVIE_ID = "com.example.leshik.moviedb.service.extra.MOVIE_ID";
    // favorite flag for update (insert/delete) favorite's state
    static final String EXTRA_PARAM_FAVORITE_FLAG = "com.example.leshik.moviedb.service.extra.FAVORITE_FLAG";
    // File name to store shared preferences by cache update service
    public static final String CACHE_PREFS_NAME = "cache_prefs";

    // Default constructor
    public CacheUpdateService() {
        super("CacheUpdateService");
    }

    /**
     * Starts this service to perform action UpdateMovie with the given parameters. If
     * the service is already performing a task this action will be queued.
     * @param context - calling context
     * @param movie_id - movie_id of the film to update
     *
     * @see IntentService
     */
    public static void startActionUpdateMovie(Context context, int movie_id) {
        Intent intent = new Intent(context, CacheUpdateService.class);
        intent.setAction(ACTION_UPDATE_MOVIE);
        intent.putExtra(EXTRA_PARAM_MOVIE_ID, movie_id);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action UpdateVideos with the given parameters. If
     * the service is already performing a task this action will be queued.
     * @param context - calling context
     * @param movie_id - movie_id of the film to update list of videos
     *
     * @see IntentService
     */
    public static void startActionUpdateVideos(Context context, int movie_id) {
        Intent intent = new Intent(context, CacheUpdateService.class);
        intent.setAction(ACTION_UPDATE_VIDEOS);
        intent.putExtra(EXTRA_PARAM_MOVIE_ID, movie_id);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action UpdateReviews with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @param context - calling context
     * @param movie_id - movie_id of the film to update list of videos
     * @param page - number of the review list page to update
     * @see IntentService
     */
    public static void startActionUpdateReviews(Context context, int movie_id, int page) {
        Intent intent = new Intent(context, CacheUpdateService.class);
        intent.setAction(ACTION_UPDATE_REVIEWS);
        intent.putExtra(EXTRA_PARAM_MOVIE_ID, movie_id);
        intent.putExtra(EXTRA_PARAM_PAGE, page);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action UpdateMovie with the given parameters. If
     * the service is already performing a task this action will be queued.
     * @param context - calling context
     * @param movie_id - movie_id of the film to update
     * @param isFavorite - if true - insert movie into favorites table
     *                     if false - remove from table
     *
     * @see IntentService
     */
    public static void startActionUpdateFavorite(Context context, long movie_id, boolean isFavorite) {
        Intent intent = new Intent(context, CacheUpdateService.class);
        intent.setAction(ACTION_UPDATE_FAVORITE);
        intent.putExtra(EXTRA_PARAM_MOVIE_ID, movie_id);
        intent.putExtra(EXTRA_PARAM_FAVORITE_FLAG, isFavorite);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action UpdatePopular with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @param context - calling context
     * @param page - page number to update
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
     * @param context - calling context
     * @param page - page number to update
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
     * @param context - calling context
     *
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

            // Simple if-else-if selector for choose action
            if (ACTION_UPDATE_MOVIE.equals(action)) {
                final int movie_id = intent.getIntExtra(EXTRA_PARAM_MOVIE_ID, -1);
                handleActionUpdateMovie(movie_id);
            } else if (ACTION_UPDATE_POPULAR.equals(action)) {
                final int page = intent.getIntExtra(EXTRA_PARAM_PAGE, -1);
                handleActionUpdatePopular(page);
            } else if (ACTION_UPDATE_TOPRATED.equals(action)) {
                final int page = intent.getIntExtra(EXTRA_PARAM_PAGE, -1);
                handleActionUpdateToprated(page);
            } else if (ACTION_UPDATE_CONFIGURATION.equals(action)) {
                handleActionUpdateConfiguration();
            } else if (ACTION_UPDATE_FAVORITE.equals(action)) {
                final long movie_id = intent.getLongExtra(EXTRA_PARAM_MOVIE_ID, -1);
                final boolean isFavorite = intent.getBooleanExtra(EXTRA_PARAM_FAVORITE_FLAG, false);
                handleActionUpdateFavorite(movie_id, isFavorite);
            } else if (ACTION_UPDATE_VIDEOS.equals(action)) {
                final int movie_id = intent.getIntExtra(EXTRA_PARAM_MOVIE_ID, -1);
                handleActionUpdateVideos(movie_id);
            } else if (ACTION_UPDATE_REVIEWS.equals(action)) {
                final int movie_id = intent.getIntExtra(EXTRA_PARAM_MOVIE_ID, -1);
                final int page = intent.getIntExtra(EXTRA_PARAM_PAGE, -1);
                handleActionUpdateReviews(movie_id, page);
            }
        }
    }

    /**
     * Handle action UpdateMovie in the provided background thread with the provided
     * parameters.
     */
    private void handleActionUpdateMovie(int movie_id) {
        if (movie_id <= 0) return;

        // Create retrofit object...
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utils.baseApiSecureUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // ... create service and call ....
        TmdbApiService service = retrofit.create(TmdbApiService.class);
        Call<TmdbMovie> movieCall = service.getMovie(movie_id, BuildConfig.THE_MOVIE_DB_API_KEY);
        TmdbMovie movie;

        try {
            // Execute and insert(a-la update) movies into table via content provider call
            movie = movieCall.execute().body();
            ContentValues values = movie.getMovieContentValues();
            // Set last update time value
            long currentTime = Calendar.getInstance().getTimeInMillis();
            values.put(MoviesContract.Movies.COLUMN_NAME_LAST_UPDATED, currentTime);
            // Update movie
            getContentResolver().insert(MoviesContract.Movies.CONTENT_URI, values);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handle action UpdateVideos in the provided background thread with the provided
     * parameters.
     */
    private void handleActionUpdateVideos(int movie_id) {
        if (movie_id <= 0) return;

        // Create retrofit object...
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utils.baseApiSecureUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // ... create service and call ....
        TmdbApiService service = retrofit.create(TmdbApiService.class);
        Call<TmdbVideos> videosCall = service.getVideos(movie_id, BuildConfig.THE_MOVIE_DB_API_KEY);
        TmdbVideos listVideos;

        try {
            listVideos = videosCall.execute().body();
            // Insert videos tables via content provider calls
            getContentResolver().bulkInsert(MoviesContract.Videos.CONTENT_URI, listVideos.getVideosContentValues());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handle action UpdateReviews in the provided background thread with the provided
     * parameters.
     */
    private void handleActionUpdateReviews(int movie_id, int page) {
        if (movie_id <= 0) return;
        int requestPage = page <= 0 ? 1 : page;

        // Create retrofit object...
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utils.baseApiSecureUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // ... create service and call ....
        TmdbApiService service = retrofit.create(TmdbApiService.class);
        Call<TmdbReviews> reviewsCall = service.getReviews(movie_id, requestPage, BuildConfig.THE_MOVIE_DB_API_KEY);
        TmdbReviews reviewsPage;

        try {
            reviewsPage = reviewsCall.execute().body();

            if (page <= 0) {
                // If page number is not positive - first delete all data from reviews table for specified movie
                getContentResolver().
                        delete(MoviesContract.Reviews.buildUri(movie_id),
                                null,
                                null);
            }
            // Insert reviews table via content provider calls
            getContentResolver().bulkInsert(MoviesContract.Reviews.CONTENT_URI, reviewsPage.getReviewsContentValues());

            // queue fetching the next page if present (recursion)
            if (reviewsPage.page < reviewsPage.totalPages)
                startActionUpdateReviews(getApplicationContext(), movie_id, requestPage + 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handle action UpdateFavorite in the provided background thread with the provided
     * parameters.
     */
    private void handleActionUpdateFavorite(long movie_id, boolean isFavorite) {
        if (movie_id <= 0) return;

        if (isFavorite) {
            // to set favorite we have to insert row in table
            // without sort_id, because content provider's insert method state it for itself
            ContentValues values = new ContentValues();
            values.put(MoviesContract.Favorites.COLUMN_NAME_MOVIE_ID, movie_id);
            values.put(MoviesContract.Favorites.COLUMN_NAME_SORT_ID, -1);
            getContentResolver().insert(MoviesContract.Favorites.CONTENT_URI, values);
        } else {
            // or simply remove row from favorite's table
            getContentResolver().delete(MoviesContract.Favorites.buildUri(movie_id), null, null);
        }
    }

    /**
     * Handle action UpdatePopular in the provided background thread with the provided
     * parameters.
     */
    private void handleActionUpdatePopular(int page) {
        int requestPage = page <= 0 ? 1 : page;

        // Create retrofit object...
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utils.baseApiSecureUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // ... create service and call ....
        TmdbApiService service = retrofit.create(TmdbApiService.class);
        Call<TmdbListPage> popularCall = service.getPopular(BuildConfig.THE_MOVIE_DB_API_KEY, requestPage);
        TmdbListPage listPage;

        try {
            listPage = popularCall.execute().body();

            if (page <= 0) {
                // If page number is not positive - first delete all data (but not first page) from popular table
                // and update last_update_time in the preferences
                getContentResolver().
                        delete(MoviesContract.Popular.CONTENT_URI,
                                MoviesContract.Popular.COLUMN_NAME_SORT_ID + ">?",
                                new String[]{String.valueOf(Utils.getCachePageSize())});
                updateCachePreference(R.string.last_popular_update_time, Calendar.getInstance().getTimeInMillis());
            }

            // Insert movies and popular tables via content provider calls
            if (listPage.listResults.size() > 0) {
                getContentResolver().bulkInsert(MoviesContract.Movies.CONTENT_URI, listPage.getMoviesContentValues());
                getContentResolver().bulkInsert(MoviesContract.Popular.CONTENT_URI, listPage.getPopularContentValues());

                // Update preferences to set number of pages and items
                updateCachePreference(R.string.total_popular_pages, listPage.totalPages);
                updateCachePreference(R.string.total_popular_items, listPage.totalResults);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handle action UpdateToprated in the provided background thread with the provided
     * parameters.
     */
    private void handleActionUpdateToprated(int page) {
        int requestPage = page <= 0 ? 1 : page;

        // Create retrofit object...
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utils.baseApiSecureUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // ... create service and call ....
        TmdbApiService service = retrofit.create(TmdbApiService.class);
        Call<TmdbListPage> topratedCall = service.getToprated(BuildConfig.THE_MOVIE_DB_API_KEY, requestPage);
        TmdbListPage listPage;
        try {
            listPage = topratedCall.execute().body();
            if (page <= 0) {
                // If page number is not positive - first delete all data (but not first page) from popular table
                // and update last_update_time in the preferences
                getContentResolver()
                        .delete(MoviesContract.Toprated.CONTENT_URI,
                                MoviesContract.Toprated.COLUMN_NAME_SORT_ID + ">?",
                                new String[]{String.valueOf(Utils.getCachePageSize())});
                updateCachePreference(R.string.last_toprated_update_time, Calendar.getInstance().getTimeInMillis());
            }

            if (listPage.listResults.size() > 0) {
                // Insert movies and toprated tables via content provider calls
                getContentResolver().bulkInsert(MoviesContract.Movies.CONTENT_URI, listPage.getMoviesContentValues());
                getContentResolver().bulkInsert(MoviesContract.Toprated.CONTENT_URI, listPage.getTopratedContentValues());

                // Update preferences to set number of pages and items
                updateCachePreference(R.string.total_toprated_pages, listPage.totalPages);
                updateCachePreference(R.string.total_toprated_items, listPage.totalResults);
            }
        } catch (IOException e) {
            e.printStackTrace();
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
            updateCachePreference(R.string.base_potser_url, config.images.baseUrl);
            updateCachePreference(R.string.base_potser_secure_url, config.images.secureBaseUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method to update shared preferences by string value
    public void updateCachePreference(int key, String value) {
        SharedPreferences prefs = getSharedPreferences(CACHE_PREFS_NAME, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(getString(key), value);
        editor.commit();
    }

    // Helper method to update shared preferences by long value
    public void updateCachePreference(int key, long value) {
        SharedPreferences prefs = getSharedPreferences(CACHE_PREFS_NAME, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(getString(key), value);
        editor.commit();
    }
}

