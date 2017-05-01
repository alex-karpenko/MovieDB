package com.example.leshik.moviedb.data.api;

/**
 * Created by Leshik on 19.12.2016.
 */

import com.example.leshik.moviedb.data.MovieListType;
import com.example.leshik.moviedb.data.model.Movie;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * template class to convert JSON answer from API to data class
 * use it to convert lists of popular and top rated movies
 */


class ListResult {
    @SerializedName("poster_path")
    @Expose
    public String posterPath;
    @SerializedName("adult")
    @Expose
    public Boolean adult;
    @SerializedName("overview")
    @Expose
    public String overview;
    @SerializedName("release_date")
    @Expose
    public String releaseDate;
    @SerializedName("genre_ids")
    @Expose
    public List<Integer> genreIds = null;
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("original_title")
    @Expose
    public String originalTitle;
    @SerializedName("original_language")
    @Expose
    public String originalLanguage;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("backdrop_path")
    @Expose
    public String backdropPath;
    @SerializedName("popularity")
    @Expose
    public Float popularity;
    @SerializedName("vote_count")
    @Expose
    public Integer voteCount;
    @SerializedName("video")
    @Expose
    public Boolean video;
    @SerializedName("vote_average")
    @Expose
    public Float voteAverage;

}

public class ListPageResponse {
    private static final int PAGE_SIZE = 20;

    @SerializedName("page")
    @Expose
    public Integer page;
    @SerializedName("results")
    @Expose
    public List<ListResult> listResults = null;
    @SerializedName("total_results")
    @Expose
    public Integer totalResults;
    @SerializedName("total_pages")
    @Expose
    public Integer totalPages;

    public List<Movie> getListPageInstance(MovieListType listType) {
        int startPosition = (page - 1) * PAGE_SIZE;
        List<Movie> returnList = new ArrayList<>();

        if (listResults != null) {
            for (ListResult r : listResults) {
                Movie movie = new Movie();

                movie.setMovieId(r.id);
                movie.setOriginalTitle(r.originalTitle);
                movie.setOverview(r.overview);
                movie.setReleaseDate(r.releaseDate);
                movie.setVoteAverage(r.voteAverage);
                movie.setPopularity(r.popularity);
                movie.setPosterPath(r.posterPath);
                movie.setHomePage(null);
                movie.setAdult(r.adult);
                movie.setVideo(r.video);
                movie.setRunTime(null);
                movie.setLastUpdate(null);
                movie.setVideos(null);
                movie.setReviews(null);
                movie.setFavoritePosition(null);
                movie.setPopularPosition(null);
                movie.setTopratedPosition(null);
                movie.setUpcomingPosition(null);

                switch (listType) {
                    case Popular:
                        movie.setPopularPosition(startPosition);
                        break;
                    case Toprated:
                        movie.setTopratedPosition(startPosition);
                        break;
                    case Upcoming:
                        movie.setUpcomingPosition(startPosition);
                        break;
                    default:
                        throw new IllegalArgumentException("ListPageResponse: instance type does not supported");
                }
                startPosition++;

                returnList.add(movie);
            }
        }

        return returnList;
    }
}
