package com.example.leshik.moviedb.ui.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import com.example.leshik.moviedb.R;
import com.example.leshik.moviedb.data.MovieListRepository;
import com.example.leshik.moviedb.data.MovieListType;
import com.example.leshik.moviedb.data.model.Movie;
import com.example.leshik.moviedb.ui.viewmodels.MovieListViewModel;
import com.example.leshik.moviedb.utils.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


/**
 * Main screen fragment with list of movie's posters,
 * placed in a RecycleView.
 * There are three types of this fragment for popular, top rated and favorites lists
 * <p>
 */
public class MovieListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "MovieListFragment";
    // Fragment types, for PageAdapter
    public static final String ARG_FRAGMENT_TYPE = "FRAGMENT_TYPE";

    // Current fragment type
    private MovieListType fragmentType;
    private static final MovieListType DEFAULT_FRAGMENT_TYPE = MovieListType.Favorite;

    // views in the fragment
    @BindView(R.id.swiperefresh)
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.movies_list)
    protected RecyclerView mRecyclerView;
    private Unbinder unbinder;

    private RecyclerView.LayoutManager mLayoutManager;

    private MovieListAdapter mAdapter;

    // for control auto content loading
    private boolean loadingCache = false;
    // start auto loading after this threshold (multiplied by number of the items in list row)
    private int scrollingThreshold = 5;

    private MovieListViewModel viewModel;
    Disposable subscription;

    // TODO: 3/7/17 Change arguments to MovieListType enum
    public static MovieListFragment newInstance(int listType) {
        MovieListFragment fragment = new MovieListFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_FRAGMENT_TYPE, listType);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentType = getFragmentType();
        viewModel = new MovieListViewModel(fragmentType,
                new MovieListRepository(getActivity().getApplicationContext()));

        // Inflate fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        // store view's references
        mSwipeRefreshLayout.setOnRefreshListener(this);

        // Create layout manager and attach it to recycle view
        mLayoutManager = new GridLayoutManager(getActivity(), Utils.calculateNoOfColumns(getActivity()));
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Construct empty adapter ...
        mAdapter = new MovieListAdapter(getActivity(), null);
        // ... and set it to recycle view
        mRecyclerView.setAdapter(mAdapter);

        // Set up scroll listener for auto load list's tail
        // set scrolling threshold
        scrollingThreshold = scrollingThreshold * Utils.calculateNoOfColumns(getActivity());
        // set scroll listener
        mRecyclerView.addOnScrollListener(new AutoLoadingScrollListener());

        subscribeToMovieList();

        return rootView;
    }

    private MovieListType getFragmentType() {
        Bundle args = getArguments();
        if (args != null) {
            int typeFromIntent = args.getInt(ARG_FRAGMENT_TYPE);
            if (typeFromIntent == MovieListType.Popular.getIndex()) return MovieListType.Popular;
            else if (typeFromIntent == MovieListType.Toprated.getIndex())
                return MovieListType.Toprated;
            else if (typeFromIntent == MovieListType.Favorite.getIndex())
                return MovieListType.Favorite;
            else throw new IllegalArgumentException("Incorrect fragment type argument");
        } else return DEFAULT_FRAGMENT_TYPE;
    }

    @Override
    public void onDestroyView() {
        unsubscribeFromMovieList();
        super.onDestroyView();
        unbinder.unbind();
    }

    private void subscribeToMovieList() {
        subscription = viewModel.getMovieList()
                .subscribe(new Consumer<List<Movie>>() {
                    @Override
                    public void accept(List<Movie> movieList) throws Exception {
                        updateUi(movieList);
                    }
                });
    }

    private void unsubscribeFromMovieList() {
        if (subscription != null && !subscription.isDisposed()) subscription.dispose();
    }

    void updateUi(List<Movie> newList) {
        mAdapter.setMovieList(newList);
        mSwipeRefreshLayout.setRefreshing(false);
        loadingCache = false;
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

    // update cache for current page type
    private void updateCurrentPageCache() {
        viewModel.forceRefresh();
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
                loadingCache = true;
                viewModel.loadNextPage();
            }
        }
    }

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
        void onItemSelected(long movieId, ImageView posterView);
    }
}
