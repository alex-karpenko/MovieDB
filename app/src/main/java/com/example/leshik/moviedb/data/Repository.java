package com.example.leshik.moviedb.data;

import android.content.Context;

import com.example.leshik.moviedb.Utils;
import com.example.leshik.moviedb.data.api.MovieResponse;
import com.example.leshik.moviedb.data.api.ReviewsResponse;
import com.example.leshik.moviedb.data.api.VideosResponse;
import com.example.leshik.moviedb.data.interfaces.ApiService;
import com.example.leshik.moviedb.data.interfaces.MovieInteractor;
import com.example.leshik.moviedb.data.model.Movie;
import com.example.leshik.moviedb.data.model.Review;
import com.example.leshik.moviedb.data.model.Video;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;

/**
 * Created by alex on 2/14/17.
 */

public class Repository implements MovieInteractor {
    private Context context;

    public Repository(Context context) {
        this.context = context;
    }

    @Override
    public Observable<Movie> getMovie(final long movieId) {
        Observable<Movie> movieFromApi;
        Movie movieFromDb;

        movieFromDb = readMovieFromDb(movieId);
        movieFromApi = readMovieFromApi(movieId)
                .zipWith(readVideoListFromApi(movieId), new BiFunction<Movie, List<Video>, Movie>() {
                    @Override
                    public Movie apply(Movie movie, List<Video> videos) throws Exception {
                        movie.setVideos(videos);
                        return movie;
                    }
                })
                .zipWith(readReviewListFromApi(movieId), new BiFunction<Movie, List<Review>, Movie>() {
                    @Override
                    public Movie apply(Movie movie, List<Review> reviews) throws Exception {
                        movie.setReviews(reviews);
                        return movie;
                    }
                })
                .doOnNext(new Consumer<Movie>() {
                    @Override
                    public void accept(Movie movie) throws Exception {
                        writeMovieToDb(movie);
                    }
                })
        ;

        if (movieFromDb != null) {
            movieFromApi = movieFromApi
                    .mergeWith(Observable.just(movieFromDb).subscribeOn(Schedulers.io()));
        }

        return movieFromApi;
    }

    private ApiService getApiServiceInstance() {
        return DataUtils.getServiceInstance(DataUtils.getRetrofitInstance(Utils.getBaseApiUrl()));
    }

    private Movie readMovieFromDb(long movieId) {
        Realm realm = DataUtils.getRealmInstance(context);
        Movie movie = findMovieFromDb(realm, movieId);
        realm.close();

        return movie;
    }

    private Movie findMovieFromDb(Realm realm, long movieId) {
        Movie movie = realm.where(Movie.class).equalTo("movieId", movieId).findFirst();
        if (movie != null && movie.isValid()) return realm.copyFromRealm(movie);
        else return null;
    }

    private long writeMovieToDb(final Movie newMovie) {
        Realm realm = DataUtils.getRealmInstance(context);

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm transactionRealm) {
                Movie movie = findMovieFromDb(transactionRealm, newMovie.getMovieId());
                if (movie != null) newMovie.updateNullFields(movie);
                transactionRealm.copyToRealmOrUpdate(newMovie);
            }
        });

        realm.close();

        return newMovie.getMovieId();
    }

    private Observable<Movie> readMovieFromApi(long movieId) {
        ApiService service = getApiServiceInstance();

        Observable<Movie> movie =
                service.getMovie(movieId, Utils.getApiKey())
                        .subscribeOn(Schedulers.io())
                        .map(new Function<MovieResponse, Movie>() {
                            @Override
                            public Movie apply(MovieResponse movieResponse) throws Exception {
                                return movieResponse.getMovieInstance();
                            }
                        });

        return movie;
    }

    private Observable<List<Video>> readVideoListFromApi(long movieId) {
        ApiService service = getApiServiceInstance();

        Observable<List<Video>> videoList =
                service.getVideos(movieId, Utils.getApiKey())
                        .subscribeOn(Schedulers.io())
                        .map(new Function<VideosResponse, List<Video>>() {
                            @Override
                            public List<Video> apply(VideosResponse videosResponse) throws Exception {
                                return videosResponse.getVideoListInstance();
                            }
                        });

        return videoList;
    }

    private Observable<List<Review>> readReviewListFromApi(final long movieId) {
        final ApiService service = getApiServiceInstance();
        final int totalPages = 0;

        Observable<List<Review>> reviewList =
                service.getReviews(movieId, Utils.getApiKey(), 1)
                        .subscribeOn(Schedulers.io())
                        .map(new Function<ReviewsResponse, List<Review>>() {
                            @Override
                            public List<Review> apply(ReviewsResponse reviewsResponse) throws Exception {
//                        totalPages = reviewsResponse.totalPages;
                                return reviewsResponse.getReviewListInstance();
                            }
                        });

        return reviewList;
    }
}
