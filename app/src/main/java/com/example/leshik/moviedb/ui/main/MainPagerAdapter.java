package com.example.leshik.moviedb.ui.main;

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
        // TODO: 3/7/17 Change this to use MovieListType enum
        Fragment fragment = MovieListFragment.newInstance(position);

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
