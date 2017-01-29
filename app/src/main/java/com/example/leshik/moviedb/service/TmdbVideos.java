package com.example.leshik.moviedb.service;

/**
 * Created by Leshik on 08.01.2017.
 */

/**
 * template class to convert JSON answer from API to data class
 * use it to convert lists of videos of specific movie
 */

import android.content.ContentValues;

import com.example.leshik.moviedb.model.MoviesContract;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

class VideosResult {

    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("iso_639_1")
    @Expose
    public String iso6391;
    @SerializedName("iso_3166_1")
    @Expose
    public String iso31661;
    @SerializedName("key")
    @Expose
    public String key;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("site")
    @Expose
    public String site;
    @SerializedName("size")
    @Expose
    public Integer size;
    @SerializedName("type")
    @Expose
    public String type;

}

public class TmdbVideos {

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("results")
    @Expose
    public List<VideosResult> videosResults = null;

    // helper method to get a list of videos to insert via content provider
    public ContentValues[] getVideosContentValues() {
        if (videosResults == null) return null;

        int resultsSize = videosResults.size();
        if (resultsSize == 0) return null;

        ContentValues[] returnValues = new ContentValues[resultsSize];

        for (int i = 0; i < resultsSize; i++) {
            returnValues[i] = new ContentValues();
            returnValues[i].put(MoviesContract.Videos.COLUMN_NAME_MOVIE_ID, id);
            returnValues[i].put(MoviesContract.Videos.COLUMN_NAME_VIDEO_ID, videosResults.get(i).id);
            returnValues[i].put(MoviesContract.Videos.COLUMN_NAME_KEY, videosResults.get(i).key);
            returnValues[i].put(MoviesContract.Videos.COLUMN_NAME_NAME, videosResults.get(i).name);
            returnValues[i].put(MoviesContract.Videos.COLUMN_NAME_SITE, videosResults.get(i).site);
            returnValues[i].put(MoviesContract.Videos.COLUMN_NAME_SIZE, videosResults.get(i).size);
            returnValues[i].put(MoviesContract.Videos.COLUMN_NAME_TYPE, videosResults.get(i).type);
        }

        return returnValues;
    }
}
