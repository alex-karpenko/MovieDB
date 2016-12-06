package com.example.leshik.moviedb.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Leshik on 06.12.2016.
 */

public class MoviesDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MoviesContract.Movies.CREATE_TABLE_STATEMENT);
        db.execSQL(MoviesContract.Popularity.CREATE_TABLE_STATEMENT);
        db.execSQL(MoviesContract.TopRated.CREATE_TABLE_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(MoviesContract.Movies.DROP_TABLE_STATEMENT);
        db.execSQL(MoviesContract.Popularity.DROP_TABLE_STATEMENT);
        db.execSQL(MoviesContract.TopRated.DROP_TABLE_STATEMENT);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }
}
