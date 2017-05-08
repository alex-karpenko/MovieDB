package com.example.leshik.moviedb.data.model;

import java.util.Calendar;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by alex on 2/12/17.
 */

public class Movie extends RealmObject {
    @PrimaryKey
    private long movieId;

    private String originalTitle;
    private String overview;
    @Index
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

    @Index
    private long favoriteTimestamp;

    public boolean isEmpty() {
        return movieId == 0;
    }

    public void updateNullFields(Movie existentMovie) {
        if (existentMovie != null) {
            if (originalTitle == null) originalTitle = existentMovie.getOriginalTitle();
            if (overview == null) originalTitle = existentMovie.getOverview();
            if (releaseDate == null) releaseDate = existentMovie.getReleaseDate();
            if (voteAverage == null) voteAverage = existentMovie.getVoteAverage();
            if (popularity == null) popularity = existentMovie.getPopularity();
            if (posterPath == null) posterPath = existentMovie.getPosterPath();
            if (homePage == null) homePage = existentMovie.getHomePage();
            if (adult == null) adult = existentMovie.getAdult();
            if (video == null) video = existentMovie.getVideo();
            if (runTime == null) runTime = existentMovie.getRunTime();
            if (videos == null || videos.size() == 0) videos = existentMovie.getVideos();
            if (reviews == null || reviews.size() == 0) reviews = existentMovie.getReviews();
            if (favoriteTimestamp <= 0L) favoriteTimestamp = existentMovie.getFavoriteTimestamp();
            if (lastUpdate == null) lastUpdate = existentMovie.getLastUpdate();
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
        return runTime == null ? 0 : runTime;
    }

    public void setRunTime(Integer runTime) {
        this.runTime = runTime;
    }

    public Long getLastUpdate() {
        if (lastUpdate == null) return 0L;
        return lastUpdate;
    }

    public void setLastUpdate(Long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void invertFavorite() {
        if (isFavorite()) unsetFavorite();
        else setFavorite();
    }

    public boolean isFavorite() {
        return getFavoriteTimestamp() > 0L;
    }

    public void unsetFavorite() {
        setFavoriteTimestamp(0L);
    }

    public void setFavorite() {

        setFavoriteTimestamp(Calendar.getInstance().getTimeInMillis());
    }

    public long getFavoriteTimestamp() {
        return favoriteTimestamp;
    }

    public void setFavoriteTimestamp(long favoriteTimestamp) {
        this.favoriteTimestamp = favoriteTimestamp;
    }

    public RealmList<Video> getVideos() {
        return videos;
    }

    public void setVideos(RealmList<Video> videos) {
        this.videos = videos;
    }

    public void setVideos(List<Video> videos) {
        if (this.videos == null)
            this.videos = new RealmList<>();

        if (videos != null)
            for (Video v : videos)
                this.videos.add(v);
    }

    public RealmList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(RealmList<Review> reviews) {
        this.reviews = reviews;
    }

    public void setReviews(List<Review> reviews) {
        if (this.reviews == null)
            this.reviews = new RealmList<>();
        if (reviews != null)
            for (Review r : reviews) this.reviews.add(r);
    }

}
