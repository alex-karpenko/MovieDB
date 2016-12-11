package com.example.leshik.moviedb.model;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
    static final int MOVIES_WITH_MOVIE_ID = 101;
    static final int POPULAR = 200;
    static final int POPULAR_WITH_SORT_ID = 201;
    static final int TOPRATED = 300;
    static final int TOPRATED_WITH_SORT_ID = 301;
    static final int FAVORITES = 400;
    static final int FAVORITES_WITH_SORT_ID = 401;
    static final int VIDEOS = 500;
    static final int VIDEOS_WITH_MOVIE_ID = 501;
    static final int VIDEOS_WITH_VIDEO_ID = 502;

    @Override
    public boolean onCreate() {
        mOpenHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIES:
                return MoviesContract.Movies.CONTENT_TYPE;
            case MOVIES_WITH_MOVIE_ID:
                return MoviesContract.Movies.CONTENT_ITEM_TYPE;
            case POPULAR:
                return MoviesContract.Popular.CONTENT_TYPE;
            case POPULAR_WITH_SORT_ID:
                return MoviesContract.Popular.CONTENT_ITEM_TYPE;
            case TOPRATED:
                return MoviesContract.TopRated.CONTENT_TYPE;
            case TOPRATED_WITH_SORT_ID:
                return MoviesContract.TopRated.CONTENT_ITEM_TYPE;
            case FAVORITES:
                return MoviesContract.Favorites.CONTENT_TYPE;
            case FAVORITES_WITH_SORT_ID:
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
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor returnCursor;
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIES:
                returnCursor = db.query(MoviesContract.Movies.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null, null,
                        sortOrder);
                break;
            case MOVIES_WITH_MOVIE_ID:
                returnCursor = db.query(MoviesContract.Movies.TABLE_NAME,
                        projection,
                        MoviesContract.Movies.COLUMN_NAME_MOVIE_ID + "=?",
                        new String[]{uri.getLastPathSegment()},
                        null, null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnCursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        long _id;

        switch (match) {
            case MOVIES:
                int movieId = moviesRecordInsertOrUpdate(db, values);
                if (movieId > 0)
                    returnUri = MoviesContract.Movies.buildUri(movieId);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case POPULAR:
                _id = db.insert(MoviesContract.Popular.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MoviesContract.Popular.buildUri(values.getAsLong(MoviesContract.Popular.COLUMN_NAME_SORT_ID));
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case TOPRATED:
                _id = db.insert(MoviesContract.TopRated.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MoviesContract.TopRated.buildUri(values.getAsLong(MoviesContract.TopRated.COLUMN_NAME_SORT_ID));
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case FAVORITES:
                _id = db.insert(MoviesContract.Favorites.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MoviesContract.Favorites.buildUri(values.getAsLong(MoviesContract.Favorites.COLUMN_NAME_SORT_ID));
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case VIDEOS:
                _id = db.insert(MoviesContract.Videos.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MoviesContract.Videos.buildUri(values.getAsLong(MoviesContract.Videos.COLUMN_NAME_VIDEO_ID));
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int result = 0;
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIES:
                db.beginTransaction();
                try {
                    for (ContentValues v : values) {
                        int r = moviesRecordInsertOrUpdate(db, v);
                        if (r != -1) result++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return result;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int result;
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIES:
                result = db.delete(MoviesContract.Movies.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIES_WITH_MOVIE_ID:
                result = db.delete(MoviesContract.Movies.TABLE_NAME,
                        MoviesContract.Movies.COLUMN_NAME_MOVIE_ID + "=?",
                        new String[]{uri.getLastPathSegment()});
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int result;
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIES:
                result = db.update(MoviesContract.Movies.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case MOVIES_WITH_MOVIE_ID:
                result = db.update(MoviesContract.Movies.TABLE_NAME,
                        values,
                        MoviesContract.Movies.COLUMN_NAME_MOVIE_ID + "=?",
                        new String[]{uri.getLastPathSegment()});
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return result;
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
        matcher.addURI(authority, MoviesContract.PATH_MOVIES + "/#", MOVIES_WITH_MOVIE_ID);

        matcher.addURI(authority, MoviesContract.PATH_POPULAR, POPULAR);
        matcher.addURI(authority, MoviesContract.PATH_POPULAR + "/#", POPULAR_WITH_SORT_ID);
        matcher.addURI(authority, MoviesContract.PATH_TOPRATED, TOPRATED);
        matcher.addURI(authority, MoviesContract.PATH_TOPRATED + "/#", TOPRATED_WITH_SORT_ID);

        matcher.addURI(authority, MoviesContract.PATH_FAVORITES, FAVORITES);
        matcher.addURI(authority, MoviesContract.PATH_FAVORITES + "/#", FAVORITES_WITH_SORT_ID);

        matcher.addURI(authority, MoviesContract.PATH_VIDEOS, VIDEOS);
        matcher.addURI(authority, MoviesContract.PATH_VIDEOS + "/#", VIDEOS_WITH_MOVIE_ID);
        matcher.addURI(authority, MoviesContract.PATH_VIDEOS + "/*", VIDEOS_WITH_VIDEO_ID);

        return matcher;
    }

    // Check existence of Movies record by movie_id
    // return _ID
    private long checkMoviesRecordExistence(SQLiteDatabase db, int movieId) {
        long _id = -1;
        // cursor to select _ID where movie_id from table equal to movieId from parameters
        Cursor cursor = db.query(MoviesContract.Movies.TABLE_NAME, // table
                new String[]{MoviesContract.Movies._ID},  // column
                MoviesContract.Movies.COLUMN_NAME_MOVIE_ID + "=?", // where
                new String[]{String.valueOf(movieId)}, // where values
                null, null, null); // group by, having, sort

        if (cursor == null) return -1;
        if (cursor.moveToNext()) _id = cursor.getLong(0);

        cursor.close();
        return _id;
    }

    // this method checks existence of record in the movies table by movie_id
    // and insert new record if record not exist, or update existent record else
    private int moviesRecordInsertOrUpdate(SQLiteDatabase db, ContentValues values) {
        int movieId;
        long result;

        movieId = values.getAsInteger(MoviesContract.Movies.COLUMN_NAME_MOVIE_ID);
        // Check if movies record exist
        result = checkMoviesRecordExistence(db, movieId);
        if (result <= 0)
            // new record - insert
            result = db.insert(MoviesContract.Movies.TABLE_NAME, null, values);
        else
            // existent - update
            result = db.update(MoviesContract.Movies.TABLE_NAME, values,
                    MoviesContract.Movies._ID + "=?", new String[]{String.valueOf(result)});
        if (result > 0)
            return movieId;
        else
            return 0;
    }

}
