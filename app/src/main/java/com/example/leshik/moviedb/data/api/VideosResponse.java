package com.example.leshik.moviedb.data.api;

/**
 * Created by Leshik on 08.01.2017.
 */

/**
 * template class to convert JSON answer from API to data class
 * use it to convert lists of videos of specific movie
 */

import com.example.leshik.moviedb.data.model.Video;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
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

public class VideosResponse {

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("results")
    @Expose
    public List<VideosResult> videosResults = null;

    public List<Video> getVideoListInstance() {
        List<Video> videoList = new ArrayList<>();

        for (VideosResult v : videosResults) {
            Video video = new Video();

            video.setMovieId(id);
            video.setVideoId(v.id);
            video.setKey(v.key);
            video.setName(v.name);
            video.setSite(v.site);
            video.setSize(v.size);
            video.setType(v.type);

            videoList.add(video);
        }

        return videoList;
    }

}
