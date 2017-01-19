package com.example.leshik.moviedb;

import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.leshik.moviedb.model.MoviesContract;
import com.example.leshik.moviedb.service.CacheUpdateService;

import java.util.Calendar;


/**
 * Main screen fragment with list of movie's posters,
 * placed in a RecycleView.
 * <p>
 */
public class MovieListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        SwipeRefreshLayout.OnRefreshListener {
    // Loader ID for movie's list
    private static final int FRAGMENT_LIST_LOADER_ID = 1;
    // Fragment types, for PageAdapter
    public static final String ARG_FRAGMENT_TYPE = "FRAGMENT_TAB_TYPE";
    public static final int POPULAR_TAB_FRAGMENT = 0;
    public static final int TOPRATED_TAB_FRAGMENT = 1;
    public static final int FAVORITES_TAB_FRAGMENT = 2;
    // Current fragment type
    private int fragmentTabType = FAVORITES_TAB_FRAGMENT;
    private MoviesRecycleListAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public void onRefresh() {
        updateCurrentPageCache();
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
        void onItemSelected(Uri movieUri, ImageView posterView);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Get bundle with args (fragment type)
        Bundle args = getArguments();
        if (args != null) fragmentTabType = args.getInt(ARG_FRAGMENT_TYPE, FAVORITES_TAB_FRAGMENT);

        // Inflate fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.movies_list);

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
        // Create content loader for list data
        getLoaderManager().initLoader(FRAGMENT_LIST_LOADER_ID, null, this);

        super.onActivityCreated(savedInstanceState);

        // Every time, when fragment appears on the screen, we have to update contents
        // (after start activity, returning from details or settings, etc.)
        updateCurrentPageCacheIfNeed();
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
            updateCurrentPageCache();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updatePopularCache() {
        CacheUpdateService.startActionUpdatePopular(getActivity(), -1);
        for (int i = 2; i <= Utils.CACHE_PRELOAD_PAGES; i++)
            CacheUpdateService.startActionUpdatePopular(getActivity(), i);
    }

    private void updateTopratedCache() {
        CacheUpdateService.startActionUpdateToprated(getActivity(), -1);
        for (int i = 2; i <= Utils.CACHE_PRELOAD_PAGES; i++)
            CacheUpdateService.startActionUpdateToprated(getActivity(), i);
    }

    private void updateCurrentPageCache() {
        switch (fragmentTabType) {
            case POPULAR_TAB_FRAGMENT:
                updatePopularCache();
                break;
            case TOPRATED_TAB_FRAGMENT:
                updateTopratedCache();
                break;
            case FAVORITES_TAB_FRAGMENT:
                mSwipeRefreshLayout.setRefreshing(false);
                break;
        }
    }

    private void updateCurrentPageCacheIfNeed() {
        long currentTime = Calendar.getInstance().getTimeInMillis();

        switch (fragmentTabType) {
            case POPULAR_TAB_FRAGMENT:
                if (currentTime - Utils.getLongCachePreference(getActivity(), R.string.last_popular_update_time) >= Utils.getCacheUpdateInterval()) {
                    updatePopularCache();
                }
                break;
            case TOPRATED_TAB_FRAGMENT:
                if (currentTime - Utils.getLongCachePreference(getActivity(), R.string.last_toprated_update_time) >= Utils.getCacheUpdateInterval()) {
                    updateTopratedCache();
                }
                break;
            case FAVORITES_TAB_FRAGMENT:
                mSwipeRefreshLayout.setRefreshing(false);
                break;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Default URI and projection
        Uri baseUri = MoviesContract.Favorites.CONTENT_URI;
        String[] baseProjection = MoviesContract.Favorites.shortListProjection;

        // Set URI and projection in order to fragment type
        switch (fragmentTabType) {
            case POPULAR_TAB_FRAGMENT:
                baseUri = MoviesContract.Popular.CONTENT_URI;
                baseProjection = MoviesContract.Popular.shortListProjection;
                break;
            case TOPRATED_TAB_FRAGMENT:
                baseUri = MoviesContract.Toprated.CONTENT_URI;
                baseProjection = MoviesContract.Toprated.shortListProjection;
                break;
            case FAVORITES_TAB_FRAGMENT:
                baseUri = MoviesContract.Favorites.CONTENT_URI;
                baseProjection = MoviesContract.Favorites.shortListProjection;
                break;
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

}
