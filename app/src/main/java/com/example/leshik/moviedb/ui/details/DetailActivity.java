package com.example.leshik.moviedb.ui.details;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.leshik.moviedb.R;
import com.example.leshik.moviedb.data.PreferenceStorage;
import com.example.leshik.moviedb.data.interfaces.PreferenceInterface;
import com.example.leshik.moviedb.ui.poster.FullPosterActivity;
import com.example.leshik.moviedb.ui.settings.SettingsActivity;
import com.example.leshik.moviedb.utils.EventsUtils;
import com.example.leshik.moviedb.utils.ViewUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Activity class for deal with detail movie information
 * It starts from MainActivity by clicking on the poster image in list
 */
public class DetailActivity extends AppCompatActivity implements DetailFragment.Callback {
    private static final String TAG = "DetailActivity";
    // marker string and variable to state saving
    private static final String ARG_MOVIE_ID = "ARG_MOVIE_ID";
    long movieId;

    @BindView(R.id.detail_frame)
    protected LinearLayout mDetailFrame;

    private PreferenceInterface prefStorage;

    private Disposable subscription;

    // helper method to create proper intent to start DetailActivity
    static public Intent getIntentInstance(Context context, long movieId) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(DetailActivity.ARG_MOVIE_ID, movieId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefStorage = PreferenceStorage.getInstance(this.getApplicationContext());
        ViewUtils.applyTheme(this, prefStorage.getTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

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

        subscription = EventsUtils.getEventObservable()
                .subscribe(new Consumer<EventsUtils.EventType>() {
                    @Override
                    public void accept(@NonNull EventsUtils.EventType eventType) throws Exception {
                        Toast.makeText(DetailActivity.this, eventType.getMessageId(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        subscription.dispose();
        super.onDestroy();
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
        ViewUtils.restartActivityIfNeed(this);
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
    public void onImageClicked(long movieId, ImageView posterView) {
        // callback method that called when poster image is clicked
        // start full poster view activity
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(this, posterView, getString(R.string.poster_image));
            startActivity(FullPosterActivity.getIntentInstance(this, movieId), options.toBundle());
        } else {
            startActivity(FullPosterActivity.getIntentInstance(this, movieId));
        }
    }
}
