package com.example.leshik.moviedb;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.example.leshik.moviedb.service.CacheUpdateService;

public class MainActivity extends AppCompatActivity implements MovieListFragment.Callback, DetailFragment.Callback {
    private static final String TAG = "MainActivity";
    private static final String STATE_CURRENT_PAGE = "STATE_CURRENT_PAGE";
    private static final String STATE_SELECTED_URI = "STATE_SELECTED_URI";

    public static String[] tabFragmentNames;

    private MainPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    private Uri selectedMovieUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.loadDefaultPreferences(this);
        Utils.applyCurrentTheme(this);

        super.onCreate(savedInstanceState);

        // Every time when activity created - update configuration from the TMDB
        Utils.basePosterUrl = Utils.getStringCachePreference(this, R.string.base_potser_url);
        Utils.basePosterSecureUrl = Utils.getStringCachePreference(this, R.string.base_potser_secure_url);

        CacheUpdateService.startActionUpdateConfiguration(this);

        setContentView(R.layout.activity_main);
        if (findViewById(R.id.detail_container) != null) {
            Utils.setTwoPane(true);
        } else Utils.setTwoPane(false);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);

        mPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.main_pager);
        mViewPager.setAdapter(mPagerAdapter);

        // Get list of tab's titles from resources
        Resources res = getResources();
        tabFragmentNames = res.getStringArray(R.array.main_tab_names);

        // Assign pager to tab layout
        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        if (savedInstanceState != null) {
            int selectedTab = savedInstanceState.getInt(STATE_CURRENT_PAGE);
            selectedMovieUri = savedInstanceState.getParcelable(STATE_SELECTED_URI);
            mViewPager.setCurrentItem(selectedTab);

            if (selectedMovieUri != null && Utils.isTwoPane()) {
                // replace fragment into details frame
                Bundle args = new Bundle();
                args.putParcelable(DetailFragment.FRAGMENT_MOVIE_URI, selectedMovieUri);

                DetailFragment fragment = new DetailFragment();
                fragment.setArguments(args);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container, fragment)
                        .commit();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_CURRENT_PAGE, mViewPager.getCurrentItem());
        if (selectedMovieUri != null) outState.putParcelable(STATE_SELECTED_URI, selectedMovieUri);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.restartActivityIfNeed(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the main; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle menu item clicks here.
        // Get pressed menu item
        int id = item.getItemId();

        // if Settings pressed, start SettingsActivity via explicit intent
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Uri movieUri, ImageView posterView) {
        selectedMovieUri = movieUri;

        Intent intent = new Intent(this, DetailActivity.class)
                .setData(movieUri);

        if (!Utils.isTwoPane()) {
            // If one pane - start details activity
            // If Lollipop or higher - start activity with image animation
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                ActivityOptions options = ActivityOptions
                        .makeSceneTransitionAnimation(this, posterView, getString(R.string.poster_image));
                startActivity(intent, options.toBundle());
            } else {
                // If lower then Lollipop - simple start detail activity
                startActivity(intent);
            }
        } else {
            // if two panes
            // replace fragment into details frame
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.FRAGMENT_MOVIE_URI, movieUri);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public void onImageClicked(int movieId, String posterName, String movieTitle) {
        Utils.startFullPosterActivity(this, movieId, posterName, movieTitle);
    }
}
