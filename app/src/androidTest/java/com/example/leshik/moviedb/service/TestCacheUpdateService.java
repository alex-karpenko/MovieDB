package com.example.leshik.moviedb.service;

import android.content.Context;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.leshik.moviedb.model.MoviesContract;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


/**
 * Created by Leshik on 09.12.2016.
 */

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestCacheUpdateService {
    public static final String LOG_TAG = TestCacheUpdateService.class.getSimpleName();

    private static final Context mContext = InstrumentationRegistry.getTargetContext();

    @BeforeClass
    public void setUp() {
        deleteAllRecordsFromProvider();
    }

    @Test
    public void test01_UpdateConfiguration() throws Throwable {
        CacheUpdateService.startActionUpdateConfiguration(mContext);
        TimeUnit.SECONDS.sleep(5);
    }

    @Test
    public void test02_UpdatePopular_test_with_clear_all_data() throws Throwable {
        CacheUpdateService.startActionUpdatePopular(mContext, -1);
        CacheUpdateService.startActionUpdatePopular(mContext, 2);
        CacheUpdateService.startActionUpdatePopular(mContext, 3);
        TimeUnit.SECONDS.sleep(10);

        Cursor c = mContext.getContentResolver().query(MoviesContract.Popular.CONTENT_URI, null, null, null, null);
        assertNotNull(c);
        assertTrue(c.moveToNext());
        assertEquals(60, c.getCount());
    }

    @Test
    public void test03_UpdateToprated_test_with_clear_all_data() throws Throwable {
        CacheUpdateService.startActionUpdateToprated(mContext, -1);
        CacheUpdateService.startActionUpdateToprated(mContext, 2);
        CacheUpdateService.startActionUpdateToprated(mContext, 3);
        TimeUnit.SECONDS.sleep(10);

        Cursor c = mContext.getContentResolver().query(MoviesContract.Toprated.CONTENT_URI, null, null, null, null);
        assertNotNull(c);
        assertTrue(c.moveToNext());
        assertEquals(60, c.getCount());
    }

    private void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(MoviesContract.Videos.CONTENT_URI, null, null);
        mContext.getContentResolver().delete(MoviesContract.Favorites.CONTENT_URI, null, null);
        mContext.getContentResolver().delete(MoviesContract.Toprated.CONTENT_URI, null, null);
        mContext.getContentResolver().delete(MoviesContract.Popular.CONTENT_URI, null, null);
        mContext.getContentResolver().delete(MoviesContract.Movies.CONTENT_URI, null, null);
    }

}
