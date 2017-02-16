package com.example.leshik.moviedb;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.leshik.moviedb.data.MoviesContract;
import com.example.leshik.moviedb.service.CacheUpdateService;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * Main screen fragment with list of movie's posters,
 * placed in a RecycleView.
 * There are three types of this fragment for popular, top rated and favorites lists
 * <p>
 */
public class MovieListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "MovieListFragment";
    // Loader ID for movie's list
    private static final int FRAGMENT_LIST_LOADER_ID = 1;
    // Fragment types, for PageAdapter
    public static final String ARG_FRAGMENT_TYPE = "FRAGMENT_TAB_TYPE";
    public static final int POPULAR_TAB_FRAGMENT = 0;
    public static final int TOPRATED_TAB_FRAGMENT = 1;
    public static final int FAVORITES_TAB_FRAGMENT = 2;
    // Current fragment type
    private int fragmentTabType = FAVORITES_TAB_FRAGMENT;

    // views in the fragment
    @BindView(R.id.swiperefresh)
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.movies_list)
    protected RecyclerView mRecyclerView;
    private Unbinder unbinder;

    private RecyclerView.LayoutManager mLayoutManager;

    private MoviesRecycleListAdapter mAdapter;

    // for control auto content loading
    private boolean loadingCache = false;
    // start auto loading after this threshold (multiplied by number of the items in list row)
    private int scrollingThreshold = 5;

    // swipe refresh callback
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
        unbinder = ButterKnife.bind(this, rootView);

        // store view's references
        mSwipeRefreshLayout.setOnRefreshListener(this);

        // Create layout manager and attach it to recycle view
        mLayoutManager = new GridLayoutManager(getActivity(), Utils.calculateNoOfColumns(getActivity()));
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Construct empty cursor adapter ...
        mAdapter = new MoviesRecycleListAdapter(getActivity(), null);
        // ... and set it to recycle view
        mRecyclerView.setAdapter(mAdapter);

        // Set up scroll listener for auto load list's tail
        if (fragmentTabType == POPULAR_TAB_FRAGMENT || fragmentTabType == TOPRATED_TAB_FRAGMENT) {
            // set scrolling threshold
            scrollingThreshold = scrollingThreshold * Utils.calculateNoOfColumns(getActivity());
            // set scroll listener
            mRecyclerView.addOnScrollListener(new AutoLoadingScrollListener());
        }

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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
            // set refresh mart to on
            mSwipeRefreshLayout.setRefreshing(true);
            // and update current page
            updateCurrentPageCache();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // clear popular cache and preload 1-st page and configures count of pages
    private void updatePopularCache() {
        CacheUpdateService.startActionUpdatePopular(getActivity(), -1);
        for (int i = 2; i <= Utils.getCachePreloadPages(); i++)
            CacheUpdateService.startActionUpdatePopular(getActivity(), i);
    }

    // clear toprated cache and preload 1-st page and configures count of pages
    private void updateTopratedCache() {
        CacheUpdateService.startActionUpdateToprated(getActivity(), -1);
        for (int i = 2; i <= Utils.getCachePreloadPages(); i++)
            CacheUpdateService.startActionUpdateToprated(getActivity(), i);
    }

    // update cache for current page type
    private void updateCurrentPageCache() {
        switch (fragmentTabType) {
            case POPULAR_TAB_FRAGMENT:
                updatePopularCache();
                break;
            case TOPRATED_TAB_FRAGMENT:
                updateTopratedCache();
                break;
            case FAVORITES_TAB_FRAGMENT:
                // no actions, but set off refresh mark
                mSwipeRefreshLayout.setRefreshing(false);
                break;
        }
    }

    // check if cache is expired and start updating if need
    private void updateCurrentPageCacheIfNeed() {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        long lastUpdateTime;

        switch (fragmentTabType) {
            case POPULAR_TAB_FRAGMENT:
                lastUpdateTime = Utils.getLongCachePreference(getActivity(), R.string.last_popular_update_time);
                if (Utils.getCacheUpdateInterval() > 0 || lastUpdateTime <= 0) {
                    if (currentTime - lastUpdateTime >= Utils.getCacheUpdateInterval()) {
                        updatePopularCache();
                    }
                }
                break;
            case TOPRATED_TAB_FRAGMENT:
                lastUpdateTime = Utils.getLongCachePreference(getActivity(), R.string.last_toprated_update_time);
                if (Utils.getCacheUpdateInterval() > 0 || lastUpdateTime <= 0) {
                    if (currentTime - lastUpdateTime >= Utils.getCacheUpdateInterval()) {
                        updateTopratedCache();
                    }
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
        loadingCache = false;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
        loadingCache = false;
    }

    // listener for check every scroll event on list view and start loading cache content
    // when scrolled to the end of cached data
    private class AutoLoadingScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (loadingCache) return; // immediate return if we in loading state

            int totalItems = mLayoutManager.getItemCount(); // total items in the view (in the cursor)
            int lastVisibleItem = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition(); // last visible item
            // if last view under threshold position - start loading cache
            if (lastVisibleItem + scrollingThreshold >= totalItems) {
                long maxPages;
                int currentPage = totalItems / Utils.getCachePageSize();

                switch (fragmentTabType) {
                    case POPULAR_TAB_FRAGMENT:
                        maxPages = Utils.getLongCachePreference(getActivity(), R.string.total_popular_pages);
                        if (currentPage <= maxPages) {
                            CacheUpdateService.startActionUpdatePopular(getActivity(), currentPage + 1);
                            loadingCache = true;
                        }
                        break;
                    case TOPRATED_TAB_FRAGMENT:
                        maxPages = Utils.getLongCachePreference(getActivity(), R.string.total_toprated_pages);
                        if (currentPage <= maxPages) {
                            CacheUpdateService.startActionUpdateToprated(getActivity(), currentPage + 1);
                            loadingCache = true;
                        }
                        break;
                }

            }
        }
    }
}
