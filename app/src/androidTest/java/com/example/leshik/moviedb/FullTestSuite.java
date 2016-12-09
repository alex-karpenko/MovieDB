package com.example.leshik.moviedb;

import android.test.suitebuilder.TestSuiteBuilder;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.example.leshik.moviedb.model.TestDb;

// Runs all unit tests.
@RunWith(Suite.class)
@Suite.SuiteClasses({TestDb.class
    })
public class UnitTestSuite {}
