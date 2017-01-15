package com.example.leshik.moviedb.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Leshik on 06.12.2016.
 */

public class MoviesDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Movies table in first place because foreign indexes
        db.execSQL(MoviesContract.Movies.CREATE_TABLE_STATEMENT);
        // create rest tables
        db.execSQL(MoviesContract.Popular.CREATE_TABLE_STATEMENT);
        db.execSQL(MoviesContract.Toprated.CREATE_TABLE_STATEMENT);
        db.execSQL(MoviesContract.Favorites.CREATE_TABLE_STATEMENT);
        db.execSQL(MoviesContract.Videos.CREATE_TABLE_STATEMENT);
        db.execSQL(MoviesContract.Reviews.CREATE_TABLE_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // In first time we have to delete tables with foreign indexes on movies table
//        db.execSQL(MoviesContract.Popular.DROP_TABLE_STATEMENT);
//        db.execSQL(MoviesContract.Toprated.DROP_TABLE_STATEMENT);
//        db.execSQL(MoviesContract.Favorites.DROP_TABLE_STATEMENT);
//        db.execSQL(MoviesContract.Videos.DROP_TABLE_STATEMENT);
//        db.execSQL(MoviesContract.Reviews.DROP_TABLE_STATEMENT);
        // and drop movies table after all
//        db.execSQL(MoviesContract.Movies.DROP_TABLE_STATEMENT);
//        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // :))
//        onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        // To force foreign keys to work ...
        db.setForeignKeyConstraintsEnabled(true);
    }
}
