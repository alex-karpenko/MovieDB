package com.example.leshik.moviedb;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.leshik.moviedb.model.MoviesContract;
import com.example.leshik.moviedb.service.CacheUpdateService;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FullPosterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FullPosterFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String ARG_POSTER_NAME = "POSTER_NAME";
    private static final String ARG_MOVIE_ID = "MOVIE_ID";
    private static final String ARG_MOVIE_TITLE = "MOVIE_TITLE";

    private static final int FAVORITE_MARK_LOADER = 3;

    private String mPosterName;
    private int mMovieId;
    private String mMovieTitle;

    private Menu mMenu;
    private boolean isFavorite;

    public FullPosterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param posterName - file name of poster image.
     * @return A new instance of fragment FullPosterFragment.
     */
    public static FullPosterFragment newInstance(int movie_id, String posterName, String movieTitle) {
        FullPosterFragment fragment = new FullPosterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_POSTER_NAME, posterName);
        args.putInt(ARG_MOVIE_ID, movie_id);
        args.putString(ARG_MOVIE_TITLE, movieTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.poster_fragment, menu);
        mMenu = menu;
        getLoaderManager().initLoader(FAVORITE_MARK_LOADER, null, this);

        // Setup share provider
        // get provider's menu item
        MenuItem shareItem = menu.findItem(R.id.action_share);
        // ... and get provider fro it
        ShareActionProvider myShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);

        // create intent
        Intent myShareIntent = new Intent(Intent.ACTION_SEND);
        // TODO: 19.01.2017 : change type to html?
        myShareIntent.setType("text/*");
        // TODO: 19.01.2017 : create html page with content of the movie
        String contentToSend = Utils.getPosterFullUri(mPosterName).toString();
        myShareIntent.putExtra(Intent.EXTRA_TEXT, contentToSend);
        // set intent into provider
        myShareActionProvider.setShareIntent(myShareIntent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (savedInstanceState == null) {
            if (getArguments() != null) {
                mPosterName = getArguments().getString(ARG_POSTER_NAME);
                mMovieId = getArguments().getInt(ARG_MOVIE_ID, -1);
                mMovieTitle = getArguments().getString(ARG_MOVIE_TITLE);
            }
        } else {
            mPosterName = savedInstanceState.getString(ARG_POSTER_NAME);
            mMovieId = savedInstanceState.getInt(ARG_MOVIE_ID);
            mMovieTitle = savedInstanceState.getString(ARG_MOVIE_TITLE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_POSTER_NAME, mPosterName);
        outState.putInt(ARG_MOVIE_ID, mMovieId);
        outState.putString(ARG_MOVIE_TITLE, mMovieTitle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_favorite) {
            isFavorite = !isFavorite;
            Utils.setFavoriteIcon(isFavorite, mMenu);
            CacheUpdateService.startActionUpdateFavorite(getActivity(), mMovieId, isFavorite);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_full_poster, container, false);

        // Inflate the layout for this fragment
        TouchImageView mPosterImage = (TouchImageView) rootView.findViewById(R.id.full_poster_image);
        Picasso.with(getActivity())
                .load(Utils.getPosterFullUri(mPosterName))
                .into(mPosterImage);

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case FAVORITE_MARK_LOADER:
                if (mMovieId > 0) {
                    return new CursorLoader(getActivity(),
                            MoviesContract.Favorites.buildUri(mMovieId),
                            null, null, null, null);
                }
                break;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case FAVORITE_MARK_LOADER:
                isFavorite = false;
                if (data != null && data.moveToFirst()) {
                    isFavorite = true;
                }
                Utils.setFavoriteIcon(isFavorite, mMenu);
                break;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
