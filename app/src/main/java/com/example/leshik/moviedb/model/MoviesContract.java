package com.example.leshik.moviedb.model;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Leshik on 06.12.2016.
 */

public final class MoviesContract {
    private MoviesContract() {
    }

    // Base URIs
    public static final String CONTENT_AUTHORITY = "com.example.leshik.moviedb";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    // Table paths
    public static final String PATH_MOVIES = "movies";
    public static final String PATH_POPULAR = "popular";
    public static final String PATH_TOPRATED = "toprated";
    public static final String PATH_FAVORITES = "favorites";
    public static final String PATH_VIDEOS = "videos";
    public static final String PATH_REVIEWS = "reviews";

    public static final int SHORT_LIST_PROJECTION_INDEX_ID = 0;
    public static final int SHORT_LIST_PROJECTION_INDEX_SORT_ID = 1;
    public static final int SHORT_LIST_PROJECTION_INDEX_MOVIE_ID = 2;
    public static final int SHORT_LIST_PROJECTION_INDEX_POSTER_PATH = 3;

    // Class to describe movies table
    public static abstract class Movies implements BaseColumns {
        // Table name
        static final String TABLE_NAME = "movies";
        // Constants for content provider interface
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
        // Table columns
        public static final String COLUMN_NAME_MOVIE_ID = "movie_id";
        public static final String COLUMN_NAME_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_NAME_OVERVIEW = "overview";
        public static final String COLUMN_NAME_RELEASE_DATE = "release_date";
        public static final String COLUMN_NAME_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_NAME_POPULARITY = "popularity";
        public static final String COLUMN_NAME_POSTER_PATH = "poster_path";
        public static final String COLUMN_NAME_HOMEPAGE = "homepage";
        public static final String COLUMN_NAME_ADULT = "adult";
        public static final String COLUMN_NAME_VIDEO = "video";
        public static final String COLUMN_NAME_LAST_UPDATED = "last_updated";
        public static final String COLUMN_NAME_RUNTIME = "runtime";
        // Default projection
        public static final String[] DETAIL_PROJECTION = {
                _ID,
                COLUMN_NAME_MOVIE_ID,
                COLUMN_NAME_ORIGINAL_TITLE,
                COLUMN_NAME_OVERVIEW,
                COLUMN_NAME_POSTER_PATH,
                COLUMN_NAME_RELEASE_DATE,
                COLUMN_NAME_VOTE_AVERAGE,
                COLUMN_NAME_RUNTIME,
                COLUMN_NAME_LAST_UPDATED
        };
        // ... and indexes for it
        public static final int DETAIL_PROJECTION_INDEX_ID = 0;
        public static final int DETAIL_PROJECTION_INDEX_MOVIE_ID = 1;
        public static final int DETAIL_PROJECTION_INDEX_ORIGINAL_TITLE = 2;
        public static final int DETAIL_PROJECTION_INDEX_OVERVIEW = 3;
        public static final int DETAIL_PROJECTION_INDEX_POSTER_PATH = 4;
        public static final int DETAIL_PROJECTION_INDEX_RELEASE_DATE = 5;
        public static final int DETAIL_PROJECTION_INDEX_VOTE_AVERAGE = 6;
        public static final int DETAIL_PROJECTION_INDEX_RUNTIME = 7;
        public static final int DETAIL_PROJECTION_INDEX_LAST_UPDATED = 8;
        // Create table sql-statement
        static final String CREATE_TABLE_STATEMENT = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME_MOVIE_ID + " INTEGER NOT NULL UNIQUE, " + // movie_id from TMDB
                COLUMN_NAME_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                COLUMN_NAME_OVERVIEW + " TEXT NOT NULL, " +
                COLUMN_NAME_RELEASE_DATE + " TEXT NOT NULL, " + // release date stored in format YYYY-MM-DD
                COLUMN_NAME_VOTE_AVERAGE + " REAL DEFAULT 0.0, " +
                COLUMN_NAME_POPULARITY + " REAL DEFAULT 0.0, " +
                COLUMN_NAME_POSTER_PATH + " TEXT NOT NULL, " +
                COLUMN_NAME_HOMEPAGE + " TEXT, " +
                COLUMN_NAME_ADULT + " INTEGER DEFAULT 0, " + // 0 - false, 1 - true
                COLUMN_NAME_VIDEO + " INTEGER DEFAULT 0, " + // 0 - false, 1 - true
                COLUMN_NAME_RUNTIME + " INTEGER DEFAULT 0, " +
                COLUMN_NAME_LAST_UPDATED + " INTEGER DEFAULT 0 " +
                ");";
        // Delete table sql-statement
        static final String DROP_TABLE_STATEMENT = "DROP TABLE " + TABLE_NAME + ";";

        // URI build method
        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    // Class to describe movies by popularity
    public static abstract class Popular implements BaseColumns {
        // Table name
        static final String TABLE_NAME = "popular";
        // Constants for content provider interface
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_POPULAR).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POPULAR;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POPULAR;
        // Columns
        public static final String COLUMN_NAME_SORT_ID = "sort_id";
        public static final String COLUMN_NAME_MOVIE_ID = "movie_id";
        // Create statement
        static final String CREATE_TABLE_STATEMENT = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME_SORT_ID + " INTEGER NOT NULL UNIQUE, " +
                COLUMN_NAME_MOVIE_ID + " INTEGER NOT NULL, " +
                "FOREIGN KEY (" + COLUMN_NAME_MOVIE_ID + ") " +
                "REFERENCES " + Movies.TABLE_NAME + "(" + Movies.COLUMN_NAME_MOVIE_ID + ") " +
                "ON DELETE RESTRICT ON UPDATE CASCADE " +
                ");";
        // Drop table statement
        static final String DROP_TABLE_STATEMENT = "DROP TABLE " + TABLE_NAME + ";";
        // Wide SELECT query
        static final String SELECT_STATEMENT = "(SELECT " +
                Movies.TABLE_NAME + ".*, " +
                TABLE_NAME + "." + COLUMN_NAME_SORT_ID +
                " FROM " + Movies.TABLE_NAME + ", " + TABLE_NAME +
                " WHERE " + Movies.TABLE_NAME + "." + Movies.COLUMN_NAME_MOVIE_ID + "=" +
                TABLE_NAME + "." + COLUMN_NAME_MOVIE_ID +
                ")";
        // Projection for list fragment
        public static final String[] shortListProjection = {_ID,
                COLUMN_NAME_SORT_ID,
                COLUMN_NAME_MOVIE_ID,
                Movies.COLUMN_NAME_POSTER_PATH};

        // URI build method
        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    // Class to describe movies by popularity
    public static abstract class Toprated implements BaseColumns {
        // Table name
        static final String TABLE_NAME = "toprated";
        // Constants for content provider interface
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TOPRATED).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOPRATED;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOPRATED;
        // Columns
        public static final String COLUMN_NAME_SORT_ID = "sort_id";
        public static final String COLUMN_NAME_MOVIE_ID = "movie_id";
        // Create statement
        static final String CREATE_TABLE_STATEMENT = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME_SORT_ID + " INTEGER NOT NULL UNIQUE, " +
                COLUMN_NAME_MOVIE_ID + " INTEGER NOT NULL, " +
                "FOREIGN KEY (" + COLUMN_NAME_MOVIE_ID + ") " +
                "REFERENCES " + Movies.TABLE_NAME + "(" + Movies.COLUMN_NAME_MOVIE_ID + ") " +
                "ON DELETE RESTRICT ON UPDATE CASCADE " +
                ");";
        // Drop table statement
        static final String DROP_TABLE_STATEMENT = "DROP TABLE " + TABLE_NAME + ";";
        // Wide SELECT query
        static final String SELECT_STATEMENT = "(SELECT " +
                Movies.TABLE_NAME + ".*, " +
                TABLE_NAME + "." + COLUMN_NAME_SORT_ID +
                " FROM " + Movies.TABLE_NAME + ", " + TABLE_NAME +
                " WHERE " + Movies.TABLE_NAME + "." + Movies.COLUMN_NAME_MOVIE_ID + "=" +
                TABLE_NAME + "." + COLUMN_NAME_MOVIE_ID +
                ")";
        // Projection for list fragment
        public static final String[] shortListProjection = {_ID,
                COLUMN_NAME_SORT_ID,
                COLUMN_NAME_MOVIE_ID,
                Movies.COLUMN_NAME_POSTER_PATH};


        // URI build method
        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    // Class to describe favorites table
    public static abstract class Favorites implements BaseColumns {
        // Table name
        static final String TABLE_NAME = "favorites";
        // Constants for content provider interface
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITES;
        // Columns
        public static final String COLUMN_NAME_SORT_ID = "sort_id";
        public static final String COLUMN_NAME_MOVIE_ID = "movie_id";
        // Create statement
        static final String CREATE_TABLE_STATEMENT = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME_SORT_ID + " INTEGER NOT NULL UNIQUE, " +
                COLUMN_NAME_MOVIE_ID + " INTEGER NOT NULL, " +
                "FOREIGN KEY (" + COLUMN_NAME_MOVIE_ID + ") " +
                "REFERENCES " + Movies.TABLE_NAME + "(" + Movies.COLUMN_NAME_MOVIE_ID + ") " +
                "ON DELETE RESTRICT ON UPDATE CASCADE " +
                ");";
        // Drop table statement
        static final String DROP_TABLE_STATEMENT = "DROP TABLE " + TABLE_NAME + ";";
        // Wide SELECT query
        static final String SELECT_STATEMENT = "(SELECT " +
                Movies.TABLE_NAME + ".*, " +
                TABLE_NAME + "." + COLUMN_NAME_SORT_ID +
                " FROM " + Movies.TABLE_NAME + ", " + TABLE_NAME +
                " WHERE " + Movies.TABLE_NAME + "." + Movies.COLUMN_NAME_MOVIE_ID + "=" +
                TABLE_NAME + "." + COLUMN_NAME_MOVIE_ID +
                ")";
        // query to find next (max+1) sort_id
        // we use it while insert new row,
        // because sqlite3 does not allow second autoincrement field
        static final String MAX_SORT_ID_SELECT_STATEMENT = "(SELECT " +
                "MAX(" + COLUMN_NAME_SORT_ID + ") + 1 " +
                " FROM " + TABLE_NAME +
                ")";
        // Projection for list fragment
        public static final String[] shortListProjection = {_ID,
                COLUMN_NAME_SORT_ID,
                COLUMN_NAME_MOVIE_ID,
                Movies.COLUMN_NAME_POSTER_PATH};

        // URI build method
        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }


    // Class videos
    public static abstract class Videos implements BaseColumns {
        // Table name
        static final String TABLE_NAME = "videos";
        // Constants for content provider interface
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_VIDEOS).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VIDEOS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VIDEOS;
        // Columns
        public static final String COLUMN_NAME_MOVIE_ID = "movie_id";
        public static final String COLUMN_NAME_VIDEO_ID = "video_id";
        public static final String COLUMN_NAME_KEY = "video_key";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_SITE = "site";
        public static final String COLUMN_NAME_SIZE = "size";
        public static final String COLUMN_NAME_TYPE = "type";
        // Create statement
        static final String CREATE_TABLE_STATEMENT = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME_VIDEO_ID + " TEXT NOT NULL UNIQUE, " +
                COLUMN_NAME_MOVIE_ID + " INTEGER NOT NULL, " +
                COLUMN_NAME_KEY + " TEXT, " +
                COLUMN_NAME_NAME + " TEXT, " +
                COLUMN_NAME_SITE + " TEXT, " +
                COLUMN_NAME_SIZE + " INTEGER, " +
                COLUMN_NAME_TYPE + " TEXT, " +
                "FOREIGN KEY (" + COLUMN_NAME_MOVIE_ID + ") " +
                "REFERENCES " + Movies.TABLE_NAME + "(" + Movies.COLUMN_NAME_MOVIE_ID + ") " +
                "ON DELETE RESTRICT ON UPDATE CASCADE " +
                ");";
        // Drop table statement
        static final String DROP_TABLE_STATEMENT = "DROP TABLE " + TABLE_NAME + ";";
        // Default projection
        public static final String[] DETAIL_PROJECTION = {
                _ID,
                COLUMN_NAME_MOVIE_ID,
                COLUMN_NAME_VIDEO_ID,
                COLUMN_NAME_KEY,
                COLUMN_NAME_SITE,
                COLUMN_NAME_SIZE,
                COLUMN_NAME_TYPE,
                COLUMN_NAME_NAME
        };
        // ... and indexes for it
        public static final int DETAIL_PROJECTION_INDEX_ID = 0;
        public static final int DETAIL_PROJECTION_INDEX_MOVIE_ID = 1;
        public static final int DETAIL_PROJECTION_INDEX_VIDEO_ID = 2;
        public static final int DETAIL_PROJECTION_INDEX_KEY = 3;
        public static final int DETAIL_PROJECTION_INDEX_SITE = 4;
        public static final int DETAIL_PROJECTION_INDEX_SIZE = 5;
        public static final int DETAIL_PROJECTION_INDEX_TYPE = 6;
        public static final int DETAIL_PROJECTION_INDEX_NAME = 7;

        // URI build method with video_id
        public static Uri buildUri(String id) {
            return Uri.withAppendedPath(CONTENT_URI, id);
        }

        // ... and with movie_id
        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    // Class reviews
    public static abstract class Reviews implements BaseColumns {
        // Table name
        static final String TABLE_NAME = "reviews";
        // Constants for content provider interface
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEWS).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEWS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEWS;
        // Columns
        public static final String COLUMN_NAME_MOVIE_ID = "movie_id";
        public static final String COLUMN_NAME_REVIEW_ID = "review_id";
        public static final String COLUMN_NAME_AUTHOR = "author";
        public static final String COLUMN_NAME_CONTENT = "content";
        public static final String COLUMN_NAME_URL = "url";
        // Create statement
        static final String CREATE_TABLE_STATEMENT = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME_REVIEW_ID + " TEXT NOT NULL UNIQUE, " +
                COLUMN_NAME_MOVIE_ID + " INTEGER NOT NULL, " +
                COLUMN_NAME_AUTHOR + " TEXT, " +
                COLUMN_NAME_CONTENT + " TEXT, " +
                COLUMN_NAME_URL + " TEXT, " +
                "FOREIGN KEY (" + COLUMN_NAME_MOVIE_ID + ") " +
                "REFERENCES " + Movies.TABLE_NAME + "(" + Movies.COLUMN_NAME_MOVIE_ID + ") " +
                "ON DELETE RESTRICT ON UPDATE CASCADE " +
                ");";
        // Drop table statement
        static final String DROP_TABLE_STATEMENT = "DROP TABLE " + TABLE_NAME + ";";
        // Default projection
        public static final String[] DETAIL_PROJECTION = {
                _ID,
                COLUMN_NAME_MOVIE_ID,
                COLUMN_NAME_REVIEW_ID,
                COLUMN_NAME_AUTHOR,
                COLUMN_NAME_CONTENT,
                COLUMN_NAME_URL
        };
        // ... and indexes for it
        public static final int DETAIL_PROJECTION_INDEX_ID = 0;
        public static final int DETAIL_PROJECTION_INDEX_MOVIE_ID = 1;
        public static final int DETAIL_PROJECTION_INDEX_REVIEW_ID = 2;
        public static final int DETAIL_PROJECTION_INDEX_AUTHOR = 3;
        public static final int DETAIL_PROJECTION_INDEX_CONTENT = 4;
        public static final int DETAIL_PROJECTION_INDEX_URL = 5;

        // URI build method with review_id
        public static Uri buildUri(String id) {
            return Uri.withAppendedPath(CONTENT_URI, id);
        }

        // ... and with movie_id
        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

}
