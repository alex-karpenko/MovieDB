package com.example.leshik.moviedb.data.model;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.Index;

/**
 * Created by alex on 4/29/17.
 * <p>
 * Class to represent movie list pages, obtained from REST API
 */

public class MovieListItem extends RealmObject {
    @Ignore
    public static final String LIST_TYPE_COLUMN = "listType";
    @Ignore
    public static final String PAGE_COLUMN = "page";
    @Ignore
    public static final String POSITION_COLUMN = "position";
    @Ignore
    public static final String MOVIE_ID_COLUMN = "movieId";

    @Index
    public int listType;
    @Index
    public int page;
    @Index
    public int position;
    @Index
    public long movieId;

    public MovieListItem() {
    }

    public MovieListItem(int listType, int page, int position, long movieId) {
        this.listType = listType;
        this.page = page;
        this.position = position;
        this.movieId = movieId;
    }

    public int getAbsolutePosition(int pageSize) {
        return pageSize * (page - 1) + position;
    }

    public boolean isEmpty() {
        return movieId <= 0L;
    }
}
