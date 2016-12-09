package com.example.leshik.moviedb;

import com.example.leshik.moviedb.model.TestDb;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

// Runs all unit tests.
@RunWith(Suite.class)
@Suite.SuiteClasses({TestDb.class
    })
public class UnitTestSuite {}
