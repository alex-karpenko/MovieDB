package com.example.leshik.moviedb;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
    public static final String FRAGMENT_MOVIE_URI = "FRAGMENT_MOVIE_URI";
    private static final int MOVIE_LOADER = 2;
    private static final int FAVORITE_MARK_LOADER = 3;
    private static final int VIDEOS_LOADER = 4;
    private static final int REVIEWS_LOADER = 5;

    private Uri mUri;
    private long movieId;

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

    private SimpleCursorAdapter mVideosListAdapter;
    private SimpleCursorAdapter mReviewsListAdapter;

    private String mPosterName;
    private boolean isFavorite;
    private Menu mMenu;

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

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mVideosListLayout = (LinearLayout) rootView.findViewById(R.id.videos_layout);
        NonScrollListView mVideosList = (NonScrollListView) rootView.findViewById(R.id.videos_list);
        mVideosListLayout.setVisibility(View.GONE);

        mReviewsListLayout = (LinearLayout) rootView.findViewById(R.id.reviews_layout);
        mReviewsListTable = (TableLayout) rootView.findViewById(R.id.reviews_list);
        mReviewsListLayout.setVisibility(View.GONE);

        mVideosListAdapter = new SimpleCursorAdapter(getContext(),
                R.layout.videos_list_item,
                null, // cursor
                new String[]{MoviesContract.Videos.COLUMN_NAME_NAME},
                new int[]{R.id.videos_list_item_title},
                0);
        mVideosList.setAdapter(mVideosListAdapter);

        mReviewsListAdapter = new SimpleCursorAdapter(getContext(),
                R.layout.reviews_list_item,
                null, // cursor
                new String[]{MoviesContract.Reviews.COLUMN_NAME_AUTHOR, MoviesContract.Reviews.COLUMN_NAME_CONTENT},
                new int[]{R.id.reviews_list_item_author, R.id.reviews_list_item_content},
                0);
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
        // mReviewsListTable.setAdapter(mReviewsListAdapter);

        isFavorite = false;

        // Click listeners
        // click on video list item - call youtube app
        mVideosList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = mVideosListAdapter.getCursor();
                cursor.moveToPosition(position);
                String key = cursor.getString(MoviesContract.Videos.DETAIL_PROJECTION_INDEX_KEY);
                Utils.watchYoutubeVideo(getContext(), key);
            }
        });

        // clock on poster image - set fragment to full poster image view
        mPosterImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPosterName != null) {
                    ((DetailFragment.Callback) getContext()).onImageClicked((int) movieId, mPosterName);
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
        mMenu = menu;
        getLoaderManager().initLoader(FAVORITE_MARK_LOADER, null, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            mSwipeRefreshLayout.setRefreshing(true);
            refreshCurrentMovieReverseOrder();
            return true;
        }
        if (id == R.id.action_favorite) {
            isFavorite = !isFavorite;
            Utils.setFavoriteIcon(isFavorite, mMenu);
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
                mSwipeRefreshLayout.setRefreshing(false);
                if (data != null && data.moveToFirst()) {
                    // Setting all view's content
                    mPosterName = data.getString(MoviesContract.Movies.DETAIL_PROJECTION_INDEX_POSTER_PATH);
                    Picasso.with(getActivity())
                            .load(Utils.getPosterSmallUri(mPosterName))
                            .into(mPosterImage);

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
                // Update movie if need
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
                if (data != null && data.moveToFirst()) {
                    isFavorite = true;
                }
                Utils.setFavoriteIcon(isFavorite, mMenu);
                break;
            case VIDEOS_LOADER:
                if (data != null) {
                    mVideosListAdapter.swapCursor(data);
                    if (data.moveToFirst() && data.getCount() > 0) {
                        mVideosListLayout.setVisibility(View.VISIBLE);
                    }
                } else mVideosListLayout.setVisibility(View.GONE);
                break;
            case REVIEWS_LOADER:
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

    void refreshCurrentMovie() {
        CacheUpdateService.startActionUpdateMovie(getActivity(), (int) movieId);
        CacheUpdateService.startActionUpdateVideos(getActivity(), (int) movieId);
        CacheUpdateService.startActionUpdateReviews(getActivity(), (int) movieId, -1);
    }

    void refreshCurrentMovieReverseOrder() {
        CacheUpdateService.startActionUpdateVideos(getActivity(), (int) movieId);
        CacheUpdateService.startActionUpdateReviews(getActivity(), (int) movieId, -1);
        CacheUpdateService.startActionUpdateMovie(getActivity(), (int) movieId);
    }

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
        void onImageClicked(int movieId, String posterName);
    }
}
