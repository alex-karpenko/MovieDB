package com.example.leshik.moviedb.utils;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by alex on 3/17/17.
 */

public class FirebaseUtils {
    private FirebaseUtils() {
    }

    public static Bundle createAnalyticsSelectBundle(String id, String name, String type) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, type);

        return bundle;
    }

    public static Bundle createAnalyticsViewListBundle(String category) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, category);

        return bundle;
    }

    public static Bundle createAnalyticsViewItemBundle(String id, String name) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);

        return bundle;

    }
}
