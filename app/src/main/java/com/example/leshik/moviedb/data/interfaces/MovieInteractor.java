package com.example.leshik.moviedb.data.interfaces;

import com.example.leshik.moviedb.data.model.Movie;

import io.reactivex.Observable;

/**
 * Created by alex on 2/14/17.
 */

public interface MovieInteractor {
    class MovieRequest {
        private long movieId;
        private boolean forceRefresh;
        private boolean invertFavorite;

        public MovieRequest(long movieId, boolean forceRefresh, boolean invertFavorite) {
            this.movieId = movieId;
            this.forceRefresh = forceRefresh;
            this.invertFavorite = invertFavorite;
        }

        public long getMovieId() {
            return movieId;
        }

        public boolean isForceRefresh() {
            return forceRefresh;
        }

        public boolean isInvertFavorite() {
            return invertFavorite;
        }
    }

    Observable<Movie> getMovieObservable();
    void sendRequest(MovieRequest request);
}
