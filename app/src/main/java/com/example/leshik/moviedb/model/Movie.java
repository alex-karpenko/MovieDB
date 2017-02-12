package com.example.leshik.moviedb.model;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by alex on 2/12/17.
 */

public class Movie extends RealmObject {
    @PrimaryKey
    private int movieId;

    @Required
    private String originalTitle;
    @Required
    private String overview;
    @Required
    private String releaseDate;
    @Required
    private Float voteAverage;
    private Float popularity;
    @Required
    private String posterPath;
    private String homePage;
    private Boolean adult;
    private Boolean video;
    private Integer runTime;
    private Long lastUpdate;


    public void updateNullFieldsFromExist(Realm realm) {
        Movie movie = realm.where(Movie.class).equalTo("movieId", getMovieId()).findFirst();
        if (movie != null) {
            if (originalTitle == null) originalTitle = movie.getOriginalTitle();
            if (overview == null) originalTitle = movie.getOriginalTitle();
            if (releaseDate == null) releaseDate = movie.getReleaseDate();
            if (voteAverage == null) voteAverage = movie.getVoteAverage();
            if (popularity == null) popularity = movie.getPopularity();
            if (posterPath == null) posterPath = movie.getPosterPath();
            if (homePage == null) homePage = movie.getHomePage();
            if (adult == null) adult = movie.getAdult();
            if (video == null) video = movie.getVideo();
            if (runTime == null) runTime = movie.getRunTime();
        }
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Float getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Float voteAverage) {
        this.voteAverage = voteAverage;
    }

    public Float getPopularity() {
        return popularity;
    }

    public void setPopularity(Float popularity) {
        this.popularity = popularity;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public Boolean getAdult() {
        return adult;
    }

    public void setAdult(Boolean adult) {
        this.adult = adult;
    }

    public Boolean getVideo() {
        return video;
    }

    public void setVideo(Boolean video) {
        this.video = video;
    }

    public Integer getRunTime() {
        return runTime;
    }

    public void setRunTime(Integer runTime) {
        this.runTime = runTime;
    }

    public Long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
