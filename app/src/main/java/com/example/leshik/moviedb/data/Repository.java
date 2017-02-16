package com.example.leshik.moviedb.data;

import android.content.Context;

import com.example.leshik.moviedb.Utils;
import com.example.leshik.moviedb.data.api.MovieResponse;
import com.example.leshik.moviedb.data.interfaces.ApiService;
import com.example.leshik.moviedb.data.interfaces.MovieInteractor;
import com.example.leshik.moviedb.data.model.Movie;

import io.reactivex.Observable;
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
    public Observable<Movie> getMovie(long movieId) {
        Observable<Movie> movieFromApi;
        Movie movieFromDb;

        movieFromDb = readMovieFromDb(movieId);
        movieFromApi = readMovieFromApi(movieId)
                .doOnNext(new Consumer<Movie>() {
                    @Override
                    public void accept(Movie movie) throws Exception {
                        writeMovieToDb(movie);
                    }
                });

        if (movieFromDb != null) {
            movieFromApi = movieFromApi.mergeWith(Observable.just(movieFromDb).subscribeOn(Schedulers.io()));
        }

        return movieFromApi;
    }

    Movie readMovieFromDb(long movieId) {
        Realm realm = DataUtils.getRealmInstance(context);
        Movie movie = findMovieFromDb(realm, movieId);
        realm.close();

        return movie;
    }

    Movie findMovieFromDb(Realm realm, long movieId) {
        Movie movie = realm.where(Movie.class).equalTo("movieId", movieId).findFirst();
        if (movie != null && movie.isValid()) return realm.copyFromRealm(movie);
        else return null;
    }

    long writeMovieToDb(final Movie newMovie) {
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

    Observable<Movie> readMovieFromApi(long movieId) {
        ApiService service = DataUtils.getServiceInstance(DataUtils.getRetrofitInstance(Utils.getBaseApiUrl()));

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
}
