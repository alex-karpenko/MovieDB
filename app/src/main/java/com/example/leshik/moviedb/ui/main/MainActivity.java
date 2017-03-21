package com.example.leshik.moviedb.ui.main;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.leshik.moviedb.R;
import com.example.leshik.moviedb.data.PreferenceStorage;
import com.example.leshik.moviedb.data.interfaces.PreferenceInterface;
import com.example.leshik.moviedb.ui.details.DetailActivity;
import com.example.leshik.moviedb.ui.details.DetailFragment;
import com.example.leshik.moviedb.ui.poster.FullPosterActivity;
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

public class MainActivity extends AppCompatActivity implements MovieListFragment.Callback, DetailFragment.Callback {
    private static final String TAG = "MainActivity";
    // tags to saved state bundle
    private static final String STATE_CURRENT_PAGE = "STATE_CURRENT_PAGE";
    private static final String STATE_SELECTED_MOVIE_ID = "STATE_SELECTED_MOVIE_ID";

    // to store titles of the tabs (popular, top rated, favorites)
    public static String[] tabFragmentNames;

    // adapter to create fragments to pager
    private MainPagerAdapter mPagerAdapter;

    @BindView(R.id.main_frame)
    protected LinearLayout mMainFrame;
    @BindView(R.id.main_pager)
    protected ViewPager mViewPager;
    @BindView(R.id.main_tabs)
    protected TabLayout mTabLayout;
    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;

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

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // check if the details container is present - two pane layout was loaded
        if (findViewById(R.id.detail_container) != null) {
            ViewUtils.setTwoPane(true);
        } else ViewUtils.setTwoPane(false);

        setSupportActionBar(mToolbar);

        // create pager adapter and set it to the pager view
        mPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);

        // Get list of tab's titles from resources
        tabFragmentNames = getResources().getStringArray(R.array.main_tab_names);

        // Assign pager to tab layout
        mTabLayout.setupWithViewPager(mViewPager);

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
    public void onItemSelected(long movieId, ImageView posterView) {

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

    // callback method from details fragment - called when poster image pressed to start full poster view
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
