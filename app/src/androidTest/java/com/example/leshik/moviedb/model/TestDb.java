package com.example.leshik.moviedb.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Leshik on 08.12.2016.
 */

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestDb {
    public static final String LOG_TAG = TestDb.class.getSimpleName();
    private static final Context mContext = InstrumentationRegistry.getTargetContext();
    private static final int MOVIES_FAKE_DATA_ROWS = 20;

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
    public void test01_CreateDb() throws Throwable {
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

    @Test
    public void test02_MoviesTable() {
        MoviesDbHelper dbHelper = new MoviesDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        insertTestData_intoMovies(db);

        Cursor c = db.query(MoviesContract.Movies.TABLE_NAME, new String[]{"COUNT(*)"}, null, null, null, null, null);
        assertNotNull("Query to count rows in movies table failed.", c);
        assertTrue("moveToNext() failed.", c.moveToNext());
        assertEquals("Counted rows not equal to inserted.", MOVIES_FAKE_DATA_ROWS, c.getInt(0));
        c.close();

        db.close();
    }

    @Test
    public void test03_PopularTable() {
        MoviesDbHelper dbHelper = new MoviesDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        insertTestData_intoMovies(db);
        insertTestData_intoPopular(db);

        Cursor c = db.query(MoviesContract.Popular.TABLE_NAME, new String[]{"COUNT(*)"}, null, null, null, null, null);
        assertNotNull("Query to count rows in popular table failed.", c);
        assertTrue("moveToNext() failed.", c.moveToNext());
        assertEquals("Counted rows not equal to inserted.", MOVIES_FAKE_DATA_ROWS, c.getInt(0));
        c.close();

        db.close();
    }

    @Test
    public void test04_TopratedTable() {
        MoviesDbHelper dbHelper = new MoviesDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        insertTestData_intoMovies(db);
        insertTestData_intoToprated(db);

        Cursor c = db.query(MoviesContract.TopRated.TABLE_NAME, new String[]{"COUNT(*)"}, null, null, null, null, null);
        assertNotNull("Query to count rows in popular table failed.", c);
        assertTrue("moveToNext() failed.", c.moveToNext());
        assertEquals("Counted rows not equal to inserted.", MOVIES_FAKE_DATA_ROWS, c.getInt(0));
        c.close();

        db.close();
    }

    @Test(expected = SQLiteConstraintException.class)
    public void test05_ForeignKeys() {
        MoviesDbHelper dbHelper = new MoviesDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        insertTestData_intoMovies(db);
        insertTestData_intoToprated(db);
        insertTestData_intoPopular(db);

        db.delete(MoviesContract.Movies.TABLE_NAME, null, null);
        assertTrue("Foreign key constrain failed - rows from movies table was deleted.", false);

        db.close();
    }

    @Test
    public void test06_Select() {
        MoviesDbHelper dbHelper = new MoviesDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        insertTestData_intoMovies(db);
        insertTestData_intoToprated(db);
        insertTestData_intoPopular(db);

        Cursor c = db.query(MoviesContract.Popular.SELECT_STATEMENT, null, null, null, null, null, null);
        assertNotNull("Query to select wide popular data table failed.", c);
        assertTrue("moveToNext() failed.", c.moveToNext());
        assertEquals("Count of rows in the wide popular table is wrong", MOVIES_FAKE_DATA_ROWS, c.getCount());
        c.close();

        c = db.query(MoviesContract.TopRated.SELECT_STATEMENT, null, null, null, null, null, null);
        assertNotNull("Query to select wide toprated data table failed.", c);
        assertTrue("moveToNext() failed.", c.moveToNext());
        assertEquals("Count of rows in the wide toprated table is wrong", MOVIES_FAKE_DATA_ROWS, c.getCount());
        c.close();

        db.close();
    }

    // Helper methods to insert test data into the tables
    private void insertTestData_intoMovies(SQLiteDatabase db) {
        List<ContentValues> data = moviesFakeData();

        db.beginTransaction();
        for (ContentValues d : data) db.insert(MoviesContract.Movies.TABLE_NAME, null, d);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    private void insertTestData_intoPopular(SQLiteDatabase db) {
        List<ContentValues> data = popularFakeData();

        db.beginTransaction();
        for (ContentValues d : data) db.insert(MoviesContract.Popular.TABLE_NAME, null, d);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    private void insertTestData_intoToprated(SQLiteDatabase db) {
        List<ContentValues> data = topratedFakeData();

        db.beginTransaction();
        for (ContentValues d : data) db.insert(MoviesContract.TopRated.TABLE_NAME, null, d);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    private void insertTestData_intoFavorites(SQLiteDatabase db) {

    }

    private void insertTestData_intoVideos(SQLiteDatabase db) {

    }

    // Methods to create fake data for our tables
    List<ContentValues> moviesFakeData() {
        List<ContentValues> returnData = new ArrayList<>();
        ContentValues dataValues;

        for (int i = 0; i < MOVIES_FAKE_DATA_ROWS; i++) {
            dataValues = new ContentValues();
            dataValues.put(MoviesContract.Movies.COLUMN_NAME_MOVIE_ID, 100 + i * 2);
            dataValues.put(MoviesContract.Movies.COLUMN_NAME_ORIGINAL_TITLE, "Original title #" + Integer.valueOf(i).toString());
            dataValues.put(MoviesContract.Movies.COLUMN_NAME_OVERVIEW, "Overview #" + Integer.valueOf(i).toString());
            dataValues.put(MoviesContract.Movies.COLUMN_NAME_RELEASE_DATE,
                    Integer.valueOf(1900 + Integer.valueOf((int) (Math.random() * 100))).toString() + "-" +
                            Integer.valueOf((int) (Math.random() * 12)).toString() + "-" +
                            Integer.valueOf((int) (Math.random() * 28)).toString());
            dataValues.put(MoviesContract.Movies.COLUMN_NAME_VOTE_AVERAGE, Math.random() * 10);
            dataValues.put(MoviesContract.Movies.COLUMN_NAME_POPULARITY, Math.random() * 100);
            dataValues.put(MoviesContract.Movies.COLUMN_NAME_POSTER_PATH, "poster_path_" + Integer.valueOf(i).toString());
            dataValues.put(MoviesContract.Movies.COLUMN_NAME_HOMEPAGE, "http://movie" + Integer.valueOf(i).toString() + ".home.page.com/");
            dataValues.put(MoviesContract.Movies.COLUMN_NAME_ADULT, (i + 1) % 2);
            dataValues.put(MoviesContract.Movies.COLUMN_NAME_VIDEO, i % 2);

            returnData.add(dataValues);
        }
        return returnData;
    }

    List<ContentValues> popularFakeData() {
        List<ContentValues> returnData = new ArrayList<>();
        ContentValues dataValues;

        for (int i = 0; i < MOVIES_FAKE_DATA_ROWS; i++) {
            dataValues = new ContentValues();
            dataValues.put(MoviesContract.Popular.COLUMN_NAME_SORT_ID, i + 1);
            dataValues.put(MoviesContract.Popular.COLUMN_NAME_MOVIE_ID, 100 + i * 2);

            returnData.add(dataValues);
        }
        return returnData;
    }

    List<ContentValues> topratedFakeData() {
        List<ContentValues> returnData = new ArrayList<>();
        ContentValues dataValues;

        for (int i = 0; i < MOVIES_FAKE_DATA_ROWS; i++) {
            dataValues = new ContentValues();
            dataValues.put(MoviesContract.TopRated.COLUMN_NAME_SORT_ID, MOVIES_FAKE_DATA_ROWS - i);
            dataValues.put(MoviesContract.TopRated.COLUMN_NAME_MOVIE_ID, 100 + i * 2);

            returnData.add(dataValues);
        }
        return returnData;
    }

}
