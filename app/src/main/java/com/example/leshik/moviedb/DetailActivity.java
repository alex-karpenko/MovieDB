package com.example.leshik.moviedb;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Activity class for deal with detail movie information
 * It starts from MainActivity by clicking on the poster image in list
 */
public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.MOVIE_URI, getIntent().getData());

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            // Inflate new fragment (inner class below) with detail info
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_container, fragment)
                    .commit();
        }
    }

}
