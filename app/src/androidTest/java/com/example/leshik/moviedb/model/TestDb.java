package com.example.leshik.moviedb.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.HashSet;

import android.support.test.runner.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by Leshik on 08.12.2016.
 */

@RunWith(AndroidJUnit4.class)
@SmallTest
public class TestDb {
    public static final String LOG_TAG = TestDb.class.getSimpleName();

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

        mContext.deleteDatabase(MoviesDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new MoviesDbHelper(
                this.mContext).getWritableDatabase();
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
        c = db.rawQuery("PRAGMA table_info(" + MoviesContract.Movies.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> moviesColumnHashSet = new HashSet<String>();
//        moviesColumnHashSet.add(MoviesContract.Movies._ID);
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
        db.close();
    }

}
