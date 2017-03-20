package com.example.leshik.moviedb.ui.details;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.leshik.moviedb.R;
import com.example.leshik.moviedb.data.MovieRepository;
import com.example.leshik.moviedb.data.PreferenceStorage;
import com.example.leshik.moviedb.data.interfaces.PreferenceInterface;
import com.example.leshik.moviedb.data.model.Movie;
import com.example.leshik.moviedb.data.model.Review;
import com.example.leshik.moviedb.data.model.Video;
import com.example.leshik.moviedb.ui.viewmodels.MovieViewModel;
import com.example.leshik.moviedb.utils.FirebaseUtils;
import com.example.leshik.moviedb.utils.ViewUtils;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

/**
 * Fragment class with detail info about movie
 * Information. From intent gets URI with movie
 */
public class DetailFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "DetailFragment";
    // fragment args
    public static final String ARG_MOVIE_ID = "ARG_MOVIE_ID";
    private static final String YOUTUBE_BASE_URL = "http://www.youtube.com/watch?v=";
    private static final String YOUTUBE_BASE_CONTENT = "vnd.youtube:";
    // state variables
    private long movieId;
    private Movie lastMovie;

    // references for all views
    @BindView(R.id.poster_image)
    protected ImageView mPosterImage;
    @BindView(R.id.detail_title)
    protected TextView mTitleText;
    @BindView(R.id.detail_released)
    protected TextView mReleasedText;
    @BindView(R.id.detail_runtime)
    protected TextView mRuntimeText;
    @BindView(R.id.detail_rating)
    protected TextView mRatingText;
    @BindView(R.id.detail_overview)
    protected TextView mOverviewText;
    @BindView(R.id.detail_homepage)
    protected TextView mHomepageText;

    @BindView(R.id.swiperefresh)
    protected SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.videos_layout)
    protected LinearLayout mVideosListLayout;
    @BindView(R.id.reviews_layout)
    protected LinearLayout mReviewsListLayout;

    @BindView(R.id.videos_list)
    protected TableLayout mVideosListTable;
    @BindView(R.id.reviews_list)
    protected TableLayout mReviewsListTable;

    private Unbinder unbinder;

    private Menu mMenu;
    private ShareActionProvider mShareActionProvider;

    // variables to store data for updating share action intent
    private String mPosterName;
    private String mMovieTitle;

    // favorite flag
    private boolean isFavorite;

    MovieViewModel mViewModel;
    CompositeDisposable subscription = new CompositeDisposable();

    PreferenceInterface prefStorage;

    private FirebaseAnalytics mFirebaseAnalytics;

    public DetailFragment() {
        // Required empty public constructor
    }

    public static DetailFragment newInstance(long movieId) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();

        args.putLong(ARG_MOVIE_ID, movieId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // restore movie uri
        if (savedInstanceState != null) {
            movieId = savedInstanceState.getLong(ARG_MOVIE_ID);
        }

        prefStorage = PreferenceStorage.getInstance(getContext().getApplicationContext());

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // put movie uri into state bundle
        outState.putLong(ARG_MOVIE_ID, movieId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get bundle with args (URI)
        Bundle args = getArguments();
        if (args != null) movieId = args.getLong(ARG_MOVIE_ID);

        // init vew model
        mViewModel = new MovieViewModel(movieId, new MovieRepository(getActivity().getApplicationContext()));

        // Inflate fragment
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        mSwipeRefreshLayout.setOnRefreshListener(this);

        isFavorite = false;

        // set onClick listener for poster image
        // click on poster image - call to full poster image view via callback in the activity
        mPosterImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPosterName != null) {
                    ((DetailFragment.Callback) getContext()).onImageClicked(movieId, (ImageView) v);
                }
            }
        });

        subscribeToMovie();

        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM,
                FirebaseUtils.createAnalyticsSelectBundle(TAG, "Create Detail Fragment", "Movie Details"));

        return rootView;
    }

    @Override
    public void onDestroyView() {
        unsubscribeFromMovie();
        unbinder.unbind();

        super.onDestroyView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail_fragment, menu);

        // store ref. to the menu to update favorite mark
        mMenu = menu;

        // and update share action intent
        if (menu != null) {
            mShareActionProvider = new ShareActionProvider(getContext());
            MenuItemCompat.setActionProvider(menu.findItem(R.id.action_share), mShareActionProvider);
            updateShareAction(mMovieTitle, mPosterName);

            ViewUtils.setFavoriteIcon(isFavorite, mMenu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            // set refresh mark
            mSwipeRefreshLayout.setRefreshing(true);
            // and start update action
            refreshCurrentMovie();
            return true;
        }
        if (id == R.id.action_favorite) {
            // reverse mark flag
            isFavorite = !isFavorite;
            // and change mark on the menu with dependence on the theme
            ViewUtils.setFavoriteIcon(isFavorite, mMenu);
            // update favorite flag in the db
            mViewModel.invertFavorite();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // swipe refresh layout callback
    @Override
    public void onRefresh() {
        refreshCurrentMovie();
    }

    /**
     * Method to update share action intent after loading movie data
     *
     * @param title
     * @param poster
     */
    void updateShareAction(String title, String poster) {
        // create intent
        Intent myShareIntent = ViewUtils.getShareIntent(getContext(), title, prefStorage.getPosterFullUri(poster).toString());
        // set intent into provider
        if (mShareActionProvider != null) mShareActionProvider.setShareIntent(myShareIntent);
    }

    private void updateUi(Movie movie) {
        Log.i(TAG, "updateUi: +");

        // stop refresh circle
        mSwipeRefreshLayout.setRefreshing(false);

        // update variables for share action provider
        mPosterName = movie.getPosterPath();
        mMovieTitle = movie.getOriginalTitle();

        if (mMenu != null)
            updateShareAction(mMovieTitle, mPosterName);

        if (isPosterChanged(movie)) {
            // load poster image
            Picasso.with(getActivity())
                    .load(prefStorage.getPosterSmallUri(mPosterName))
                    .into(mPosterImage);
        }

        // Setting all view's content
        mTitleText.setText(movie.getOriginalTitle());
        mReleasedText.setText(movie.getReleaseDate());

        if (movie.getRunTime() > 0) {
            mRuntimeText.setText(getString(R.string.runtime_format_str, movie.getRunTime()));
        } else {
            mRuntimeText.setText(getString(R.string.null_runtime_format_str));
        }

        mRatingText.setText(getString(R.string.rating_format_str, movie.getVoteAverage()));

        // Set homepage link, if present
        String homePage = movie.getHomePage();
        if (homePage != null && homePage.length() > 0) {
            mHomepageText.setText(getString(R.string.homepage_format_str, homePage));
            mHomepageText.setVisibility(View.VISIBLE);
        } else {
            mHomepageText.setVisibility(View.GONE);
        }

        mOverviewText.setText(movie.getOverview());

        // Inflater for creating reviews and videos lists
        LayoutInflater inflater = LayoutInflater.from(getContext());
        // Review list
        mReviewsListTable.removeAllViews();
        if (movie.getReviews() != null && movie.getReviews().size() > 0) {
            for (Review r : movie.getReviews()) {
                View v = inflater.inflate(R.layout.reviews_list_item, mReviewsListTable, false);

                TextView authorView = ButterKnife.findById(v, R.id.reviews_list_item_author);
                ExpandableTextView contentView = ButterKnife.findById(v, R.id.reviews_list_item_content);

                authorView.setText(r.getAuthor());
                contentView.setText(r.getContent());

                mReviewsListTable.addView(v);
            }

            mReviewsListLayout.setVisibility(View.VISIBLE);
        } else mReviewsListLayout.setVisibility(View.GONE);

        // Video list
        mVideosListTable.removeAllViews();
        if (movie.getVideos() != null && movie.getVideos().size() > 0) {
            for (Video video : movie.getVideos()) {
                View v = inflater.inflate(R.layout.videos_list_item, mVideosListTable, false);

                TextView titleView = ButterKnife.findById(v, R.id.videos_list_item_title);
                titleView.setText(video.getName());

                // setup listener
                v.setTag(video.getKey());
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // start video watching, key of the video we extract from view's tag
                        watchYoutubeVideo((String) view.getTag());
                    }
                });

                mVideosListTable.addView(v);
            }
            mVideosListLayout.setVisibility(View.VISIBLE);
        } else mVideosListLayout.setVisibility(View.GONE);

        // Favorite mark
        isFavorite = false;
        if (movie.getFavoritePosition() != null && movie.getFavoritePosition() >= 0) {
            isFavorite = true;
        }
        // and update mark
        ViewUtils.setFavoriteIcon(isFavorite, mMenu);

        lastMovie = movie;
    }

    private boolean isPosterChanged(Movie newMovie) {
        return lastMovie == null || !lastMovie.getPosterPath().equals(newMovie.getPosterPath());
    }

    private void watchYoutubeVideo(String id) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_BASE_CONTENT + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(YOUTUBE_BASE_URL + id));
        try {
            getContext().startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            getContext().startActivity(webIntent);
        }
    }

    private void subscribeToMovie() {
        subscription.add(mViewModel.getMovie()
                .subscribe(new Consumer<Movie>() {
                    @Override
                    public void accept(Movie movie) throws Exception {
                        updateUi(movie);
                    }
                }));
    }

    private void unsubscribeFromMovie() {
        subscription.dispose();
    }

    // starting intent services to update cache tables
    void refreshCurrentMovie() {
        boolean isRefreshStarted = mViewModel.forceRefresh();
        if (!isRefreshStarted) mSwipeRefreshLayout.setRefreshing(false);
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of poster image
     * clicked.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        void onImageClicked(long movieId, ImageView posterView);
    }
}