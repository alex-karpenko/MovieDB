package com.example.leshik.moviedb.ui.main;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.leshik.moviedb.R;
import com.example.leshik.moviedb.data.PreferenceStorage;
import com.example.leshik.moviedb.data.interfaces.PreferenceInterface;
import com.example.leshik.moviedb.ui.details.DetailActivity;
import com.example.leshik.moviedb.ui.details.DetailFragment;
import com.example.leshik.moviedb.ui.settings.SettingsActivity;
import com.example.leshik.moviedb.utils.EventsUtils;
import com.example.leshik.moviedb.utils.FirebaseUtils;
import com.example.leshik.moviedb.utils.ViewUtils;
import com.google.firebase.analytics.FirebaseAnalytics;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity implements MovieListFragment.Callback {
    private static final String TAG = "MainActivity";
    // tags to saved state bundle
    private static final String STATE_CURRENT_PAGE = "STATE_CURRENT_PAGE";
    private static final String STATE_SELECTED_MOVIE_ID = "STATE_SELECTED_MOVIE_ID";

    // to store titles of the tabs (popular, top rated, favorites)
    public static String[] tabFragmentNames;

    // adapter to create fragments to pager
    private MainPagerAdapter mPagerAdapter;
    private ViewPagerListener mViewPagerListener;
    private SpinnerListener mSpinnerListener;

    @BindView(R.id.main_frame)
    protected LinearLayout mMainFrame;
    @BindView(R.id.main_pager)
    protected ViewPager mViewPager;
    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;
    @BindView(R.id.toolbar_spinner)
    protected Spinner mToolbarSpinner;

    // current selected movie (for two pane view)
    private long selectedMovieId = 0;

    private PreferenceInterface prefStorage;

    private FirebaseAnalytics mFirebaseAnalytics;

    private Disposable subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefStorage = PreferenceStorage.getInstance(this.getApplicationContext());
        ViewUtils.applyTheme(this, prefStorage.getTheme());
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);
        ButterKnife.bind(this);

        // check if the details container is present - two pane layout was loaded
        if (findViewById(R.id.detail_container) != null) {
            ViewUtils.setTwoPane(true);
        } else ViewUtils.setTwoPane(false);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Get list of tab's titles from resources
        tabFragmentNames = getResources().getStringArray(R.array.main_tab_names);
        // create pager adapter and set it to the pager view
        mPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);

        // Setup spinner
        ArrayAdapter<CharSequence> spinnerAdapter =
                ArrayAdapter.createFromResource(getSupportActionBar().getThemedContext(),
                        R.array.main_tab_names, R.layout.main_spinner_item);
        spinnerAdapter.setDropDownViewResource(R.layout.main_spinner_dropdown_item);
        mToolbarSpinner.setAdapter(spinnerAdapter);

        mViewPagerListener = new ViewPagerListener(mToolbarSpinner);
        mViewPager.addOnPageChangeListener(mViewPagerListener);

        mSpinnerListener = new SpinnerListener(mViewPager);
        mToolbarSpinner.setOnItemSelectedListener(mSpinnerListener);

        // restore state, if it was save
        if (savedInstanceState != null) {
            int selectedTab = savedInstanceState.getInt(STATE_CURRENT_PAGE);
            selectedMovieId = savedInstanceState.getLong(STATE_SELECTED_MOVIE_ID, 0);
            mViewPager.setCurrentItem(selectedTab);

            // restore content of the details fragment if it present
            if (selectedMovieId != 0 && ViewUtils.isTwoPane()) {
                // replace fragment into details frame
                DetailFragment fragment = DetailFragment.newInstance(selectedMovieId);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container, fragment)
                        .commit();
            }
        }

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT,
                FirebaseUtils.createAnalyticsSelectBundle(TAG, "Start Main Activity", TAG));

        subscription = EventsUtils.getEventObservable()
                .subscribe(new Consumer<EventsUtils.EventType>() {
                    @Override
                    public void accept(@NonNull EventsUtils.EventType eventType) throws Exception {
                        Toast.makeText(MainActivity.this, eventType.getMessageId(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        subscription.dispose();
        mToolbarSpinner.setOnItemSelectedListener(null);
        mViewPager.removeOnPageChangeListener(mViewPagerListener);
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_CURRENT_PAGE, mViewPager.getCurrentItem());
        if (selectedMovieId != 0) outState.putLong(STATE_SELECTED_MOVIE_ID, selectedMovieId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // if theme was changed - restart activity
        ViewUtils.restartActivityIfNeed(this);
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

    // listener action - if image from the list pressed - start details activity or update detail frame (if two panes)
    @Override
    public void onMovieListItemSelected(long movieId, ImageView posterView) {

        if (!ViewUtils.isTwoPane()) {
            Intent intent = DetailActivity.getIntentInstance(this, movieId);

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
            DetailFragment fragment = DetailFragment.newInstance(movieId);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, fragment)
                    .commit();
        }
    }

    static class ViewPagerListener extends ViewPager.SimpleOnPageChangeListener {
        private Spinner spinner;

        ViewPagerListener(Spinner spinner) {
            this.spinner = spinner;
        }

        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            spinner.setSelection(position, true);
            Log.i(TAG, "onPageSelected: page changed to " + position);
        }
    }

    static class SpinnerListener implements AdapterView.OnItemSelectedListener {
        ViewPager pager;

        SpinnerListener(ViewPager pager) {
            this.pager = pager;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            pager.setCurrentItem(position, true);
            Log.i(TAG, "onItemSelected: spinner changed to " + position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}
