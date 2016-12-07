package com.example.leshik.moviedb.model;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Leshik on 07.12.2016.
 */

public class MoviesProvider extends ContentProvider {
    // DB Open Helper. we create it in onCreate and close in shutdown methods
    private MoviesDbHelper mOpenHelper;
    // URI matcher for provider
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    // URI Matcher constants
    static final int MOVIES = 100;
    static final int MOVIES_WITH_ID = 101;
    static final int POPULAR = 200;
    static final int TOPRATED = 300;
    static final int FAVORITES = 400;
    static final int FAVORITES_WITH_ID = 401;
    static final int VIDEOS = 500;
    static final int VIDEOS_WITH_MOVIE_ID = 501;
    static final int VIDEOS_WITH_VIDEO_ID = 502;

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIES:
                return MoviesContract.Movies.CONTENT_TYPE;
            case MOVIES_WITH_ID:
                return MoviesContract.Movies.CONTENT_ITEM_TYPE;
            case POPULAR:
                return MoviesContract.Popular.CONTENT_TYPE;
            case TOPRATED:
                return MoviesContract.TopRated.CONTENT_TYPE;
            case FAVORITES:
                return MoviesContract.Favorites.CONTENT_TYPE;
            case FAVORITES_WITH_ID:
                return MoviesContract.Favorites.CONTENT_ITEM_TYPE;
            case VIDEOS:
                return MoviesContract.Videos.CONTENT_TYPE;
            case VIDEOS_WITH_MOVIE_ID:
                return MoviesContract.Videos.CONTENT_TYPE;
            case VIDEOS_WITH_VIDEO_ID:
                return MoviesContract.Videos.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown URI:" + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        return super.bulkInsert(uri, values);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }

    static UriMatcher buildUriMatcher() {
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MoviesContract.PATH_MOVIES, MOVIES);
        matcher.addURI(authority, MoviesContract.PATH_MOVIES + "/#", MOVIES_WITH_ID);

        matcher.addURI(authority, MoviesContract.PATH_POPULAR, POPULAR);
        matcher.addURI(authority, MoviesContract.PATH_TOPRATED, TOPRATED);

        matcher.addURI(authority, MoviesContract.PATH_FAVORITES, FAVORITES);
        matcher.addURI(authority, MoviesContract.PATH_FAVORITES + "/#", FAVORITES_WITH_ID);

        matcher.addURI(authority, MoviesContract.PATH_VIDEOS, VIDEOS);
        matcher.addURI(authority, MoviesContract.PATH_VIDEOS + "/#", VIDEOS_WITH_MOVIE_ID);
        matcher.addURI(authority, MoviesContract.PATH_VIDEOS + "/*", VIDEOS_WITH_VIDEO_ID);

        return matcher;
    }

}
