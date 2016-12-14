package com.example.leshik.moviedb.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Leshik on 12.12.2016.
 */

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestProvider {
    public static final String LOG_TAG = TestProvider.class.getSimpleName();
    private static final Context mContext = InstrumentationRegistry.getTargetContext();

    @Before
    public void setUp() {
        deleteAllRecordsFromProvider();
    }

    @Test
    public void test01_bulkInsert() {
        List<ContentValues> moviesData = TestDb.moviesFakeData();
        List<ContentValues> topratedData = TestDb.topratedFakeData();
        List<ContentValues> popularData = TestDb.popularFakeData();
        List<ContentValues> favoritesData = TestDb.favoritesFakeData();
        List<ContentValues> videosData = TestDb.videosFakeData();

        ContentValues[] values = new ContentValues[moviesData.size()];
        int result = mContext.getContentResolver().bulkInsert(MoviesContract.Movies.CONTENT_URI, moviesData.toArray(values));
        assertEquals("Inserted wrong number of rows into movies table", values.length, result);
        // double try, for testing dublicate checking mechanism
        result = mContext.getContentResolver().bulkInsert(MoviesContract.Movies.CONTENT_URI, moviesData.toArray(values));
        assertEquals("Inserted wrong number of rows into movies table", values.length, result);

        values = new ContentValues[popularData.size()];
        result = mContext.getContentResolver().bulkInsert(MoviesContract.Popular.CONTENT_URI, popularData.toArray(values));
        assertEquals("Inserted wrong number of rows into popular table", values.length, result);

        values = new ContentValues[topratedData.size()];
        result = mContext.getContentResolver().bulkInsert(MoviesContract.Toprated.CONTENT_URI, topratedData.toArray(values));
        assertEquals("Inserted wrong number of rows into toprated table", values.length, result);

        values = new ContentValues[favoritesData.size()];
        result = mContext.getContentResolver().bulkInsert(MoviesContract.Favorites.CONTENT_URI, favoritesData.toArray(values));
        assertEquals("Inserted wrong number of rows into favorites table", values.length, result);

        values = new ContentValues[videosData.size()];
        result = mContext.getContentResolver().bulkInsert(MoviesContract.Videos.CONTENT_URI, videosData.toArray(values));
        assertEquals("Inserted wrong number of rows into videos table", values.length, result);
    }

    @Test
    public void test02_insert() {
        List<ContentValues> moviesData = TestDb.moviesFakeData();
        List<ContentValues> topratedData = TestDb.topratedFakeData();
        List<ContentValues> popularData = TestDb.popularFakeData();
        List<ContentValues> favoritesData = TestDb.favoritesFakeData();
        List<ContentValues> videosData = TestDb.videosFakeData();

        Uri resultMovies = mContext.getContentResolver().insert(MoviesContract.Movies.CONTENT_URI, moviesData.get(0));
        assertEquals("Inserted wrong number of rows into movies table",
                MoviesContract.Movies.buildUri(moviesData.get(0).getAsInteger(MoviesContract.Movies.COLUMN_NAME_MOVIE_ID)),
                resultMovies);
        // bulk insert for testing rest of the tables (foreign keys must by present)
        ContentValues[] values = new ContentValues[moviesData.size()];
        int rows = mContext.getContentResolver().bulkInsert(MoviesContract.Movies.CONTENT_URI, moviesData.toArray(values));
        assertEquals("Inserted wrong number of rows into movies table", values.length, rows);

        Uri resultPopular = mContext.getContentResolver().insert(MoviesContract.Popular.CONTENT_URI, popularData.get(1));
        assertEquals("Inserted wrong number of rows into popular table",
                MoviesContract.Popular.buildUri(popularData.get(1).getAsInteger(MoviesContract.Popular.COLUMN_NAME_SORT_ID)),
                resultPopular);

        Uri resultToprated = mContext.getContentResolver().insert(MoviesContract.Toprated.CONTENT_URI, topratedData.get(2));
        assertEquals("Inserted wrong number of rows into toprated table",
                MoviesContract.Toprated.buildUri(topratedData.get(2).getAsInteger(MoviesContract.Toprated.COLUMN_NAME_SORT_ID)),
                resultToprated);

        Uri resultFavorites = mContext.getContentResolver().insert(MoviesContract.Favorites.CONTENT_URI, favoritesData.get(3));
        assertEquals("Inserted wrong number of rows into favorites table",
                MoviesContract.Favorites.buildUri(favoritesData.get(3).getAsInteger(MoviesContract.Favorites.COLUMN_NAME_SORT_ID)),
                resultFavorites);

        Uri resultVideos = mContext.getContentResolver().insert(MoviesContract.Videos.CONTENT_URI, videosData.get(4));
        assertEquals("Inserted wrong number of rows into videos table",
                MoviesContract.Videos.buildUri(videosData.get(4).getAsString(MoviesContract.Videos.COLUMN_NAME_VIDEO_ID)),
                resultVideos);

        Cursor c = mContext.getContentResolver().query(resultPopular, null, null, null, null);
        assertNotNull("Cursor is null - no rows or error while selecting popular table.", c);
        assertTrue("Cannot moveToNext() - no rows or error while selecting popular table.", c.moveToNext());
        assertEquals("Rows count is not 1 while selecting popular table.", 1, c.getCount());
        assertEquals("Wrong data in the popular table.",
                (long) popularData.get(1).getAsInteger(MoviesContract.Popular.COLUMN_NAME_SORT_ID),
                c.getInt(c.getColumnIndex(MoviesContract.Popular.COLUMN_NAME_SORT_ID)));
        c.close();

        c = mContext.getContentResolver().query(resultToprated, null, null, null, null);
        assertNotNull("Cursor is null - no rows or error while selecting toprated table.", c);
        assertTrue("Cannot moveToNext() - no rows or error while selecting toprated table.", c.moveToNext());
        assertEquals("Rows count is not 1 while selecting toprated table.", 1, c.getCount());
        assertEquals("Wrong data in the toprated table.",
                (long) topratedData.get(2).getAsInteger(MoviesContract.Toprated.COLUMN_NAME_SORT_ID),
                c.getInt(c.getColumnIndex(MoviesContract.Toprated.COLUMN_NAME_SORT_ID)));
        c.close();

        c = mContext.getContentResolver().query(resultFavorites, null, null, null, null);
        assertNotNull("Cursor is null - no rows or error while selecting favorites table.", c);
        assertTrue("Cannot moveToNext() - no rows or error while selecting favorites table.", c.moveToNext());
        assertEquals("Rows count is not 1 while selecting favorites table.", 1, c.getCount());
        assertEquals("Wrong data in the favorites table.",
                (long) favoritesData.get(3).getAsInteger(MoviesContract.Favorites.COLUMN_NAME_SORT_ID),
                c.getInt(c.getColumnIndex(MoviesContract.Favorites.COLUMN_NAME_SORT_ID)));
        c.close();

        c = mContext.getContentResolver().query(resultVideos, null, null, null, null);
        assertNotNull("Cursor is null - no rows or error while selecting videos table.", c);
        assertTrue("Cannot moveToNext() - no rows or error while selecting videos table.", c.moveToNext());
        assertEquals("Rows count is not 1 while selecting videos table.", 1, c.getCount());
        assertEquals("Wrong data in the videos table.",
                videosData.get(4).getAsString(MoviesContract.Videos.COLUMN_NAME_VIDEO_ID),
                c.getString(c.getColumnIndex(MoviesContract.Videos.COLUMN_NAME_VIDEO_ID)));
        c.close();
    }


    @Test
    public void test03_delete() {
        test01_bulkInsert();

        // Get one record from videos table ...
        Cursor c = mContext.getContentResolver().query(MoviesContract.Videos.CONTENT_URI,
                new String[]{MoviesContract.Videos.COLUMN_NAME_VIDEO_ID, MoviesContract.Videos.COLUMN_NAME_MOVIE_ID},
                null, null, null);
        assertNotNull("Cursor is null after query videos table.", c);
        assertTrue("No next record in the cursor after query favorites table.", c.moveToNext());
        String video_id = c.getString(c.getColumnIndex(MoviesContract.Videos.COLUMN_NAME_VIDEO_ID));
        assertTrue("No next record in the cursor after query favorites table.", c.moveToNext());
        int movie_id = c.getInt(c.getColumnIndex(MoviesContract.Videos.COLUMN_NAME_MOVIE_ID));
        c.close();
        // ... and delete it ...
        assertEquals("Can not delete one record from videos table.",
                mContext.getContentResolver().delete(MoviesContract.Videos.buildUri(video_id), null, null), 1);
        assertEquals("Can not delete one record from videos table.",
                mContext.getContentResolver().delete(MoviesContract.Videos.buildUri(movie_id), null, null), 1);
        // ... and delete all the rest.
        int result = mContext.getContentResolver().delete(MoviesContract.Videos.CONTENT_URI, null, null);
        assertTrue("Deleted wrong number of rows from videos table", result == TestDb.MOVIES_FAKE_DATA_ROWS / 4 - 2);

        // Get one record from favorites table ...
        c = mContext.getContentResolver().query(MoviesContract.Favorites.CONTENT_URI,
                new String[]{MoviesContract.Favorites.COLUMN_NAME_SORT_ID},
                null, null, null);
        assertNotNull("Cursor is null after query favorites table.", c);
        assertTrue("No next record in the cursor after query favorites table.", c.moveToNext());
        int sort_id = c.getInt(c.getColumnIndex(MoviesContract.Favorites.COLUMN_NAME_SORT_ID));
        c.close();
        // ... and delete it ...
        assertEquals("Can not delete one record from favorites table.",
                mContext.getContentResolver().delete(MoviesContract.Favorites.buildUri(sort_id), null, null), 1);
        // ... and delete all the rest.
        result = mContext.getContentResolver().delete(MoviesContract.Favorites.CONTENT_URI, null, null);
        assertTrue("Deleted wrong number of rows from favorites table", result == TestDb.MOVIES_FAKE_DATA_ROWS / 2 - 1);

        // Get one record from toprated table ...
        c = mContext.getContentResolver().query(MoviesContract.Toprated.CONTENT_URI,
                new String[]{MoviesContract.Toprated.COLUMN_NAME_SORT_ID},
                null, null, null);
        assertNotNull("Cursor is null after query toprated table.", c);
        assertTrue("No next record in the cursor after query toprated table.", c.moveToNext());
        sort_id = c.getInt(c.getColumnIndex(MoviesContract.Toprated.COLUMN_NAME_SORT_ID));
        c.close();
        // ... and delete it ...
        assertEquals("Can not delete one record from toprated table.",
                mContext.getContentResolver().delete(MoviesContract.Toprated.buildUri(sort_id), null, null), 1);
        // ... and delete all the rest.
        result = mContext.getContentResolver().delete(MoviesContract.Toprated.CONTENT_URI, null, null);
        assertTrue("Deleted wrong number of rows from toprated table", result == TestDb.MOVIES_FAKE_DATA_ROWS - 1);

        // Get one record from popular table ...
        c = mContext.getContentResolver().query(MoviesContract.Popular.CONTENT_URI,
                new String[]{MoviesContract.Popular.COLUMN_NAME_SORT_ID},
                null, null, null);
        assertNotNull("Cursor is null after query popular table.", c);
        assertTrue("No next record in the cursor after query popular table.", c.moveToNext());
        sort_id = c.getInt(c.getColumnIndex(MoviesContract.Popular.COLUMN_NAME_SORT_ID));
        c.close();
        // ... and delete it ...
        assertEquals("Can not delete one record from popular table.",
                mContext.getContentResolver().delete(MoviesContract.Popular.buildUri(sort_id), null, null), 1);
        // ... and delete all the rest.
        result = mContext.getContentResolver().delete(MoviesContract.Popular.CONTENT_URI, null, null);
        assertTrue("Deleted wrong number of rows from popular table", result == TestDb.MOVIES_FAKE_DATA_ROWS - 1);

        // Get one record from movies table ...
        c = mContext.getContentResolver().query(MoviesContract.Movies.CONTENT_URI,
                new String[]{MoviesContract.Movies.COLUMN_NAME_MOVIE_ID},
                null, null, null);
        assertNotNull("Cursor is null after query movies table.", c);
        assertTrue("No next record in the cursor after query movies table.", c.moveToNext());
        movie_id = c.getInt(c.getColumnIndex(MoviesContract.Movies.COLUMN_NAME_MOVIE_ID));
        c.close();
        // ... and delete it ...
        assertEquals("Can not delete one record from movies table.",
                mContext.getContentResolver().delete(MoviesContract.Movies.buildUri(movie_id), null, null), 1);
        // ... and delete all the rest.
        result = mContext.getContentResolver().delete(MoviesContract.Movies.CONTENT_URI, null, null);
        assertTrue("Deleted wrong number of rows from movies table", result == TestDb.MOVIES_FAKE_DATA_ROWS - 1);
    }

    @Test
    public void test04_query() {
        test01_bulkInsert();

        Cursor c = mContext.getContentResolver().query(MoviesContract.Movies.CONTENT_URI, null, null, null, null);
        assertNotNull("Cursor is null after query movies table.", c);
        assertTrue("No next record in the cursor after query movies table.", c.moveToNext());
        assertEquals("Invalid rows count in the movies table", TestDb.MOVIES_FAKE_DATA_ROWS, c.getCount());
        c.close();

        c = mContext.getContentResolver().query(MoviesContract.Popular.CONTENT_URI, null, null, null, null);
        assertNotNull("Cursor is null after query popular table.", c);
        assertTrue("No next record in the cursor after query popular table.", c.moveToNext());
        assertEquals("Invalid rows count in the popular table", TestDb.MOVIES_FAKE_DATA_ROWS, c.getCount());
        c.close();

        c = mContext.getContentResolver().query(MoviesContract.Toprated.CONTENT_URI, null, null, null, null);
        assertNotNull("Cursor is null after query toprated table.", c);
        assertTrue("No next record in the cursor after query toprated table.", c.moveToNext());
        assertEquals("Invalid rows count in the toprated table", TestDb.MOVIES_FAKE_DATA_ROWS, c.getCount());
        c.close();

        c = mContext.getContentResolver().query(MoviesContract.Favorites.CONTENT_URI, null, null, null, null);
        assertNotNull("Cursor is null after query favorites table.", c);
        assertTrue("No next record in the cursor after query favorites table.", c.moveToNext());
        assertEquals("Invalid rows count in the favorites table", TestDb.MOVIES_FAKE_DATA_ROWS / 2, c.getCount());
        c.close();

        c = mContext.getContentResolver().query(MoviesContract.Videos.CONTENT_URI, null, null, null, null);
        assertNotNull("Cursor is null after query videos table.", c);
        assertTrue("No next record in the cursor after query videos table.", c.moveToNext());
        assertEquals("Invalid rows count in the videos table", TestDb.MOVIES_FAKE_DATA_ROWS / 4, c.getCount());
        c.close();
    }

    private void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(MoviesContract.Videos.CONTENT_URI, null, null);
        mContext.getContentResolver().delete(MoviesContract.Favorites.CONTENT_URI, null, null);
        mContext.getContentResolver().delete(MoviesContract.Toprated.CONTENT_URI, null, null);
        mContext.getContentResolver().delete(MoviesContract.Popular.CONTENT_URI, null, null);
        mContext.getContentResolver().delete(MoviesContract.Movies.CONTENT_URI, null, null);
    }

}
