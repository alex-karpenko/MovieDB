package com.example.leshik.moviedb.model;

import android.content.ContentValues;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.List;

import static org.junit.Assert.assertEquals;
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

        ContentValues[] values = new ContentValues[moviesData.size()];
        int result = mContext.getContentResolver().bulkInsert(MoviesContract.Movies.CONTENT_URI, moviesData.toArray(values));
        assertEquals("Inserted wrong number of rows into movies table", values.length, result);

        values = new ContentValues[popularData.size()];
        result = mContext.getContentResolver().bulkInsert(MoviesContract.Popular.CONTENT_URI, popularData.toArray(values));
        assertEquals("Inserted wrong number of rows into popular table", values.length, result);

        values = new ContentValues[topratedData.size()];
        result = mContext.getContentResolver().bulkInsert(MoviesContract.TopRated.CONTENT_URI, topratedData.toArray(values));
        assertEquals("Inserted wrong number of rows into toprated table", values.length, result);

        values = new ContentValues[favoritesData.size()];
        result = mContext.getContentResolver().bulkInsert(MoviesContract.Favorites.CONTENT_URI, favoritesData.toArray(values));
        assertEquals("Inserted wrong number of rows into favorites table", values.length, result);
    }

    @Test
    public void test02_delete() {
        test01_bulkInsert();

        int result = mContext.getContentResolver().delete(MoviesContract.Favorites.CONTENT_URI, null, null);
        assertTrue("Deleted wrong number of rows from favorites table", result > 0);

        result = mContext.getContentResolver().delete(MoviesContract.TopRated.CONTENT_URI, null, null);
        assertTrue("Deleted wrong number of rows from toprated table", result > 0);

        result = mContext.getContentResolver().delete(MoviesContract.Popular.CONTENT_URI, null, null);
        assertTrue("Deleted wrong number of rows from popular table", result > 0);

        result = mContext.getContentResolver().delete(MoviesContract.Movies.CONTENT_URI, null, null);
        assertTrue("Deleted wrong number of rows from movies table", result > 0);
    }

    private void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(MoviesContract.Videos.CONTENT_URI, null, null);
        mContext.getContentResolver().delete(MoviesContract.Favorites.CONTENT_URI, null, null);
        mContext.getContentResolver().delete(MoviesContract.TopRated.CONTENT_URI, null, null);
        mContext.getContentResolver().delete(MoviesContract.Popular.CONTENT_URI, null, null);
        mContext.getContentResolver().delete(MoviesContract.Movies.CONTENT_URI, null, null);
    }

}
