package com.example.leshik.moviedb.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;

import com.example.leshik.moviedb.R;

/**
 * Utility class for provide some common (project-wide) methods, variables and constants
 */

public final class ViewUtils {
    private static final String TAG = "ViewUtils";
    // is main screen has two panes
    private static boolean twoPane = false;
    // Current favorite icons
    private static int iconFavoriteBlack = R.drawable.ic_favorite_black_light;
    private static int iconFavoriteOutline = R.drawable.ic_favorite_outline_light;
    // flag to know is activity needs to restart after theme switch
    private static boolean restartActivity = false;

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

    // change favorite icons resource IDs based on the current theme
    static void setupThemeIcons(Context context) {
        TypedValue val = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.isLightTheme, val, true);
        if (val.type == TypedValue.TYPE_INT_BOOLEAN && val.data == 0) {
            // Dark
            iconFavoriteBlack = R.drawable.ic_favorite_black_dark;
            iconFavoriteOutline = R.drawable.ic_favorite_outline_dark;
        } else {
            // Light
            iconFavoriteBlack = R.drawable.ic_favorite_black_light;
            iconFavoriteOutline = R.drawable.ic_favorite_outline_light;
        }
    }

    // update favorite icon in the menu
    public static void setFavoriteIcon(boolean flag, Menu menu) {
        int favIcon;
        if (flag) favIcon = iconFavoriteBlack;
        else favIcon = iconFavoriteOutline;
        if (menu != null) {
            MenuItem favMenuItem = menu.findItem(R.id.action_favorite);
            favMenuItem.setIcon(favIcon);
        }
    }

    // apply current theme to the context and change icons set
    public static void applyTheme(Context context, int themeId) {
        context.setTheme(themeId);
        setupThemeIcons(context);
    }

    // set restart activity flag to schedule restart it
    public static void scheduleActivityRestart() {
        restartActivity = true;
    }

    // restart application if this was planned by setting restartActivity flag
    public static void restartActivityIfNeed(Activity context) {
        if (restartActivity) {
            restartActivity = false;
            // start "root" activity with ACTIVITY_CLEAR_TOP flag
            Intent intent = context.getBaseContext().getPackageManager().getLaunchIntentForPackage(context.getBaseContext().getPackageName());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
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
