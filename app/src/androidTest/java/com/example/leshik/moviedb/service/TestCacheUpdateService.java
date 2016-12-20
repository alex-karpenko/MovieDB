package com.example.leshik.moviedb.service;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ServiceTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Created by Leshik on 09.12.2016.
 */

@RunWith(AndroidJUnit4.class)
public class TestCacheUpdateService {
    public static final String LOG_TAG = TestCacheUpdateService.class.getSimpleName();

    private static final Context mContext = InstrumentationRegistry.getTargetContext();

    @Rule
    public final ServiceTestRule mServiceRule = new ServiceTestRule();

    @Test
    public void test01_UpdateConfiguration() throws Throwable {
        Intent intent = new Intent(mContext, CacheUpdateService.class);
        intent.setAction(CacheUpdateService.ACTION_UPDATE_CONFIGURATION);
        mServiceRule.startService(intent);
    }
}
