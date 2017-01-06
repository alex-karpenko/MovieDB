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
import android.widget.TextView;

import com.example.leshik.moviedb.model.MoviesContract;
import com.example.leshik.moviedb.service.CacheUpdateService;
import com.squareup.picasso.Picasso;

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
    private Uri mUri;
    private long movieId;

    ImageView posterView;
    TextView title;
    TextView released;
    TextView rating;
    TextView overview;

    boolean isFavorite;
    Menu mMenu;

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
        posterView = (ImageView) rootView.findViewById(R.id.poster_image);
        title = (TextView) rootView.findViewById(R.id.detail_title);
        released = (TextView) rootView.findViewById(R.id.detail_released);
        rating = (TextView) rootView.findViewById(R.id.detail_rating);
        overview = (TextView) rootView.findViewById(R.id.detail_overview);

        isFavorite = false;

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // create loader
        getLoaderManager().initLoader(DETAIL_FRAGMENT_LOADER, null, this);
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
            // TODO: implement movie refreshing
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
                            .load(Utils.basePosterUrl
                                    + "w185" // TODO: we have to think to adopt width on image
                                    + data.getString(MoviesContract.Movies.DETAIL_PROJECTION_INDEX_POSTER_PATH))
                            .into(posterView);
                    title.setText(data.getString(MoviesContract.Movies.DETAIL_PROJECTION_INDEX_ORIGINAL_TITLE));
                    released.setText(data.getString(MoviesContract.Movies.DETAIL_PROJECTION_INDEX_RELEASE_DATE));
                    rating.setText(String.format("%.1f/10", data.getFloat(MoviesContract.Movies.DETAIL_PROJECTION_INDEX_VOTE_AVERAGE)));
                    overview.setText(data.getString(MoviesContract.Movies.DETAIL_PROJECTION_INDEX_OVERVIEW));
                }
                break;
            case FAVORITE_MARK_LOADER:
                isFavorite = false;
                if (data != null && data.moveToFirst()) {
                    isFavorite = true;
                }
                setFavoriteIcon(isFavorite);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Nothing to do
    }

    void setFavoriteIcon(boolean flag) {
        int favIcon;
        if (flag) favIcon = R.drawable.ic_favorite_black;
        else favIcon = R.drawable.ic_favorite_outline;
        if (mMenu != null) {
            MenuItem favMenuItem = mMenu.findItem(R.id.action_favorite);
            favMenuItem.setIcon(favIcon);
        }
    }
}
