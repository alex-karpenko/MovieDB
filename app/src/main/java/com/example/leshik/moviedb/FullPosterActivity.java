package com.example.leshik.moviedb;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullPosterActivity extends AppCompatActivity {
    public static final String ARG_POSTER_NAME = "POSTER_NAME";
    public static final String ARG_MOVIE_TITLE = "MOVIE_TITLE";
    public static final String ARG_MOVIE_ID = "MOVIE_ID";

    private boolean mVisible;
    private int movieId;
    private String posterName;
    private String movieTitle;

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
    @BindView(R.id.fullscreen_content)
    protected View mContentView;
    @BindView(R.id.full_poster_title)
    protected TextView mTitleView;
    @BindView(R.id.poster_toolbar)
    protected Toolbar mToolbar;

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
        Utils.applyCurrentTheme(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_full_poster);
        ButterKnife.bind(this);

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

        if (savedInstanceState == null) {
            // get poster image name from intent
            Intent intent = getIntent();
            posterName = intent.getStringExtra(ARG_POSTER_NAME);
            movieId = intent.getIntExtra(ARG_MOVIE_ID, -1);
            movieTitle = intent.getStringExtra(ARG_MOVIE_TITLE);
        } else {
            // ... or from saved state
            posterName = savedInstanceState.getString(ARG_POSTER_NAME, "");
            movieId = savedInstanceState.getInt(ARG_MOVIE_ID, -1);
            movieTitle = savedInstanceState.getString(ARG_MOVIE_TITLE, "");
        }

        mTitleView.setText(movieTitle);

        // Inflate new fragment full screen poster image
        FullPosterFragment fragment = FullPosterFragment.newInstance(movieId, posterName, movieTitle);
        if (savedInstanceState != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fullscreen_content, fragment)
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fullscreen_content, fragment)
                    .commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_POSTER_NAME, posterName);
        outState.putInt(ARG_MOVIE_ID, movieId);
        outState.putString(ARG_MOVIE_TITLE, movieTitle);
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
    protected void onStart() {
        super.onStart();
        // set touch listener on image view to toggle toolbar/title visibility state
        TouchImageView imageView = (TouchImageView) mContentView.findViewById(R.id.full_poster_image);
        if (imageView != null) {
            imageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (AUTO_HIDE) {
                        delayedHide(AUTO_HIDE_DELAY_MILLIS);
                    }
                    toggle();
                    return true;
                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // clear touch listener
        TouchImageView imageView = (TouchImageView) mContentView.findViewById(R.id.full_poster_image);
        if (imageView != null) {
            imageView.setOnTouchListener(null);
        }
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
}
