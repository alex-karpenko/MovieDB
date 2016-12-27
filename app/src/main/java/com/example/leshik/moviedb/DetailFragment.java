package com.example.leshik.moviedb;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.leshik.moviedb.model.MoviesContract;
import com.squareup.picasso.Picasso;

/**
 * Fragment class with detail info about movie
 * Information (MovieInfo instance) gets from Intent
 * In future maybe will be necessary to get some information via HTTP request to TMDB,
 * with movie referenced by ID (take it from MovieInfo)
 */
public class DetailFragment extends Fragment implements LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    public static final String MOVIE_URI = "MOVIE_URI";
    private static final int DETAIL_FRAGMENT_LOADER = 2;
    private Uri mUri;

    ImageView posterView;
    TextView title;
    TextView released;
    TextView rating;
    TextView overview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get bundle with args (URI)
        Bundle args = getArguments();
        if (args != null) mUri = args.getParcelable(MOVIE_URI);
        // Inflate fragment
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        posterView = (ImageView) rootView.findViewById(R.id.poster_image);
        title = (TextView) rootView.findViewById(R.id.detail_title);
        released = (TextView) rootView.findViewById(R.id.detail_released);
        rating = (TextView) rootView.findViewById(R.id.detail_rating);
        overview = (TextView) rootView.findViewById(R.id.detail_overview);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(DETAIL_FRAGMENT_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mUri != null) {
            return new CursorLoader(getActivity(),
                    mUri,
                    MoviesContract.Movies.DETAIL_PROJECTION,
                    null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
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
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }


}
