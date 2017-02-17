package com.example.leshik.moviedb.data;

import android.content.Context;
import android.util.Log;

import com.example.leshik.moviedb.Utils;
import com.example.leshik.moviedb.data.interfaces.MovieInteractor;
import com.example.leshik.moviedb.data.model.Movie;
import com.example.leshik.moviedb.data.model.Review;
import com.example.leshik.moviedb.data.model.Video;

import java.util.Calendar;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Leshik on 17.02.2017.
 */

public class MovieRepository implements MovieInteractor {
    private static final String TAG = "MovieRepository";
    private DataStorage mDataStorage;

    public MovieRepository(Context context) {
        mDataStorage = new DataStorage(context);
    }

    @Override
    public Observable<Movie> getMovie(final long movieId, boolean forceReload) {
        Observable<Movie> movieFromApi;
        Movie movieFromDb;
        Observable<Movie> returnObservable;
        long currentTime = Calendar.getInstance().getTimeInMillis();
        long cacheUpdateInterval = Utils.getCacheUpdateInterval();

        movieFromDb = mDataStorage.readMovieFromDb(movieId);

        movieFromApi = mDataStorage.readMovieFromApi(movieId)
                .zipWith(mDataStorage.readVideoListFromApi(movieId), new BiFunction<Movie, List<Video>, Movie>() {
                    @Override
                    public Movie apply(Movie movie, List<Video> videos) throws Exception {
                        movie.setVideos(videos);
                        return movie;
                    }
                })
                .zipWith(mDataStorage.readReviewListFromApi(movieId), new BiFunction<Movie, List<Review>, Movie>() {
                    @Override
                    public Movie apply(Movie movie, List<Review> reviews) throws Exception {
                        movie.setReviews(reviews);
                        return movie;
                    }
                })
                .doOnNext(new Consumer<Movie>() {
                    @Override
                    public void accept(Movie movie) throws Exception {
                        mDataStorage.writeMovieToDb(movie);
                    }
                });

        if (movieFromDb == null || forceReload) {
            // 1) cache does not exist
            // 2) forced cache reloading
            //   - return api response only
            Log.i(TAG, "getMovie: from API");
            returnObservable = movieFromApi;
        } else if (((movieFromDb.getLastUpdate() + cacheUpdateInterval) <= currentTime && cacheUpdateInterval > 0)
                || (movieFromDb.getLastUpdate() <= 0 && cacheUpdateInterval <= 0)) {
            // 1) cache exist but expired
            // 2) cache has never been updated and update interval is "never"
            //    - return cache data, and refresh cache from api
            Log.i(TAG, "getMovie: from DB+API");
            returnObservable = movieFromApi
                    .mergeWith(Observable.just(movieFromDb));
        } else {
            // cache exist and fresh - return data from db only
            Log.i(TAG, "getMovie: from DB");
            returnObservable = Observable.just(movieFromDb);
        }

        return returnObservable.subscribeOn(Schedulers.io());
    }

    @Override
    public void setFavoriteFlag(final long movieId, final boolean isFavorite) {
        mDataStorage.setFavoriteFlag(movieId, isFavorite);
    }

}
