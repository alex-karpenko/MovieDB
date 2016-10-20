package com.example.leshik.moviedb;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
*  Activity class for deal with detail movie information 
*  It starts from MainActivity by clicking on the poster image in list
*
*/
public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            // Inflate new fragment (inner class belov) with detail info
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_container, new DetailFragment())
                    .commit();
        }
    }

    /**
     *  Fragment class with detail info about movie
     *  Information (MovieInfo instance) gets from Intent
     *  In future maybe will be necessary to get some information via HTTP request to TMDB,
     *  with movie referenced by ID (take it from MovieInfo)
     */
    public static class DetailFragment extends Fragment {
        private static final String LOG_TAG = DetailFragment.class.getSimpleName();
        private MovieInfo movieInfo;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate fragment
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            // The detail Activity called via intent.  Inspect the intent for MovieInfo data.
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra(MovieUtils.EXTRA_MOVIE_INFO)) {
                movieInfo = (MovieInfo) intent.getParcelableExtra(MovieUtils.EXTRA_MOVIE_INFO);

                if (movieInfo != null) {
                    // Post image into fragment
                    ImageView posterView = (ImageView) rootView.findViewById(R.id.poster_image);
                    // Post image by callinf Picasso methods
                    Picasso.with(getActivity())
                            .load(MovieUtils.basePosterUrl
                                    + "w185" // TODO: we have to think to adopt width on image
                                    + movieInfo.getPosterPath()).into(posterView);
                    // Title
                    TextView title=(TextView) rootView.findViewById(R.id.detail_title);
                    title.setText(movieInfo.getOriginalTitle());
                    // Release date
                    TextView released=(TextView) rootView.findViewById(R.id.detail_released);
                    released.setText(movieInfo.getReleaseDate());
                    // Rating, format it from double value to string like XX.X (one digit after dec.point)
                    TextView rating=(TextView) rootView.findViewById(R.id.detail_rating);
                    rating.setText(String.format("%.1f/10", movieInfo.getVoteAverage()));
                    // Overview
                    TextView overview=(TextView) rootView.findViewById(R.id.detail_overview);
                    overview.setText(movieInfo.getOverviewText());
                } else {
                    Log.e(LOG_TAG, "movieInfo is null");
                }
            }

            return rootView;
        }
    }
}
