package com.example.leshik.moviedb;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

/**
 * Activity class for deal with detail movie information
 * It starts from MainActivity by clicking on the poster image in list
 */
public class DetailActivity extends AppCompatActivity implements DetailFragment.Callback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(mToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }


        if (savedInstanceState == null) {
            Bundle args = new Bundle();
            // put URI from intent to fragment's Data
            args.putParcelable(DetailFragment.MOVIE_URI, getIntent().getData());

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            // Inflate new fragment (inner class below) with detail info
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public void onImageClicked(String posterName) {
        Intent intent = new Intent(this, FullPosterActivity.class);
        intent.putExtra(FullPosterActivity.ARG_POSTER_NAME, posterName);
        startActivity(intent);
    }
}
