package com.example.leshik.moviedb.data.interfaces;

import com.example.leshik.moviedb.data.model.Video;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by alex on 2/14/17.
 */

public interface VideosInteractor {
    Observable<List<Video>> getVideoList(long movieId);
    Observable<Video> getVideo(String videoId);
}
