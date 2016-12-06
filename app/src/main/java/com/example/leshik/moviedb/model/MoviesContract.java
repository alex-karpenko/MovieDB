package com.example.leshik.moviedb.model;

import android.provider.BaseColumns;

/**
 * Created by Leshik on 06.12.2016.
 */

public final class MoviesContract {
    private MoviesContract() {
    }

    // Database file name
    public static final String DB_NAME = "movies.db";

    // Class to describe movies table
    public static abstract class Movies implements BaseColumns {
        // Table name
        public static final String TABLE_NAME = "movies";
        // Table columns
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_NAME_OVERVIEW = "overview";
        public static final String COLUMN_NAME_RELEASE_DATE = "release_date";
        public static final String COLUMN_NAME_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_NAME_POPULARITY = "popularity";
        public static final String COLUMN_NAME_POSTER_PATH = "poster_path";
        public static final String COLUMN_NAME_HOMEPAGE = "homepage";
        public static final String COLUMN_NAME_ADULT = "adult";
        public static final String COLUMN_NAME_VIDEO = "video";
        // Create table sql-statement
        public static final String CREATE_TABLE_STATEMENT = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_NAME_ID + " INTEGER PRIMARY KEY, " + // movie_id from TMDB
                COLUMN_NAME_ORIGINAL_TITLE + " TEXT, " +
                COLUMN_NAME_OVERVIEW + " TEXT, " +
                COLUMN_NAME_RELEASE_DATE + "TEXT, " + // release date stored in format YYYY-MM-DD
                COLUMN_NAME_VOTE_AVERAGE + "REAL, " +
                COLUMN_NAME_POPULARITY + "REAL, " +
                COLUMN_NAME_POSTER_PATH + "TEXT, " +
                COLUMN_NAME_HOMEPAGE + "TEXT, " +
                COLUMN_NAME_ADULT + "INTEGER, " + // 0 - false, 1 - true
                COLUMN_NAME_VIDEO + "INTEGER " + // 0 - false, 1 - true
                ")";
    }
}
