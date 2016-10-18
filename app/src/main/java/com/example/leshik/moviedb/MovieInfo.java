package com.example.leshik.moviedb;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Leshik on 16.10.2016.
 */

public class MovieInfo implements Parcelable {
    private long id;
    private String originalTitle;
    private String overviewText;
    private double voteAverage;
    private String releaseDate;
    private String posterPath;

    MovieInfo(long id, String originalTitle, String overview, String releaseDate, double voteAverage, String posterPath) {
        this.id = id;
        this.originalTitle = originalTitle;
        this.overviewText = overview;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.posterPath = posterPath;
    }

    protected MovieInfo(Parcel in) {
        id = in.readLong();
        originalTitle = in.readString();
        overviewText = in.readString();
        voteAverage = in.readDouble();
        releaseDate = in.readString();
        posterPath = in.readString();
    }

    public static final Creator<MovieInfo> CREATOR = new Creator<MovieInfo>() {
        @Override
        public MovieInfo createFromParcel(Parcel in) {
            return new MovieInfo(in);
        }

        @Override
        public MovieInfo[] newArray(int size) {
            return new MovieInfo[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(originalTitle);
        parcel.writeString(overviewText);
        parcel.writeDouble(voteAverage);
        parcel.writeString(releaseDate);
        parcel.writeString(posterPath);
    }
}
