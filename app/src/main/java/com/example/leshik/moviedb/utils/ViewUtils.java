package com.example.leshik.moviedb.utils;

import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.leshik.moviedb.R;
import com.example.leshik.moviedb.data.MovieListType;
import com.example.leshik.moviedb.ui.main.MovieListAdapter;

/**
 * Utility class for provide some common (project-wide) methods, variables and constants
 */

public final class ViewUtils {
    private static final String TAG = "ViewUtils";
    // is main screen has two panes
    private static boolean twoPane = false;
    // Lists' adapters state
    private static MovieListAdapter.AdapterState movieListAdapterStates[] = {null, null, null, null}; // 4 pages

    // private constructor to avoid creation on instance
    private ViewUtils() {
    }

    // calculate number of GridLayout columns based on screen width
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        Log.i(TAG, "calculateNoOfColumns: width=" + displayMetrics.widthPixels + "px, height=" + displayMetrics.heightPixels + "px, density=" + displayMetrics.density);
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        if (isTwoPane()) {
            dpWidth = dpWidth / 2;
        }
        int noOfColumns = (int) (dpWidth / 180);
        if (noOfColumns == 1) noOfColumns = 2;

        return noOfColumns;
    }

    public static MovieListAdapter.AdapterState getMovieListAdapterState(MovieListType listType) {
        return movieListAdapterStates[listType.getIndex()];
    }

    public static void setMovieListAdapterState(MovieListType listType, MovieListAdapter.AdapterState state) {
        movieListAdapterStates[listType.getIndex()] = state;
    }

    public static int getScreenPxWidth(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    public static int getScreenPxHeight(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }

    public static int getScreenDpWidth(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) (displayMetrics.widthPixels / displayMetrics.density);
    }

    public static int getScreenDpHeight(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) (displayMetrics.heightPixels / displayMetrics.density);
    }

    // update favorite icon in the menu
    public static void setFavoriteIcon(boolean flag, Menu menu) {
        int favIcon;
        if (flag) favIcon = R.drawable.ic_favorite_black_dark;
        else favIcon = R.drawable.ic_favorite_outline_dark;
        if (menu != null) {
            MenuItem favMenuItem = menu.findItem(R.id.action_favorite);
            favMenuItem.setIcon(favIcon);
        }
    }

    // getter/setter for two pane flag
    public static boolean isTwoPane() {
        return twoPane;
    }

    public static void setTwoPane(boolean twoPane) {
        ViewUtils.twoPane = twoPane;
    }

    // create and return share action intent
    // TODO: 29.01.2017 develop normal method for create fine html-based letter
    public static Intent getShareIntent(Context context, String title, String poster) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        // letter subject
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.share_action_subject) + title);

        // letter content
        String content = new StringBuilder()
                .append(title + "\n\n")
                .append(poster)
                .toString();
        intent.putExtra(Intent.EXTRA_TEXT, content);
        intent.setType("text/*");

        return intent;
    }
}
