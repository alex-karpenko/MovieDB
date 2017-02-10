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
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FullPosterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FullPosterFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "FullPosterFragment";
    // state and fragment's arguments markers
    private static final String ARG_POSTER_NAME = "POSTER_NAME";
    private static final String ARG_MOVIE_ID = "MOVIE_ID";
    private static final String ARG_MOVIE_TITLE = "MOVIE_TITLE";

    // content loader's number
    private static final int FAVORITE_MARK_LOADER = 3;

    private String mPosterName;
    private int mMovieId;
    private String mMovieTitle;

    private Menu mMenu;
    private ShareActionProvider mShareActionProvider;
    private boolean isFavorite;

    @BindView(R.id.full_poster_image)
    protected PhotoView mPosterImage;
    private Unbinder unbinder;


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
        // set variables to argument's bundle
        args.putString(ARG_POSTER_NAME, posterName);
        args.putInt(ARG_MOVIE_ID, movie_id);
        args.putString(ARG_MOVIE_TITLE, movieTitle);
        // create fragment
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.poster_fragment, menu);
        mMenu = menu;
        getLoaderManager().initLoader(FAVORITE_MARK_LOADER, null, this);

        // Setup share provider
        // and update share action intent
        if (menu != null) {
            mShareActionProvider = new ShareActionProvider(getContext());
            MenuItemCompat.setActionProvider(menu.findItem(R.id.action_share), mShareActionProvider);

            // create intent
            Intent myShareIntent = Utils.getShareIntent(getContext(), mMovieTitle, mPosterName);
            // set intent into provider
            mShareActionProvider.setShareIntent(myShareIntent);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // set state variables from saved state or argument's bundle
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
            // toggle favorite flag
            isFavorite = !isFavorite;
            // change mark on the toolbar
            Utils.setFavoriteIcon(isFavorite, mMenu);
            // and update state in db table via intent service
            CacheUpdateService.startActionUpdateFavorite(getActivity(), mMovieId, isFavorite);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_full_poster, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        final PhotoViewAttacher attacher = new PhotoViewAttacher(mPosterImage);
        attacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                onImageClicked();
            }

            @Override
            public void onOutsidePhotoTap() {
                onImageClicked();
            }
        });

        Picasso.with(getActivity())
                .load(Utils.getPosterFullUri(mPosterName))
                .into(mPosterImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        attacher.update();
                    }

                    @Override
                    public void onError() {
                    }
                });

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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
                // set favorite mark if table contain row with our movie_id
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

    interface OnImageClickCallback {
        void onImageClicked();
    }

    private void onImageClicked() {
        ((FullPosterActivity) getActivity()).onImageClicked();
    }
}
