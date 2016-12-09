package com.example.leshik.moviedb.model;

import android.content.UriMatcher;
import android.net.Uri;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;


/**
 * Created by Leshik on 09.12.2016.
 */

@RunWith(AndroidJUnit4.class)
public class TestUriMatcher {
    public static final String LOG_TAG = TestUriMatcher.class.getSimpleName();

    public static final Uri TEST_URI_MOVIES_DIR = MoviesContract.Movies.CONTENT_URI;
    public static final Uri TEST_URI_MOVIES_ITEM = Uri.withAppendedPath(MoviesContract.Movies.CONTENT_URI, "123");

    public static final Uri TEST_URI_POPULAR_DIR = MoviesContract.Popular.CONTENT_URI;
    public static final Uri TEST_URI_POPULAR_ITEM = Uri.withAppendedPath(MoviesContract.Popular.CONTENT_URI, "123");

    public static final Uri TEST_URI_TOPRATED_DIR = MoviesContract.TopRated.CONTENT_URI;
    public static final Uri TEST_URI_TOPRATED_ITEM = Uri.withAppendedPath(MoviesContract.TopRated.CONTENT_URI, "123");

    public static final Uri TEST_URI_FAVORITES_DIR = MoviesContract.Favorites.CONTENT_URI;
    public static final Uri TEST_URI_FAVORITES_ITEM = Uri.withAppendedPath(MoviesContract.Favorites.CONTENT_URI, "123");

    public static final Uri TEST_URI_VIDEOS_DIR = MoviesContract.Videos.CONTENT_URI;
    public static final Uri TEST_URI_VIDEOS_VIDEO_ITEM = Uri.withAppendedPath(MoviesContract.Videos.CONTENT_URI, "qwqwqwiuwqyeiuqyweui");
    public static final Uri TEST_URI_VIDEOS_MOVIE_ITEM = Uri.withAppendedPath(MoviesContract.Videos.CONTENT_URI, "123");

    @Test
    public void testUriMatcher() throws Throwable {
        UriMatcher testMatcher = MoviesProvider.buildUriMatcher();

        assertEquals("Error: the MOVIES URI was matcher incorrectly.",
                MoviesProvider.MOVIES, testMatcher.match(TEST_URI_MOVIES_DIR));
        assertEquals("Error: the MOVIES WITH ID URI was matcher incorrectly.",
                MoviesProvider.MOVIES_WITH_MOVIE_ID, testMatcher.match(TEST_URI_MOVIES_ITEM));

        assertEquals("Error: the POPULAR URI was matcher incorrectly.",
                MoviesProvider.POPULAR, testMatcher.match(TEST_URI_POPULAR_DIR));
        assertEquals("Error: the POPULAR WITH ID URI was matcher incorrectly.",
                MoviesProvider.POPULAR_WITH_SORT_ID, testMatcher.match(TEST_URI_POPULAR_ITEM));

        assertEquals("Error: the TOPRATED URI was matcher incorrectly.",
                MoviesProvider.TOPRATED, testMatcher.match(TEST_URI_TOPRATED_DIR));
        assertEquals("Error: the TOPRATED WITH ID URI was matcher incorrectly.",
                MoviesProvider.TOPRATED_WITH_SORT_ID, testMatcher.match(TEST_URI_TOPRATED_ITEM));

        assertEquals("Error: the FAVORITES URI was matcher incorrectly.",
                MoviesProvider.FAVORITES, testMatcher.match(TEST_URI_FAVORITES_DIR));
        assertEquals("Error: the FAVORITES WITH ID URI was matcher incorrectly.",
                MoviesProvider.FAVORITES_WITH_SORT_ID, testMatcher.match(TEST_URI_FAVORITES_ITEM));

        assertEquals("Error: the VIDEOS URI was matcher incorrectly.",
                MoviesProvider.VIDEOS, testMatcher.match(TEST_URI_VIDEOS_DIR));
        assertEquals("Error: the VIDEOS WITH VIDEO ID URI was matcher incorrectly.",
                MoviesProvider.VIDEOS_WITH_VIDEO_ID, testMatcher.match(TEST_URI_VIDEOS_VIDEO_ITEM));
        assertEquals("Error: the VIDEOS WITH MOVIE ID URI was matcher incorrectly.",
                MoviesProvider.VIDEOS_WITH_MOVIE_ID, testMatcher.match(TEST_URI_VIDEOS_MOVIE_ITEM));
    }
}
