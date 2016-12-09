package com.example.leshik.moviedb.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Leshik on 08.12.2016.
 */

@RunWith(AndroidJUnit4.class)
public class TestDb {
    public static final String LOG_TAG = TestDb.class.getSimpleName();
    private static final Context mContext = InstrumentationRegistry.getTargetContext();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(MoviesDbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    @Before
    public void setUp() {
        deleteTheDatabase();
    }

    @Test
    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MoviesContract.Movies.TABLE_NAME);
        tableNameHashSet.add(MoviesContract.Popular.TABLE_NAME);
        tableNameHashSet.add(MoviesContract.TopRated.TABLE_NAME);
        tableNameHashSet.add(MoviesContract.Favorites.TABLE_NAME);
        tableNameHashSet.add(MoviesContract.Videos.TABLE_NAME);

        deleteTheDatabase();
        SQLiteDatabase db = new MoviesDbHelper(mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while (c.moveToNext());

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without all necessary tables:" + tableNameHashSet.toString(),
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        // 1. movies
        c = db.rawQuery("PRAGMA table_info(" + MoviesContract.Movies.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> moviesColumnHashSet = new HashSet<String>();
        moviesColumnHashSet.add(MoviesContract.Movies._ID);
        moviesColumnHashSet.add(MoviesContract.Movies.COLUMN_NAME_MOVIE_ID);
        moviesColumnHashSet.add(MoviesContract.Movies.COLUMN_NAME_ORIGINAL_TITLE);
        moviesColumnHashSet.add(MoviesContract.Movies.COLUMN_NAME_OVERVIEW);
        moviesColumnHashSet.add(MoviesContract.Movies.COLUMN_NAME_RELEASE_DATE);
        moviesColumnHashSet.add(MoviesContract.Movies.COLUMN_NAME_VOTE_AVERAGE);
        moviesColumnHashSet.add(MoviesContract.Movies.COLUMN_NAME_POPULARITY);
        moviesColumnHashSet.add(MoviesContract.Movies.COLUMN_NAME_POSTER_PATH);
        moviesColumnHashSet.add(MoviesContract.Movies.COLUMN_NAME_HOMEPAGE);
        moviesColumnHashSet.add(MoviesContract.Movies.COLUMN_NAME_ADULT);
        moviesColumnHashSet.add(MoviesContract.Movies.COLUMN_NAME_VIDEO);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            moviesColumnHashSet.remove(columnName);
        } while (c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required movies entry columns:" + moviesColumnHashSet.toString(),
                moviesColumnHashSet.isEmpty());
        c.close();

        // 2. popular
        c = db.rawQuery("PRAGMA table_info(" + MoviesContract.Popular.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> popularColumnHashSet = new HashSet<String>();
        popularColumnHashSet.add(MoviesContract.Popular._ID);
        popularColumnHashSet.add(MoviesContract.Popular.COLUMN_NAME_SORT_ID);
        popularColumnHashSet.add(MoviesContract.Popular.COLUMN_NAME_MOVIE_ID);

        columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            popularColumnHashSet.remove(columnName);
        } while (c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required popular entry columns:" + popularColumnHashSet.toString(),
                popularColumnHashSet.isEmpty());
        c.close();

        // 3. toprated
        c = db.rawQuery("PRAGMA table_info(" + MoviesContract.TopRated.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> topratedColumnHashSet = new HashSet<String>();
        topratedColumnHashSet.add(MoviesContract.TopRated._ID);
        topratedColumnHashSet.add(MoviesContract.TopRated.COLUMN_NAME_SORT_ID);
        topratedColumnHashSet.add(MoviesContract.TopRated.COLUMN_NAME_MOVIE_ID);

        columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            topratedColumnHashSet.remove(columnName);
        } while (c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required toprated entry columns:" + topratedColumnHashSet.toString(),
                topratedColumnHashSet.isEmpty());
        c.close();

        // 4. favorites
        c = db.rawQuery("PRAGMA table_info(" + MoviesContract.Favorites.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> favoritesColumnHashSet = new HashSet<String>();
        favoritesColumnHashSet.add(MoviesContract.Favorites._ID);
        favoritesColumnHashSet.add(MoviesContract.Favorites.COLUMN_NAME_SORT_ID);
        favoritesColumnHashSet.add(MoviesContract.Favorites.COLUMN_NAME_MOVIE_ID);

        columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            favoritesColumnHashSet.remove(columnName);
        } while (c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required favvorites entry columns:" + favoritesColumnHashSet.toString(),
                favoritesColumnHashSet.isEmpty());
        c.close();

        // 4. videos
        c = db.rawQuery("PRAGMA table_info(" + MoviesContract.Videos.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> videosColumnHashSet = new HashSet<String>();
        videosColumnHashSet.add(MoviesContract.Videos._ID);
        videosColumnHashSet.add(MoviesContract.Videos.COLUMN_NAME_VIDEO_ID);
        videosColumnHashSet.add(MoviesContract.Videos.COLUMN_NAME_MOVIE_ID);
        videosColumnHashSet.add(MoviesContract.Videos.COLUMN_NAME_KEY);
        videosColumnHashSet.add(MoviesContract.Videos.COLUMN_NAME_NAME);
        videosColumnHashSet.add(MoviesContract.Videos.COLUMN_NAME_SITE);
        videosColumnHashSet.add(MoviesContract.Videos.COLUMN_NAME_SIZE);
        videosColumnHashSet.add(MoviesContract.Videos.COLUMN_NAME_TYPE);

        columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            favoritesColumnHashSet.remove(columnName);
        } while (c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required favvorites entry columns:" + favoritesColumnHashSet.toString(),
                favoritesColumnHashSet.isEmpty());
        c.close();


        db.close();
    }

}
