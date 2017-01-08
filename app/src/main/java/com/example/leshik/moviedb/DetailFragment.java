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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.leshik.moviedb.model.MoviesContract;
import com.example.leshik.moviedb.service.CacheUpdateService;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

/**
 * Fragment class with detail info about movie
 * Information. From intent gets URI with movie
 */
public class DetailFragment extends Fragment implements LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    // data tag to pass data via intent
    public static final String MOVIE_URI = "MOVIE_URI";
    private static final int DETAIL_FRAGMENT_LOADER = 2;
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

    private LinearLayout mVideosListLayout;
    private NonScrollListView mVideosList;
    private LinearLayout mReviewsListLayout;
    private NonScrollListView mReviewsList;

    private SimpleCursorAdapter mVideosListAdapter;
    private SimpleCursorAdapter mReviewsListAdapter;

    private boolean isFavorite;
    private Menu mMenu;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get bundle with args (URI)
        Bundle args = getArguments();
        if (args != null) mUri = args.getParcelable(MOVIE_URI);
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
        mVideosListLayout = (LinearLayout) rootView.findViewById(R.id.videos_layout);
        mVideosList = (NonScrollListView) rootView.findViewById(R.id.videos_list);
        mReviewsListLayout = (LinearLayout) rootView.findViewById(R.id.reviews_layout);
        mReviewsList = (NonScrollListView) rootView.findViewById(R.id.reviews_list);

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
        mReviewsList.setAdapter(mReviewsListAdapter);

        isFavorite = false;

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // create loaders
        getLoaderManager().initLoader(DETAIL_FRAGMENT_LOADER, null, this);
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
            refreshCurrentMovie();
            return true;
        }
        if (id == R.id.action_favorite) {
            isFavorite = !isFavorite;
            CacheUpdateService.startActionUpdateFavorite(getActivity(), movieId, isFavorite);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        switch (loaderId) {
            case DETAIL_FRAGMENT_LOADER:
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
            case DETAIL_FRAGMENT_LOADER:
                if (data != null && data.moveToFirst()) {
                    // Setting all view's content
                    Picasso.with(getActivity())
                            .load(Utils.basePosterSecureUrl
                                    + "w185" // TODO: we have to think to adopt width on image
                                    + data.getString(MoviesContract.Movies.DETAIL_PROJECTION_INDEX_POSTER_PATH))
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
                long currentTime = Calendar.getInstance().getTimeInMillis();
                if ((currentTime - data.getLong(MoviesContract.Movies.DETAIL_PROJECTION_INDEX_LAST_UPDATED)) >= Utils.CACHE_UPDATE_INTERVAL) {
                    refreshCurrentMovie();
                }
                break;
            case FAVORITE_MARK_LOADER:
                isFavorite = false;
                if (data != null && data.moveToFirst()) {
                    isFavorite = true;
                }
                setFavoriteIcon(isFavorite);
                break;
            case VIDEOS_LOADER:
                if (data != null && data.moveToFirst() && data.getCount() > 0) {
                    mVideosListAdapter.swapCursor(data);
                    mVideosListLayout.setVisibility(View.VISIBLE);
                } else mVideosListLayout.setVisibility(View.GONE);
                break;
            case REVIEWS_LOADER:
                if (data != null && data.moveToFirst() && data.getCount() > 0) {
                    mReviewsListAdapter.swapCursor(data);
                    mReviewsListLayout.setVisibility(View.VISIBLE);
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

    void setFavoriteIcon(boolean flag) {
        int favIcon;
        if (flag) favIcon = R.drawable.ic_favorite_black_light;
        else favIcon = R.drawable.ic_favorite_outline_light;
        if (mMenu != null) {
            MenuItem favMenuItem = mMenu.findItem(R.id.action_favorite);
            favMenuItem.setIcon(favIcon);
        }
    }

    void refreshCurrentMovie() {
        CacheUpdateService.startActionUpdateMovie(getActivity(), (int) movieId);
        CacheUpdateService.startActionUpdateVideos(getActivity(), (int) movieId);
        CacheUpdateService.startActionUpdateReviews(getActivity(), (int) movieId, -1);
    }
}
