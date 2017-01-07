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

public class MainActivity extends AppCompatActivity implements MovieListFragment.Callback {
    public static String[] tabFragmentNames;
    MainPagerAdapter mPagerAdapter;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Every time when activity created - update configuration from the TMDB
        Utils.basePosterUrl = Utils.getStringCachePreference(this, R.string.base_potser_url);
        Utils.basePosterSecureUrl = Utils.getStringCachePreference(this, R.string.base_potser_secure_url);
        CacheUpdateService.startActionUpdateConfiguration(this);

        setContentView(R.layout.activity_main);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);

        mPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.main_pager);
        mViewPager.setAdapter(mPagerAdapter);

        // Get list of tab's titles from resources
        Resources res = getResources();
        tabFragmentNames = res.getStringArray(R.array.main_tab_names);

        // Assign pager to tab layout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.main_tabs);
        tabLayout.setupWithViewPager(mViewPager);
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
        Intent intent = new Intent(this, DetailActivity.class)
                .setData(movieUri);
        // If Lollipop or higher - start activity with image animation
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(this, posterView, getString(R.string.poster_image));
            startActivity(intent, options.toBundle());
        } else {
            // If lower then Lollipop - simple start detail activity
            startActivity(intent);
        }
    }
}
