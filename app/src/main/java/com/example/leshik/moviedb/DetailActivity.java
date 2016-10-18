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

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_container, new DetailFragment())
                    .commit();
        }
    }

    /**
     *
     *
     */
    public static class DetailFragment extends Fragment {
        private static final String LOG_TAG = DetailFragment.class.getSimpleName();
        private MovieInfo movieInfo;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            // The detail Activity called via intent.  Inspect the intent for forecast data.
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra(MovieUtils.EXTRA_MOVIE_INFO)) {
                movieInfo = (MovieInfo) intent.getParcelableExtra(MovieUtils.EXTRA_MOVIE_INFO);

                if (movieInfo != null) {
                    // Post image into fragment
                    ImageView posterView = (ImageView) rootView.findViewById(R.id.poster_image);
                    Picasso.with(getActivity())
                            .load(MovieUtils.basePosterUrl
                                    + "w185" // !!!
                                    + movieInfo.getPosterPath()).into(posterView);
                    // Title
                    TextView title=(TextView) rootView.findViewById(R.id.detail_title);
                    title.setText(movieInfo.getOriginalTitle());
                } else {
                    Log.e(LOG_TAG, "movieInfo is null");
                }
            }

            return rootView;
        }
    }
}
