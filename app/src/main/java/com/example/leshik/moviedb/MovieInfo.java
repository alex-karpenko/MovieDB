package com.example.leshik.moviedb;

/**
 * Created by Leshik on 16.10.2016.
 */

public class MovieInfo {
    private long id;
    private String originalTitle;
    private String overviewText;
    private double voteAverage;
    private String releaseDate;
    private String posterPath;

    MovieInfo(long id, String originalTitle, String overview, String releaseDate, double voteAverage, String posterPath) {
        this.id=id;
        this.originalTitle=originalTitle;
        this.overviewText=overview;
        this.releaseDate=releaseDate;
        this.voteAverage=voteAverage;
        this.posterPath=posterPath;
    }

    public long getId() {
        return id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getOverviewText() {
        return overviewText;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getPosterPath() {
        return posterPath;
    }
}
