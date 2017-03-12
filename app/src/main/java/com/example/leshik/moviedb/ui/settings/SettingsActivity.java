package com.example.leshik.moviedb.ui.settings;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.leshik.moviedb.R;
import com.example.leshik.moviedb.data.PreferenceStorage;
import com.example.leshik.moviedb.data.interfaces.PreferenceInterface;
import com.example.leshik.moviedb.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An activity class that presents a set of application settings.
 * Now we use only one setting - sorting order
 */
public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.settings_toolbar)
    protected Toolbar mToolbar;

    private PreferenceInterface prefStorage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        prefStorage = PreferenceStorage.getInstance(this.getApplicationContext());
        Utils.applyTheme(this, prefStorage.getTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        getFragmentManager().beginTransaction()
                .replace(R.id.settings_container, SettingsFragment.newInstance())
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
