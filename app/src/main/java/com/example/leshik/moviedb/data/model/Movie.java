package com.example.leshik.moviedb.data.model;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by alex on 2/12/17.
 */

public class Movie extends RealmObject {
    @PrimaryKey
    private long movieId;

    private String originalTitle;
    private String overview;
    private String releaseDate;
    private Float voteAverage;
    private Float popularity;
    private String posterPath;
    private String homePage;
    private Boolean adult;
    private Boolean video;
    private Integer runTime;
    private Long lastUpdate;

    private RealmList<Video> videos;
    private RealmList<Review> reviews;

    private Integer favoritePosition;
    private Integer popularPosition;
    private Integer topratedPosition;

    public void updateNullFields(Movie movie) {
        if (movie != null) {
            if (originalTitle == null) originalTitle = movie.getOriginalTitle();
            if (overview == null) originalTitle = movie.getOverview();
            if (releaseDate == null) releaseDate = movie.getReleaseDate();
            if (voteAverage == null) voteAverage = movie.getVoteAverage();
            if (popularity == null) popularity = movie.getPopularity();
            if (posterPath == null) posterPath = movie.getPosterPath();
            if (homePage == null) homePage = movie.getHomePage();
            if (adult == null) adult = movie.getAdult();
            if (video == null) video = movie.getVideo();
            if (runTime == null) runTime = movie.getRunTime();
            if (favoritePosition == null) favoritePosition = movie.getFavoritePosition();
            if (popularPosition == null) popularPosition = movie.getPopularPosition();
            if (topratedPosition == null) topratedPosition = movie.getTopratedPosition();
            if (videos == null || videos.size() == 0) videos = movie.getVideos();
            if (reviews == null || reviews.size() == 0) reviews = movie.getReviews();
        }
    }

    public long getMovieId() {
        return movieId;
    }

    public void setMovieId(long movieId) {
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

    public Integer getFavoritePosition() {
        return favoritePosition;
    }

    public void setFavoritePosition(Integer favoritePosition) {
        this.favoritePosition = favoritePosition;
    }

    public Integer getPopularPosition() {
        return popularPosition;
    }

    public void setPopularPosition(Integer popularPosition) {
        this.popularPosition = popularPosition;
    }

    public Integer getTopratedPosition() {
        return topratedPosition;
    }

    public void setTopratedPosition(Integer topratedPosition) {
        this.topratedPosition = topratedPosition;
    }

    public RealmList<Video> getVideos() {
        return videos;
    }

    public void setVideos(RealmList<Video> videos) {
        this.videos = videos;
    }

    public RealmList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(RealmList<Review> reviews) {
        this.reviews = reviews;
    }
}
