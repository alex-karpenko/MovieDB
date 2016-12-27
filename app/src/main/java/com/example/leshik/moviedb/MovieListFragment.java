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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.leshik.moviedb.model.MoviesContract;
import com.example.leshik.moviedb.service.CacheUpdateService;

import java.util.Calendar;


/**
 * Main screen fragment with list of movie's posters,
 * placed in a GridView.
 * <p>
 */
public class MovieListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int FRAGMENT_LIST_LOADER_ID = 1;
    // cache update interval in milliseconds
    // 5 sec for debug
    private static final long CACHE_UPDATE_INTERVAL = 1000 * 60 * 5; // 5 minutes
    // Number of pages to preload
    private static final int CACHE_PRELOAD_PAGES = 5;
    private MoviesRecycleListAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

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
        View rootView = inflater.inflate(R.layout.fragment_main_recycleview, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.movies_list);

        // Create layout manager and attach it to recycle view
        mLayoutManager = new GridLayoutManager(getActivity(),
                2,
                GridLayoutManager.VERTICAL,
                false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Construct empty cursor adapter ...
        mAdapter = new MoviesRecycleListAdapter(getActivity(), null);
        // ... and set it to recycle view
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Every time when activity created - update configuration
        CacheUpdateService.startActionUpdateConfiguration(getActivity());
        // and create content loader
        getLoaderManager().initLoader(FRAGMENT_LIST_LOADER_ID, null, this);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Every time, when fragment appears on the screen, we have to update contents
        // (after start activity, returning from details or settings, etc.)
        updateMoviesCache();
    }

    /**
     * Method to update list of movies
     */
    private void updateMoviesCache() {
        long currentTime = Calendar.getInstance().getTimeInMillis();

        // Check difference of current time and last cache update time
        // and start update services if needed
        // 1. for popular
        if (currentTime - Utils.getLongCachePreference(getActivity(), R.string.last_popular_update_time) >= CACHE_UPDATE_INTERVAL) {
            CacheUpdateService.startActionUpdatePopular(getActivity(), -1);
            for (int i = 2; i <= CACHE_PRELOAD_PAGES; i++)
                CacheUpdateService.startActionUpdatePopular(getActivity(), i);
        }
        // 2. for top rated
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
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
