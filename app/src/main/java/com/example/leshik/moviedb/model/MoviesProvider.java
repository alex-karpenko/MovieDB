package com.example.leshik.moviedb.model;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
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
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null, null,
                        sortOrder);
                break;
            case POPULAR:
                returnCursor = db.query(MoviesContract.Popular.SELECT_STATEMENT,
                        projection,
                        selection,
                        selectionArgs,
                        null, null,
                        sortOrder);
                break;
            case POPULAR_WITH_SORT_ID:
                returnCursor = db.query(MoviesContract.Popular.SELECT_STATEMENT,
                        projection,
                        MoviesContract.Popular.COLUMN_NAME_SORT_ID + "=?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null, null,
                        sortOrder);
                break;
            case TOPRATED:
                returnCursor = db.query(MoviesContract.TopRated.SELECT_STATEMENT,
                        projection,
                        selection,
                        selectionArgs,
                        null, null,
                        sortOrder);
                break;
            case TOPRATED_WITH_SORT_ID:
                returnCursor = db.query(MoviesContract.TopRated.SELECT_STATEMENT,
                        projection,
                        MoviesContract.TopRated.COLUMN_NAME_SORT_ID + "=?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null, null,
                        sortOrder);
                break;
            case FAVORITES:
                returnCursor = db.query(MoviesContract.Favorites.SELECT_STATEMENT,
                        projection,
                        selection,
                        selectionArgs,
                        null, null,
                        sortOrder);
                break;
            case FAVORITES_WITH_SORT_ID:
                returnCursor = db.query(MoviesContract.Favorites.SELECT_STATEMENT,
                        projection,
                        MoviesContract.Favorites.COLUMN_NAME_SORT_ID + "=?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null, null,
                        sortOrder);
                break;
            case VIDEOS:
                returnCursor = db.query(MoviesContract.Videos.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null, null,
                        sortOrder);
                break;
            case VIDEOS_WITH_MOVIE_ID:
                returnCursor = db.query(MoviesContract.Videos.TABLE_NAME,
                        projection,
                        MoviesContract.Videos.COLUMN_NAME_MOVIE_ID + "=?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null, null,
                        sortOrder);
                break;
            case VIDEOS_WITH_VIDEO_ID:
                returnCursor = db.query(MoviesContract.Videos.TABLE_NAME,
                        projection,
                        MoviesContract.Videos.COLUMN_NAME_VIDEO_ID + "=?",
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
        int id;

        switch (match) {
            case MOVIES:
                id = recordInsertOrUpdateHelper_integerId(db, values, MoviesContract.Movies.TABLE_NAME,
                        MoviesContract.Movies.COLUMN_NAME_MOVIE_ID);
                if (id >= 0)
                    returnUri = MoviesContract.Movies.buildUri(id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case POPULAR:
                id = recordInsertOrUpdateHelper_integerId(db, values, MoviesContract.Popular.TABLE_NAME,
                        MoviesContract.Popular.COLUMN_NAME_SORT_ID);
                if (id >= 0)
                    returnUri = MoviesContract.Popular.buildUri(id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case TOPRATED:
                id = recordInsertOrUpdateHelper_integerId(db, values, MoviesContract.TopRated.TABLE_NAME,
                        MoviesContract.TopRated.COLUMN_NAME_SORT_ID);
                if (id >= 0)
                    returnUri = MoviesContract.TopRated.buildUri(id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case FAVORITES:
                id = recordInsertOrUpdateHelper_integerId(db, values, MoviesContract.Favorites.TABLE_NAME,
                        MoviesContract.Favorites.COLUMN_NAME_SORT_ID);
                if (id >= 0)
                    returnUri = MoviesContract.Favorites.buildUri(id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case VIDEOS:
                String videoId = recordInsertOrUpdateHelper_stringId(db, values, MoviesContract.Videos.TABLE_NAME,
                        MoviesContract.Videos.COLUMN_NAME_VIDEO_ID);
                if (videoId != null)
                    returnUri = MoviesContract.Videos.buildUri(videoId);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    private int bulkInsertHelper_integerId(SQLiteDatabase db, ContentValues[] values, String tableName, String idName) {
        int result = 0;

        db.beginTransaction();
        try {
            for (ContentValues v : values) {
                int r = recordInsertOrUpdateHelper_integerId(db, v, tableName, idName);
                if (r >= 0) result++;
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return result;
    }

    private int bulkInsertHelper_stringId(SQLiteDatabase db, ContentValues[] values, String tableName, String idName) {
        int result = 0;

        db.beginTransaction();
        try {
            for (ContentValues v : values) {
                String r = recordInsertOrUpdateHelper_stringId(db, v, tableName, idName);
                if (r != null) result++;
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return result;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int result;
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIES:
                result = bulkInsertHelper_integerId(db, values, MoviesContract.Movies.TABLE_NAME, MoviesContract.Movies.COLUMN_NAME_MOVIE_ID);
                break;
            case POPULAR:
                result = bulkInsertHelper_integerId(db, values, MoviesContract.Popular.TABLE_NAME, MoviesContract.Popular.COLUMN_NAME_SORT_ID);
                break;
            case TOPRATED:
                result = bulkInsertHelper_integerId(db, values, MoviesContract.TopRated.TABLE_NAME, MoviesContract.TopRated.COLUMN_NAME_SORT_ID);
                break;
            case FAVORITES:
                result = bulkInsertHelper_integerId(db, values, MoviesContract.Favorites.TABLE_NAME, MoviesContract.Favorites.COLUMN_NAME_SORT_ID);
                break;
            case VIDEOS:
                result = bulkInsertHelper_stringId(db, values, MoviesContract.Videos.TABLE_NAME, MoviesContract.Videos.COLUMN_NAME_VIDEO_ID);
                break;
            default:
                return super.bulkInsert(uri, values);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return result;
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
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            case POPULAR:
                result = db.delete(MoviesContract.Popular.TABLE_NAME, selection, selectionArgs);
                break;
            case POPULAR_WITH_SORT_ID:
                result = db.delete(MoviesContract.Popular.TABLE_NAME,
                        MoviesContract.Popular.COLUMN_NAME_SORT_ID + "=?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            case TOPRATED:
                result = db.delete(MoviesContract.TopRated.TABLE_NAME, selection, selectionArgs);
                break;
            case TOPRATED_WITH_SORT_ID:
                result = db.delete(MoviesContract.TopRated.TABLE_NAME,
                        MoviesContract.TopRated.COLUMN_NAME_SORT_ID + "=?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            case FAVORITES:
                result = db.delete(MoviesContract.Favorites.TABLE_NAME, selection, selectionArgs);
                break;
            case FAVORITES_WITH_SORT_ID:
                result = db.delete(MoviesContract.Favorites.TABLE_NAME,
                        MoviesContract.Favorites.COLUMN_NAME_SORT_ID + "=?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            case VIDEOS:
                result = db.delete(MoviesContract.Videos.TABLE_NAME, selection, selectionArgs);
                break;
            case VIDEOS_WITH_MOVIE_ID:
                result = db.delete(MoviesContract.Videos.TABLE_NAME,
                        MoviesContract.Videos.COLUMN_NAME_MOVIE_ID + "=?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            case VIDEOS_WITH_VIDEO_ID:
                result = db.delete(MoviesContract.Videos.TABLE_NAME,
                        MoviesContract.Videos.COLUMN_NAME_VIDEO_ID + "=?",
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
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            case POPULAR:
                result = db.update(MoviesContract.Popular.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case POPULAR_WITH_SORT_ID:
                result = db.update(MoviesContract.Popular.TABLE_NAME,
                        values,
                        MoviesContract.Popular.COLUMN_NAME_SORT_ID + "=?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            case TOPRATED:
                result = db.update(MoviesContract.TopRated.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case TOPRATED_WITH_SORT_ID:
                result = db.update(MoviesContract.TopRated.TABLE_NAME,
                        values,
                        MoviesContract.TopRated.COLUMN_NAME_SORT_ID + "=?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            case FAVORITES:
                result = db.update(MoviesContract.Favorites.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case FAVORITES_WITH_SORT_ID:
                result = db.update(MoviesContract.Favorites.TABLE_NAME,
                        values,
                        MoviesContract.Favorites.COLUMN_NAME_SORT_ID + "=?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            case VIDEOS:
                result = db.update(MoviesContract.Videos.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case VIDEOS_WITH_MOVIE_ID:
                result = db.update(MoviesContract.Videos.TABLE_NAME,
                        values,
                        MoviesContract.Videos.COLUMN_NAME_MOVIE_ID + "=?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            case VIDEOS_WITH_VIDEO_ID:
                result = db.update(MoviesContract.Videos.TABLE_NAME,
                        values,
                        MoviesContract.Videos.COLUMN_NAME_VIDEO_ID + "=?",
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

    // this method checks existence of record in the table by idName
    // and insert new record if record not exist, or update existent record else
    private int recordInsertOrUpdateHelper_integerId(SQLiteDatabase db, ContentValues values, String tableName, String idName) {
        int movieId;
        long result = -1;

        movieId = values.getAsInteger(idName);
        // Check if record exist
        // cursor to select _ID where movie_id from table equal to movieId from parameters
        Cursor cursor = db.query(tableName, // table
                new String[]{BaseColumns._ID},  // column
                idName + "=?", // where
                new String[]{String.valueOf(movieId)}, // where values
                null, null, null); // group by, having, sort

        if (cursor != null) {
            if (cursor.moveToNext()) result = cursor.getLong(0);
            cursor.close();
        }

        if (result <= 0)
            // new record - insert
            result = db.insert(tableName, null, values);
        else
            // existent - update
            result = db.update(tableName, values,
                    BaseColumns._ID + "=?", new String[]{String.valueOf(result)});
        if (result > 0)
            return movieId;
        else
            return 0;
    }

    // this method checks existence of record in the table by video_id
    // and insert new record if record not exist, or update existent record else
    private String recordInsertOrUpdateHelper_stringId(SQLiteDatabase db, ContentValues values, String tableName, String idName) {
        String id;
        long result = -1;

        id = values.getAsString(idName);
        // Check if record exist
        // cursor to select _ID where movie_id from table equal to movieId from parameters
        Cursor cursor = db.query(tableName, // table
                new String[]{BaseColumns._ID},  // column
                idName + "=?", // where
                new String[]{id}, // where values
                null, null, null); // group by, having, sort

        if (cursor != null) {
            if (cursor.moveToNext()) result = cursor.getLong(0);
            cursor.close();
        }

        if (result <= 0)
            // new record - insert
            result = db.insert(tableName, null, values);
        else
            // existent - update
            result = db.update(tableName, values,
                    BaseColumns._ID + "=?", new String[]{String.valueOf(result)});
        if (result > 0)
            return id;
        else
            return null;
    }

}
