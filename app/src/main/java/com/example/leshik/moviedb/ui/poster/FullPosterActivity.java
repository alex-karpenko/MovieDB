package com.example.leshik.moviedb.ui.poster;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.leshik.moviedb.R;
import com.example.leshik.moviedb.data.MovieRepository;
import com.example.leshik.moviedb.data.PreferenceStorage;
import com.example.leshik.moviedb.data.interfaces.PreferenceInterface;
import com.example.leshik.moviedb.data.model.Movie;
import com.example.leshik.moviedb.ui.viewmodels.MovieViewModel;
import com.example.leshik.moviedb.utils.FirebaseUtils;
import com.example.leshik.moviedb.utils.ViewUtils;
import com.google.firebase.analytics.FirebaseAnalytics;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullPosterActivity extends AppCompatActivity implements FullPosterFragment.OnImageClickCallback {
    private static final String TAG = "FullPosterActivity";

    public static final String ARG_MOVIE_ID = "ARG_MOVIE_ID";

    private boolean mVisible;
    private long movieId;

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = false;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;

    // queue handler
    private final Handler mHideHandler = new Handler();

    // views to fast access
    @BindView(R.id.full_poster_title)
    protected TextView mTitleView;
    @BindView(R.id.poster_toolbar)
    protected Toolbar mToolbar;

    Unbinder unbinder;
    MovieViewModel mViewModel;
    CompositeDisposable subscription = new CompositeDisposable();

    private PreferenceInterface prefStorage;

    FirebaseAnalytics mFirebaseAnalytics;

    // helper method to create proper intent to start FullPosterActivity
    static public Intent getIntentInstance(Context context, long movieId) {
        Intent intent = new Intent(context, FullPosterActivity.class);
        intent.putExtra(FullPosterActivity.ARG_MOVIE_ID, movieId);
        return intent;
    }

    // runnable to hide views after start
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    // runnable to hide views by touch
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed hide of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }
            if (mTitleView != null) mTitleView.setVisibility(View.INVISIBLE);
        }
    };

    // runnable to show views by touch
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            if (mTitleView != null) mTitleView.setVisibility(View.VISIBLE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefStorage = PreferenceStorage.getInstance(this.getApplicationContext());
        ViewUtils.applyTheme(this, prefStorage.getTheme());
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            // get poster image name from intent
            Intent intent = getIntent();
            movieId = intent.getLongExtra(ARG_MOVIE_ID, -1);
        } else {
            // ... or from saved state
            movieId = savedInstanceState.getLong(ARG_MOVIE_ID, -1);
        }

        setContentView(R.layout.activity_full_poster);
        unbinder = ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        // set semi-transparent toolbar's background
        mToolbar.getBackground().setAlpha(128);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // show up button
            actionBar.setDisplayHomeAsUpEnabled(true);
            // and hide title
            actionBar.setDisplayShowTitleEnabled(false);
        }

        mVisible = true;

        // Inflate new fragment full screen poster image
        FullPosterFragment fragment = FullPosterFragment.newInstance(movieId);
        if (savedInstanceState != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fullscreen_content, fragment)
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fullscreen_content, fragment)
                    .commit();
        }

        // init view model
        mViewModel = new MovieViewModel(movieId, new MovieRepository(getApplicationContext()));
        subscribeToMovie();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT,
                FirebaseUtils.createAnalyticsSelectBundle(TAG, "Create Full Poster Activity", "Poster"));
    }

    @Override
    protected void onDestroy() {
        unsubscribeFromMovie();
        unbinder.unbind();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(ARG_MOVIE_ID, movieId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.poster, menu);
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(AUTO_HIDE_DELAY_MILLIS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // method to toggle visibility state of toolbar and title
    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    // hide toolbar an title
    private void hide() {
        mVisible = false;
        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    // show toolbar and title
    @SuppressLint("InlinedApi")
    private void show() {
        mVisible = true;
        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    public void onImageClicked() {
        if (AUTO_HIDE) {
            delayedHide(AUTO_HIDE_DELAY_MILLIS);
        }
        toggle();
    }

    private void subscribeToMovie() {
        subscription.add(mViewModel.getMovie()
                .subscribe(new Consumer<Movie>() {
                    @Override
                    public void accept(Movie movie) throws Exception {
                        updateUi(movie);
                    }
                }));
    }

    private void unsubscribeFromMovie() {
        subscription.dispose();
    }

    private void updateUi(Movie movie) {
        mTitleView.setText(movie.getOriginalTitle());
    }
}
