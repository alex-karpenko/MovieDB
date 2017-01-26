package com.example.leshik.moviedb;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Activity class for deal with detail movie information
 * It starts from MainActivity by clicking on the poster image in list
 */
public class DetailActivity extends AppCompatActivity implements DetailFragment.Callback {
    private static final String TAG = "DetailActivity";
    private static final String MOVIE_URI = "MOVIE_URI";
    Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.applyCurrentTheme(this);
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
            // get URI from intent
            mUri = getIntent().getData();
        } else {
            // or restore it from saved state
            mUri = savedInstanceState.getParcelable(MOVIE_URI);
        }

        Bundle args = new Bundle();
        args.putParcelable(DetailFragment.FRAGMENT_MOVIE_URI, mUri);

        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);

        // add new fragment with detail info
        if (savedInstanceState != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, fragment)
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_container, fragment)
                    .commit();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MOVIE_URI, mUri);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.restartActivityIfNeed(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle menu item clicks here.
        // Get pressed menu item
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onImageClicked(int movieId, String posterName) {
        Utils.startFullPosterActivity(this, movieId, posterName);
    }
}
