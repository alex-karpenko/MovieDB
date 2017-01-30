package com.example.leshik.moviedb;

import com.example.leshik.moviedb.model.TestDb;
import com.example.leshik.moviedb.model.TestProvider;
import com.example.leshik.moviedb.model.TestUriMatcher;
import com.example.leshik.moviedb.service.TestCacheUpdateService;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

// Runs all unit tests.
@RunWith(Suite.class)
@Suite.SuiteClasses({
        TestDb.class,
        TestUriMatcher.class,
        TestProvider.class,
        TestCacheUpdateService.class
})
public class UnitTestSuite {
}
