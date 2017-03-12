package com.example.leshik.moviedb.ui.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.leshik.moviedb.R;
import com.example.leshik.moviedb.data.PreferenceStorage;
import com.example.leshik.moviedb.data.interfaces.PreferenceInterface;
import com.example.leshik.moviedb.ui.poster.FullPosterActivity;
import com.example.leshik.moviedb.ui.settings.SettingsActivity;
import com.example.leshik.moviedb.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Activity class for deal with detail movie information
 * It starts from MainActivity by clicking on the poster image in list
 */
public class DetailActivity extends AppCompatActivity implements DetailFragment.Callback {
    private static final String TAG = "DetailActivity";
    // marker string and variable to state saving
    private static final String ARG_MOVIE_ID = "ARG_MOVIE_ID";
    long movieId;

    @BindView(R.id.detail_toolbar)
    protected Toolbar mToolbar;

    private PreferenceInterface prefStorage;

    // helper method to create proper intent to start DetailActivity
    static public Intent getIntentInstance(Context context, long movieId) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(DetailActivity.ARG_MOVIE_ID, movieId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefStorage = PreferenceStorage.getInstance(this.getApplicationContext());
        Utils.applyTheme(this, prefStorage.getTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }


        if (savedInstanceState == null) {
            // get movieId from intent
            movieId = getIntent().getLongExtra(ARG_MOVIE_ID, 0);
        } else {
            // or restore it from saved state
            movieId = savedInstanceState.getLong(ARG_MOVIE_ID);
        }

        // create fragment with all details info and add (or replace) it
        DetailFragment fragment = DetailFragment.newInstance(movieId);

        if (savedInstanceState != null) {
            // replace fragment if it is not a new activity
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, fragment)
                    .commit();
        } else {
            // add new fragment with detail info if no saved state
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_container, fragment)
                    .commit();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(ARG_MOVIE_ID, movieId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // restart activity after theme change
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
                // up toolbar button pressed
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onImageClicked(long movieId) {
        // callback method that called when poster image is clicked
        // start full poster view activity
        startActivity(FullPosterActivity.getIntentInstance(this, movieId));
    }
}
