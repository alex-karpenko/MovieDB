package com.example.leshik.moviedb;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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

    private static final int FAVORITE_MARK_LOADER = 3;

    private String mPosterName;
    private int movieId;

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
    public static FullPosterFragment newInstance(int movie_id, String posterName) {
        FullPosterFragment fragment = new FullPosterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_POSTER_NAME, posterName);
        args.putInt(ARG_MOVIE_ID, movie_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.poster_fragment, menu);
        mMenu = menu;
        getLoaderManager().initLoader(FAVORITE_MARK_LOADER, null, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            mPosterName = getArguments().getString(ARG_POSTER_NAME);
            movieId = getArguments().getInt(ARG_MOVIE_ID, -1);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_favorite) {
            isFavorite = !isFavorite;
            Utils.setFavoriteIcon(isFavorite, mMenu);
            CacheUpdateService.startActionUpdateFavorite(getActivity(), movieId, isFavorite);
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
