package com.example.leshik.moviedb;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.leshik.moviedb.model.MoviesContract;
import com.example.leshik.moviedb.service.CacheUpdateService;

import java.util.Calendar;


/**
 * Main screen fragment with list of movie's posters,
 * placed in a GridView.
 * We get information about movies from TMBD as a JSON object,
 * parse it and place into list of MovieInfo objects.
 * And construct adapter to handle list.
 * <p>
 * TODO: more accurate error handling on network operations
 */
public class MovieListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int FRAGMENT_LIST_LOADER_ID = 1;
    // cache update interval in milliseconds
    // 5 sec for debug
    private static final long CACHE_UPDATE_INTERVAL = 1000 * 60 * 5; // 5 minutes
    // Number of pages to preload
    private static final int CACHE_PRELOAD_PAGES = 5;
    private MoviesListAdapter mCursorAdapter;
    private int mPosition = GridView.INVALID_POSITION;
    private GridView mGridView;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        void onItemSelected(Uri movieUri);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Construct empty cursor adapter ...
        mCursorAdapter = new MoviesListAdapter(getActivity(), null, 0);
        // ... and set it to gridview
        mGridView = (GridView) rootView.findViewById(R.id.movie_grid);
        mGridView.setAdapter(mCursorAdapter);

        // Listener for handling clicks on poster image
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // TODO: call detail activity with cursor data
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    ((Callback) getActivity()).
                            onItemSelected(MoviesContract.Movies.buildUri(cursor.getLong(MoviesContract.SHORT_LIST_PROJECTION_INDEX_MOVIE_ID)));
                }
                mPosition = position;
            }

        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        CacheUpdateService.startActionUpdateConfiguration(getActivity());
        getLoaderManager().initLoader(FRAGMENT_LIST_LOADER_ID, null, this);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Every time, when fragment appears on the screen, we have to update contents
        // (after start activity, returning from details or settings, etc.
        updateMoviesCache();
    }

    /**
     * Method to update list of movies
     */
    private void updateMoviesCache() {
        long currentTime = Calendar.getInstance().getTimeInMillis();

        if (currentTime - Utils.getLongCachePreference(getActivity(), R.string.last_popular_update_time) >= CACHE_UPDATE_INTERVAL) {
            CacheUpdateService.startActionUpdatePopular(getActivity(), -1);
            for (int i = 2; i <= CACHE_PRELOAD_PAGES; i++)
                CacheUpdateService.startActionUpdatePopular(getActivity(), i);
        }

        if (currentTime - Utils.getLongCachePreference(getActivity(), R.string.last_toprated_update_time) >= CACHE_UPDATE_INTERVAL) {
            CacheUpdateService.startActionUpdateToprated(getActivity(), -1);
            for (int i = 2; i <= CACHE_PRELOAD_PAGES; i++)
                CacheUpdateService.startActionUpdateToprated(getActivity(), i);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri baseUri = MoviesContract.Popular.CONTENT_URI;
        String[] baseProjection = MoviesContract.Popular.shortListProjection;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOrder = prefs.getString(getString(R.string.pref_sortorder_key),
                getString(R.string.pref_sortorder_default));

        if (sortOrder.equals(getString(R.string.pref_sortorder_rating))) {
            baseProjection = MoviesContract.Toprated.shortListProjection;
            baseUri = MoviesContract.Toprated.CONTENT_URI;
        }

        return new CursorLoader(getActivity(), baseUri, baseProjection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
        if (mPosition != GridView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mGridView.smoothScrollToPosition(mPosition);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
