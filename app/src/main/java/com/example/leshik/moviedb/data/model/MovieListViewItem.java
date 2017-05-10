package com.example.leshik.moviedb.data.model;

/**
 * Created by alex on 3/30/17.
 */

public class MovieListViewItem {
    public long movieId;
    public int listPosition;
    public String posterPath;
    public String title;
    public float voteAverage;

    public MovieListViewItem(Movie movie, int position) {
        if (movie == null) throw new IllegalArgumentException("Movie cannot be null");
        if (position < 0) throw new IllegalArgumentException("Movie`s position cannot be negative");

        movieId = movie.getMovieId();
        listPosition = position;
        posterPath = movie.getPosterPath();
        title = movie.getOriginalTitle();
        voteAverage = movie.getVoteAverage();
    }
}
