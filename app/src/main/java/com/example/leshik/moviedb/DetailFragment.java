package com.example.leshik.moviedb;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.leshik.moviedb.model.MoviesContract;
import com.example.leshik.moviedb.service.CacheUpdateService;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

/**
 * Fragment class with detail info about movie
 * Information. From intent gets URI with movie
 */
public class DetailFragment extends Fragment implements LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "DetailFragment";
    // data tag to pass data via args bundle
    private static final int MOVIE_LOADER = 2;
    private static final int FAVORITE_MARK_LOADER = 3;
    private static final int VIDEOS_LOADER = 4;
    private static final int REVIEWS_LOADER = 5;

    // fragment args and state mark
    public static final String FRAGMENT_MOVIE_URI = "FRAGMENT_MOVIE_URI";
    // state variables
    private Uri mUri;
    private long movieId;

    // references for all views
    private ImageView mPosterImage;
    private TextView mTitleText;
    private TextView mReleasedText;
    private TextView mRuntimeText;
    private TextView mRatingText;
    private TextView mOverviewText;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private LinearLayout mVideosListLayout;
    private LinearLayout mReviewsListLayout;

    private TableLayout mReviewsListTable;
    private TableLayout mVideosListTable;

    private SimpleCursorAdapter mVideosListAdapter;
    private SimpleCursorAdapter mReviewsListAdapter;

    private Menu mMenu;

    // variables to store data for updating share action intent
    private String mPosterName;
    private String mMovieTitle;

    // favorite flag
    private boolean isFavorite;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // restore movie uri
        if (savedInstanceState != null) {
            mUri = savedInstanceState.getParcelable(FRAGMENT_MOVIE_URI);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // put movie uri into state bundle
        outState.putParcelable(FRAGMENT_MOVIE_URI, mUri);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get bundle with args (URI)
        Bundle args = getArguments();
        if (args != null) mUri = args.getParcelable(FRAGMENT_MOVIE_URI);
        if (mUri != null) movieId = ContentUris.parseId(mUri);

        // Inflate fragment
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        // Store views references for faster access during updates
        mPosterImage = (ImageView) rootView.findViewById(R.id.poster_image);
        mTitleText = (TextView) rootView.findViewById(R.id.detail_title);
        mReleasedText = (TextView) rootView.findViewById(R.id.detail_released);
        mRuntimeText = (TextView) rootView.findViewById(R.id.detail_runtime);
        mRatingText = (TextView) rootView.findViewById(R.id.detail_rating);
        mOverviewText = (TextView) rootView.findViewById(R.id.detail_overview);

        // for swipe refresh
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        // list of videos (as table)
        mVideosListLayout = (LinearLayout) rootView.findViewById(R.id.videos_layout);
        mVideosListTable = (TableLayout) rootView.findViewById(R.id.videos_list);
        // hide it until load data
        mVideosListLayout.setVisibility(View.GONE);

        // list on reviews (as table)
        mReviewsListLayout = (LinearLayout) rootView.findViewById(R.id.reviews_layout);
        mReviewsListTable = (TableLayout) rootView.findViewById(R.id.reviews_list);
        // hide it until load data
        mReviewsListLayout.setVisibility(View.GONE);

        // set videos list adapter with empty cursor
        mVideosListAdapter = new SimpleCursorAdapter(getContext(),
                R.layout.videos_list_item,
                null, // cursor
                new String[]{MoviesContract.Videos.COLUMN_NAME_NAME},
                new int[]{R.id.videos_list_item_frame}, // dest. view is root table (for create onClick listener for whole view)
                0);
        // we need separate view binder because our view is not a simple text view
        // and we need onClick listener to start video watching via youtube
        mVideosListAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int i) {
                if (i == MoviesContract.Videos.DETAIL_PROJECTION_INDEX_NAME) {
                    // text view with video title
                    TextView titleView = (TextView) view.findViewById(R.id.videos_list_item_title);
                    titleView.setText(cursor.getString(i)); // set text from cursor data
                    // tag view with youtube video key
                    view.setTag(cursor.getString(MoviesContract.Videos.DETAIL_PROJECTION_INDEX_KEY));
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // start video watching, key of the video we extract from view's tag
                            Utils.watchYoutubeVideo(getContext(), (String) view.getTag());
                        }
                    });
                    return true;
                }
                return false;
            }
        });

        // adapter for reviews
        mReviewsListAdapter = new SimpleCursorAdapter(getContext(),
                R.layout.reviews_list_item,
                null, // cursor
                new String[]{MoviesContract.Reviews.COLUMN_NAME_AUTHOR, MoviesContract.Reviews.COLUMN_NAME_CONTENT},
                new int[]{R.id.reviews_list_item_author, R.id.reviews_list_item_content},
                0);
        // we need separate view binder because our view is not a simple text
        mReviewsListAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int i) {
                if (i == MoviesContract.Reviews.DETAIL_PROJECTION_INDEX_CONTENT) {
                    ((ExpandableTextView) view).setText(cursor.getString(i));
                    return true;
                }
                return false;
            }
        });

        isFavorite = false;

        // set onClick listener for poster image
        // click on poster image - call to full poster image view via callback in the activity
        mPosterImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPosterName != null) {
                    ((DetailFragment.Callback) getContext()).onImageClicked((int) movieId, mPosterName, mMovieTitle);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // create loaders
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        getLoaderManager().initLoader(VIDEOS_LOADER, null, this);
        getLoaderManager().initLoader(REVIEWS_LOADER, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail_fragment, menu);

        // store ref. to the menu to update favorite mark
        mMenu = menu;
        // and create loader for the fav.mark icon
        getLoaderManager().initLoader(FAVORITE_MARK_LOADER, null, this);

        // and update share action intent
        if (menu != null)
            updateShareAction(menu.findItem(R.id.action_share), mMovieTitle, mPosterName);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            // set refresh mark
            mSwipeRefreshLayout.setRefreshing(true);
            // and start update action
            refreshCurrentMovieReverseOrder();
            return true;
        }
        if (id == R.id.action_favorite) {
            // reverse mark flag
            isFavorite = !isFavorite;
            // and change mark on the menu with dependence on the theme
            Utils.setFavoriteIcon(isFavorite, mMenu);
            // update favorites table via service on work thread
            CacheUpdateService.startActionUpdateFavorite(getActivity(), movieId, isFavorite);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        switch (loaderId) {
            case MOVIE_LOADER:
                if (mUri != null) {
                    return new CursorLoader(getActivity(),
                            mUri,
                            MoviesContract.Movies.DETAIL_PROJECTION,
                            null, null, null);
                }
                break;
            case VIDEOS_LOADER:
                mVideosListLayout.setVisibility(View.GONE);
                if (movieId > 0) {
                    return new CursorLoader(getActivity(),
                            MoviesContract.Videos.buildUri(movieId),
                            MoviesContract.Videos.DETAIL_PROJECTION,
                            null, null, null);
                }
                break;
            case REVIEWS_LOADER:
                mReviewsListLayout.setVisibility(View.GONE);
                if (movieId > 0) {
                    return new CursorLoader(getActivity(),
                            MoviesContract.Reviews.buildUri(movieId),
                            MoviesContract.Reviews.DETAIL_PROJECTION,
                            null, null, null);
                }
                break;
            case FAVORITE_MARK_LOADER:
                if (movieId > 0) {
                    return new CursorLoader(getActivity(),
                            MoviesContract.Favorites.buildUri(movieId),
                            null, null, null, null);
                }
                break;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case MOVIE_LOADER:
                // stop refresh mark view
                mSwipeRefreshLayout.setRefreshing(false);
                if (data != null && data.moveToFirst()) {
                    // update variables for share action provider
                    mPosterName = data.getString(MoviesContract.Movies.DETAIL_PROJECTION_INDEX_POSTER_PATH);
                    mMovieTitle = data.getString(MoviesContract.Movies.DETAIL_PROJECTION_INDEX_ORIGINAL_TITLE);
                    if (mMenu != null)
                        updateShareAction(mMenu.findItem(R.id.action_share), mMovieTitle, mPosterName);
                    // load poster image
                    Picasso.with(getActivity())
                            .load(Utils.getPosterSmallUri(mPosterName))
                            .into(mPosterImage);
                    // Setting all view's content
                    mTitleText.setText(data.getString(MoviesContract.Movies.DETAIL_PROJECTION_INDEX_ORIGINAL_TITLE));
                    mReleasedText.setText(data.getString(MoviesContract.Movies.DETAIL_PROJECTION_INDEX_RELEASE_DATE));
                    // TODO: make mRuntimeText formatting more reliable
                    if (data.getInt(MoviesContract.Movies.DETAIL_PROJECTION_INDEX_RUNTIME) > 0) {
                        mRuntimeText.setText(String.format("%d %s",
                                data.getInt(MoviesContract.Movies.DETAIL_PROJECTION_INDEX_RUNTIME),
                                getString(R.string.runtime_minutes_text)));
                    } else {
                        mRuntimeText.setText("-");
                    }
                    mRatingText.setText(String.format("%.1f/10", data.getFloat(MoviesContract.Movies.DETAIL_PROJECTION_INDEX_VOTE_AVERAGE)));
                    mOverviewText.setText(data.getString(MoviesContract.Movies.DETAIL_PROJECTION_INDEX_OVERVIEW));
                }
                // Update movie cache if need
                long lastUpdateTime = data.getLong(MoviesContract.Movies.DETAIL_PROJECTION_INDEX_LAST_UPDATED);
                if (Utils.getCacheUpdateInterval() > 0 || lastUpdateTime <= 0) {
                    long currentTime = Calendar.getInstance().getTimeInMillis();
                    if ((currentTime - lastUpdateTime) >= Utils.getCacheUpdateInterval()) {
                        refreshCurrentMovie();
                    }
                }
                break;

            case FAVORITE_MARK_LOADER:
                isFavorite = false;
                // set favorite mark if record in the favorites table is present
                if (data != null && data.moveToFirst()) {
                    isFavorite = true;
                }
                // and update mark
                Utils.setFavoriteIcon(isFavorite, mMenu);
                break;

            case VIDEOS_LOADER:
                // remove all views in table layout
                mVideosListTable.removeAllViews();
                if (data != null) {
                    // set cursor to the adapter
                    mVideosListAdapter.swapCursor(data);
                    if (data.moveToFirst() && data.getCount() > 0) {
                        // and insert table row for every record
                        int rowsCount = mVideosListAdapter.getCount();
                        for (int i = 0; i < rowsCount; i++) {
                            // get view via adapter
                            View v = mVideosListAdapter.getView(i, null, mVideosListTable);
                            // add row in the layout
                            mVideosListTable.addView(v);
                        }
                        // set whole layout visible
                        mVideosListLayout.setVisibility(View.VISIBLE);
                    }
                    // if cursor is empty - hide whole table layout
                } else mVideosListLayout.setVisibility(View.GONE);
                break;

            case REVIEWS_LOADER:
                // view comments above on videos list - all same ...
                mReviewsListTable.removeAllViews();
                if (data != null) {
                    mReviewsListAdapter.swapCursor(data);
                    if (data.moveToFirst() && data.getCount() > 0) {
                        int rowsCount = mReviewsListAdapter.getCount();
                        for (int i = 0; i < rowsCount; i++) {
                            View v = mReviewsListAdapter.getView(i, null, mReviewsListTable);
                            mReviewsListTable.addView(v);
                        }
                        mReviewsListLayout.setVisibility(View.VISIBLE);
                    }
                } else mReviewsListLayout.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case VIDEOS_LOADER:
                if (mVideosListAdapter != null) mVideosListAdapter.swapCursor(null);
                break;
            case REVIEWS_LOADER:
                if (mReviewsListAdapter != null) mReviewsListAdapter.swapCursor(null);
                break;
        }
    }

    // starting intent services to update cache tables
    void refreshCurrentMovie() {
        CacheUpdateService.startActionUpdateMovie(getActivity(), (int) movieId);
        CacheUpdateService.startActionUpdateVideos(getActivity(), (int) movieId);
        CacheUpdateService.startActionUpdateReviews(getActivity(), (int) movieId, -1);
    }

    // starting intent services to update cache tables in reverse order (movies at last)
    void refreshCurrentMovieReverseOrder() {
        CacheUpdateService.startActionUpdateVideos(getActivity(), (int) movieId);
        CacheUpdateService.startActionUpdateReviews(getActivity(), (int) movieId, -1);
        CacheUpdateService.startActionUpdateMovie(getActivity(), (int) movieId);
    }

    // swipe refresh layout callback
    @Override
    public void onRefresh() {
        refreshCurrentMovieReverseOrder();
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of poster image
     * clicked.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        void onImageClicked(int movieId, String posterName, String movieTitle);
    }

    /**
     * Method to update share action intent after loading movie data
     *
     * @param menuItem
     * @param title
     * @param poster
     */
    void updateShareAction(MenuItem menuItem, String title, String poster) {
        // Setup share provider
        ShareActionProvider myShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // create intent
        Intent myShareIntent = Utils.getShareIntent(getContext(), title, poster);
        // set intent into provider
        myShareActionProvider.setShareIntent(myShareIntent);
    }
}
