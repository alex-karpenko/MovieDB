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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
public class MovieListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        SwipeRefreshLayout.OnRefreshListener {
    private static final int FRAGMENT_LIST_LOADER_ID = 1;
    // cache update interval in milliseconds
    // 5 sec for debug
    private static final long CACHE_UPDATE_INTERVAL = 1000 * 60 * 5; // 5 minutes
    // Number of pages to preload
    private static final int CACHE_PRELOAD_PAGES = 5;
    private MoviesRecycleListAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public void onRefresh() {
        updateCurentPageCache();
    }

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
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.movies_list);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        // Create layout manager and attach it to recycle view
        mLayoutManager = new GridLayoutManager(getActivity(), Utils.calculateNoOfColumns(getActivity()));
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Construct empty cursor adapter ...
        mAdapter = new MoviesRecycleListAdapter(getActivity(), null);
        // ... and set it to recycle view
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // Every time when activity created - update configuration
        CacheUpdateService.startActionUpdateConfiguration(getActivity());
        // and create content loader
        getLoaderManager().initLoader(FRAGMENT_LIST_LOADER_ID, null, this);

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Every time, when fragment appears on the screen, we have to update contents
        // (after start activity, returning from details or settings, etc.)
        updateAllCacheIfNeed();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            mSwipeRefreshLayout.setRefreshing(true);
            updateCurentPageCache();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Method to update list of movies
     */
    private void updateAllCacheIfNeed() {
        long currentTime = Calendar.getInstance().getTimeInMillis();

        // Check difference of current time and last cache update time
        // and start update services if needed
        // 1. for popular
        if (currentTime - Utils.getLongCachePreference(getActivity(), R.string.last_popular_update_time) >= CACHE_UPDATE_INTERVAL) {
            updatePopularCache();
        }
        // 2. for top rated
        if (currentTime - Utils.getLongCachePreference(getActivity(), R.string.last_toprated_update_time) >= CACHE_UPDATE_INTERVAL) {
            updateTopratedCache();
        }
    }

    private void updatePopularCache() {
        CacheUpdateService.startActionUpdatePopular(getActivity(), -1);
        for (int i = 2; i <= CACHE_PRELOAD_PAGES; i++)
            CacheUpdateService.startActionUpdatePopular(getActivity(), i);
    }

    private void updateTopratedCache() {
        CacheUpdateService.startActionUpdateToprated(getActivity(), -1);
        for (int i = 2; i <= CACHE_PRELOAD_PAGES; i++)
            CacheUpdateService.startActionUpdateToprated(getActivity(), i);
    }

    private void updateCurentPageCache() {
        String sortOrder = getSortOrder();
        if (sortOrder.equals(getString(R.string.pref_sortorder_rating))) updateTopratedCache();
        else if (sortOrder.equals(getString(R.string.pref_sortorder_popular))) updatePopularCache();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri baseUri = MoviesContract.Popular.CONTENT_URI;
        String[] baseProjection = MoviesContract.Popular.shortListProjection;

        String sortOrder = getSortOrder();

        if (sortOrder.equals(getString(R.string.pref_sortorder_rating))) {
            baseProjection = MoviesContract.Toprated.shortListProjection;
            baseUri = MoviesContract.Toprated.CONTENT_URI;
        }

        return new CursorLoader(getActivity(), baseUri, baseProjection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.changeCursor(data);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

    private String getSortOrder() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOrder = prefs.getString(getString(R.string.pref_sortorder_key),
                getString(R.string.pref_sortorder_default));
        return sortOrder;
    }
}
