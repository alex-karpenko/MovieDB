package com.example.leshik.moviedb;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Utility class for provide some common (project-wide) methods, variables and constants
 */

public final class MovieUtils {
    private static final String LOG_TAG = MovieUtils.class.getSimpleName(); // for debugging purpose

    // Tag for put/get extra data via Intent with MovieInfo object inside
    public static final String EXTRA_MOVIE_INFO = "com.example.leshik.moviedb.EXTRA_MOVIE_INFO";
    // Base URLs to deal with TMBD API
    public static final String baseApiUrl = "http://api.themoviedb.org/3/";
    public static final String baseApiSecureUrl = "https://api.themoviedb.org/3/";

    // Common variables, we fill its by fetching configuration from TMDB (in MovieListFragment class)
    public static String basePosterUrl = null;
    public static String basePosterSecureUrl = null;
    public static String[] posterSizes = null;

    // Check network state of device. Do not forget to add permission...
    // <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        // next one string - for check connected&mayby_connected state
        // uncomment it if need, and comment next one
        //    return netInfo != null && netInfo.isConnectedOrConnecting();
        return netInfo != null && netInfo.isConnected();
    }

    /**
     * Method to fetch page form specified URL and return all data as string
     * TODO: more accurate error handling on network operations
     */
    public static String fetchUrl(Context context, Uri uri) {
        // If device not connected to any network - return immediately
        if (!isOnline(context)) {
            return null;
        }

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String resultJsonStr = null;

        try {

            URL url = new URL(uri.toString());

            // Create the request, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            // read until stream is not empty
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            // copy buffer into result string
            resultJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            // Close all opened resources
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        return resultJsonStr;
    }

}
