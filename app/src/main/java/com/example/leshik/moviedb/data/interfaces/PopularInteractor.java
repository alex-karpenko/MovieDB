package com.example.leshik.moviedb.data.interfaces;

import com.example.leshik.moviedb.data.model.Movie;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by Leshik on 17.02.2017.
 */

public interface PopularInteractor {
    Observable<List<Movie>> getPopularList(int lastPage);
}
