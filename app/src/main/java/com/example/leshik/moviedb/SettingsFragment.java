package com.example.leshik.moviedb;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SettingsFragment.
     */
    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add 'general' preferences, defined in the XML file
        addPreferencesFromResource(R.xml.pref_general);
        // For all preferences, attach an OnPreferenceChangeListener so the UI summary can be
        // updated when the preference changes.
        // Now only one
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_theme_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_cache_key)));
    }

    /**
     * Attaches a listener so the summary is always updated with the preference value.
     * Also fires the listener once, to initialize the summary (so it shows up before the value
     * is changed.)
     */
    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);

        // Trigger the listener immediately with the preference's
        // current value.
        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            preference.setSummary(stringValue);
        }

        if (preference.getKey().equals(getString(R.string.pref_theme_key))) {
            // TODO: change current theme
            int newTheme = R.style.AppThemeDark;
            if (stringValue.equals(getString(R.string.pref_theme_dark)))
                newTheme = R.style.AppThemeDark;
            else if (stringValue.equals(getString(R.string.pref_theme_light)))
                newTheme = R.style.AppThemeLight;
            getActivity().getApplicationContext().setTheme(newTheme);

        } else if (preference.getKey().equals(getString(R.string.pref_cache_key))) {
            // TODO: change cache update interval
            long newInterval = Long.valueOf(stringValue);
            newInterval = newInterval * 60 * 60 * 1000; // convert hours to milliseconds
            Utils.setCacheUpdateInterval(newInterval);
        }
        return true;
    }
}
