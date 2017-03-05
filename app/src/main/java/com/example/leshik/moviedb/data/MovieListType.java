package com.example.leshik.moviedb.data;

/**
 * Created by alex on 3/5/17.
 */

public enum MovieListType {
    Popular(0),
    Toprated(1),
    Favorite(2);

    private int index;

    MovieListType(int i) {
        index = i;
    }

    public int getIndex() {
        return index;
    }
}
