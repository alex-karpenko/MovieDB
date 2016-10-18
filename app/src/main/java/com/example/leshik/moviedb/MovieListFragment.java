package com.example.leshik.moviedb;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by Leshik on 16.10.2016.
 */
public class MovieListFragment extends Fragment {
    private MoviesListAdapter mMoviesAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mMoviesAdapter = new MoviesListAdapter(getActivity(), new ArrayList<MovieInfo>());
        GridView gridView = (GridView) rootView.findViewById(R.id.movie_grid);
        gridView.setAdapter(mMoviesAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                MovieInfo movieInfo = mMoviesAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(MovieUtils.EXTRA_MOVIE_INFO, movieInfo);
                startActivity(intent);
            }

        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMoviesArray();
    }

    private void updateMoviesArray() {
        FetchMoviesTask fetchTask = new FetchMoviesTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOrder = prefs.getString(getString(R.string.pref_sortorder_key),
                getString(R.string.pref_sortorder_default));

        fetchTask.execute(sortOrder);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, MovieInfo[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName(); // for debugging purpose

        /**
         * Parse JSON string to fill configuration variables
         */
        public void parseConfigFromJson(String configJsonStr)
                throws JSONException {

            if (configJsonStr == null) return;

            // These are the names of the JSON objects that need to be extracted.
            final String TMDB_CONFIG_IMAGES = "images";
            final String TMDB_CONFIG_BASE_URL = "base_url";
            final String TMDB_CONFIG_SECURE_BASE_URL = "secure_base_url";
            final String TMDB_CONFIG_POSTER_SIZES = "poster_sizes";

            JSONObject rootConfigJson = new JSONObject(configJsonStr);
            JSONObject imagesConfigJson = rootConfigJson.getJSONObject(TMDB_CONFIG_IMAGES);
            MovieUtils.basePosterUrl = imagesConfigJson.getString(TMDB_CONFIG_BASE_URL);
            MovieUtils.basePosterSecureUrl = imagesConfigJson.getString(TMDB_CONFIG_SECURE_BASE_URL);
            JSONArray posterSizesArray = imagesConfigJson.getJSONArray(TMDB_CONFIG_POSTER_SIZES);

            MovieUtils.posterSizes = new String[posterSizesArray.length()];
            for (int i = 0; i < posterSizesArray.length(); i++)
                MovieUtils.posterSizes[i] = posterSizesArray.getString(i);
        }

        /**
         * Parse JSON string to make array of MovieInfo obejcts
         */
        public MovieInfo[] getMoviesListFromJson(String moviesJsonStr)
                throws JSONException {

            if (moviesJsonStr == null) return null;

            // These are the names of the JSON objects that need to be extracted.
            final String TMDB_RESULTS = "results";
            final String TMDB_POSTER_PATH = "poster_path";
            final String TMDB_OVERVIEW = "overview";
            final String TMDB_RELEASE_DATE = "release_date";
            final String TMDB_ID = "id";
            final String TMDB_ORIGINAL_TITLE = "original_title";
            final String TMDB_VOTE_AVERAGE = "vote_average";

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(TMDB_RESULTS);

            MovieInfo[] resultsArray = new MovieInfo[moviesArray.length()];

            for (int i = 0; i < moviesArray.length(); i++) {
                // Get the JSON object representing the one movie
                JSONObject movieObject = moviesArray.getJSONObject(i);

                long id = movieObject.getLong(TMDB_ID);
                String originalTitle = movieObject.getString(TMDB_ORIGINAL_TITLE);
                String overview = movieObject.getString(TMDB_OVERVIEW);
                String releaseDate = movieObject.getString(TMDB_RELEASE_DATE);
                double voteAverage = movieObject.getDouble(TMDB_VOTE_AVERAGE);
                String posterPath = movieObject.getString(TMDB_POSTER_PATH);
                resultsArray[i] = new MovieInfo(id, originalTitle, overview, releaseDate, voteAverage, posterPath);
            }
            return resultsArray;
        }


        @Override
        protected MovieInfo[] doInBackground(String... params) {

            // Only one parameter is sort criteria: ???
            if (params.length == 0) {
                return null;
            }

            // Construct the URL for the query
            final String CONFIG_SUFFIX = "configuration";
            final String POPULAR_SUFFIX = "movie/popular";
            final String TOP_RATED_SUFFIX = "movie/top_rated";
            final String LANGUAGE_PARAM = "language";
            final String PAGE_PARAM = "page";
            final String API_KEY_PARAM = "api_key";

            // Check config variables. If empty - retrieve config
            if (MovieUtils.basePosterUrl == null || MovieUtils.basePosterSecureUrl == null || MovieUtils.posterSizes == null) {
                // Create URI to fetch configuration
                Uri configUri = Uri.parse(MovieUtils.baseApiUrl).buildUpon()
                        .appendEncodedPath(CONFIG_SUFFIX)
                        .appendQueryParameter(API_KEY_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                        .build();
                // Fetch config JSON block into String and parse it
                String configJsonStr = MovieUtils.fetchUrl(getActivity(), configUri);
                try {
                    parseConfigFromJson(configJsonStr);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }

            }

            // Fetch list on films
            // Create URI to fetch configuration
            String fetchParam = POPULAR_SUFFIX;
            if (params[0].equals(getString(R.string.pref_sortorder_rating))) {
                fetchParam = TOP_RATED_SUFFIX;
            }

            Uri moviesUri = Uri.parse(MovieUtils.baseApiUrl).buildUpon()
                    .appendEncodedPath(fetchParam)
                    .appendQueryParameter(API_KEY_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                    .build();
            // Fetch JSON block with films into String and parse it
            String moviesJsonStr = MovieUtils.fetchUrl(getActivity(), moviesUri);

            try {
                return getMoviesListFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(MovieInfo[] result) {
            if (result != null) {
                mMoviesAdapter.clear();
                mMoviesAdapter.addAll(result);
            }
        }

    }
}
