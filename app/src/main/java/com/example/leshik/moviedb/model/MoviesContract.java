package com.example.leshik.moviedb.model;

import android.provider.BaseColumns;

/**
 * Created by Leshik on 06.12.2016.
 */

final class MoviesContract {
    private MoviesContract() {
    }

    // Class to describe movies table
    static abstract class Movies implements BaseColumns {
        // Table name
        static final String TABLE_NAME = "movies";
        // Table columns
        static final String COLUMN_NAME_ID = "id";
        static final String COLUMN_NAME_ORIGINAL_TITLE = "original_title";
        static final String COLUMN_NAME_OVERVIEW = "overview";
        static final String COLUMN_NAME_RELEASE_DATE = "release_date";
        static final String COLUMN_NAME_VOTE_AVERAGE = "vote_average";
        static final String COLUMN_NAME_POPULARITY = "popularity";
        static final String COLUMN_NAME_POSTER_PATH = "poster_path";
        static final String COLUMN_NAME_HOMEPAGE = "homepage";
        static final String COLUMN_NAME_ADULT = "adult";
        static final String COLUMN_NAME_VIDEO = "video";
        // Create table sql-statement
        static final String CREATE_TABLE_STATEMENT = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_NAME_ID + " INTEGER PRIMARY KEY, " + // movie_id from TMDB
                COLUMN_NAME_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                COLUMN_NAME_OVERVIEW + " TEXT NOT NULL, " +
                COLUMN_NAME_RELEASE_DATE + "TEXT NOT NULL, " + // release date stored in format YYYY-MM-DD
                COLUMN_NAME_VOTE_AVERAGE + "REAL DEFAULT 0.0, " +
                COLUMN_NAME_POPULARITY + "REAL DEFAULT 0.0, " +
                COLUMN_NAME_POSTER_PATH + "TEXT, " +
                COLUMN_NAME_HOMEPAGE + "TEXT, " +
                COLUMN_NAME_ADULT + "INTEGER DEFAULT 0, " + // 0 - false, 1 - true
                COLUMN_NAME_VIDEO + "INTEGER DEFAULT 0 " + // 0 - false, 1 - true
                ");";
        // Delete table sql-statement
        static final String DROP_TABLE_STATEMENT = "DROP TABLE " + TABLE_NAME + ";";
    }

    // Class to describe movies by popularity
    static abstract class Popularity implements BaseColumns {
        // Table name
        static final String TABLE_NAME = "popularity";
        // Columns
        static final String COLUMN_NAME_SORT_ID = "sort_id";
        static final String COLUMN_NAME_MOVIE_ID = "movie_id";
        // Create statement
        static final String CREATE_TABLE_STATEMENT = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_NAME_SORT_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_NAME_MOVIE_ID + " INTEGER NOT NULL, " +
                "FOREIGN KEY (" + COLUMN_NAME_MOVIE_ID + ") " +
                "REFERENCES " + Movies.TABLE_NAME + "(" + Movies.COLUMN_NAME_ID + ") " +
                "ON DELETE CASCADE ON UPDATE CASCADE " +
                ");";
        // Drop table statement
        static final String DROP_TABLE_STATEMENT = "DROP TABLE " + TABLE_NAME + ";";
    }

    // Class to describe movies by popularity
    static abstract class TopRated implements BaseColumns {
        // Table name
        static final String TABLE_NAME = "top_rated";
        // Columns
        static final String COLUMN_NAME_SORT_ID = "sort_id";
        static final String COLUMN_NAME_MOVIE_ID = "movie_id";
        // Create statement
        static final String CREATE_TABLE_STATEMENT = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_NAME_SORT_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_NAME_MOVIE_ID + " INTEGER NOT NULL, " +
                "FOREIGN KEY (" + COLUMN_NAME_MOVIE_ID + ") " +
                "REFERENCES " + Movies.TABLE_NAME + "(" + Movies.COLUMN_NAME_ID + ") " +
                "ON DELETE CASCADE ON UPDATE CASCADE " +
                ");";
        // Drop table statement
        static final String DROP_TABLE_STATEMENT = "DROP TABLE " + TABLE_NAME + ";";
    }

    // Class to describe favorites table
    static abstract class Favorites implements BaseColumns {
        // Table name
        static final String TABLE_NAME = "favorites";
        // Columns
        static final String COLUMN_NAME_SORT_ID = "sort_id";
        static final String COLUMN_NAME_MOVIE_ID = "movie_id";
        // Create statement
        static final String CREATE_TABLE_STATEMENT = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_NAME_SORT_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_NAME_MOVIE_ID + " INTEGER NOT NULL, " +
                "FOREIGN KEY (" + COLUMN_NAME_MOVIE_ID + ") " +
                "REFERENCES " + Movies.TABLE_NAME + "(" + Movies.COLUMN_NAME_ID + ") " +
                "ON DELETE CASCADE ON UPDATE CASCADE " +
                ");";
        // Drop table statement
        static final String DROP_TABLE_STATEMENT = "DROP TABLE " + TABLE_NAME + ";";
    }


    // Class videos
    static abstract class Videos implements BaseColumns {
        // Table name
        static final String TABLE_NAME = "videos";
        // Columns
        static final String COLUMN_NAME_MOVIE_ID = "movie_id";
        static final String COLUMN_NAME_VIDEO_ID = "video_id";
        static final String COLUMN_NAME_KEY = "video_key";
        static final String COLUMN_NAME_NAME = "name";
        static final String COLUMN_NAME_SITE = "site";
        static final String COLUMN_NAME_SIZE = "size";
        static final String COLUMN_NAME_TYPE = "type";
        // Create statement
        static final String CREATE_TABLE_STATEMENT = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_NAME_VIDEO_ID + " TEXT PRIMARY KEY, " +
                COLUMN_NAME_MOVIE_ID + "INTEGER NOT NULL, " +
                COLUMN_NAME_KEY + "TEXT, " +
                COLUMN_NAME_NAME + "TEXT, " +
                COLUMN_NAME_SITE + "TEXT, " +
                COLUMN_NAME_SIZE + "INTEGER, " +
                COLUMN_NAME_TYPE + "TEXT, " +
                "FOREIGN KEY (" + COLUMN_NAME_MOVIE_ID + ") " +
                "REFERENCES " + Movies.TABLE_NAME + "(" + Movies.COLUMN_NAME_ID + ") " +
                "ON DELETE CASCADE ON UPDATE CASCADE " +
                ");";
        // Drop table statement
        static final String DROP_TABLE_STATEMENT = "DROP TABLE " + TABLE_NAME + ";";
    }
}
