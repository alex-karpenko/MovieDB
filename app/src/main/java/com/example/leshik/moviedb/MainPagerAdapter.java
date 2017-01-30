package com.example.leshik.moviedb;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Leshik on 07.01.2017.
 * Adapter class to fill main screen pager with fragments
 * (popular, top rated and favorites)
 */

class MainPagerAdapter extends FragmentPagerAdapter {
    MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new MovieListFragment();
        Bundle args = new Bundle();
        args.putInt(MovieListFragment.ARG_FRAGMENT_TYPE, position);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return MainActivity.tabFragmentNames[position];
    }
}
