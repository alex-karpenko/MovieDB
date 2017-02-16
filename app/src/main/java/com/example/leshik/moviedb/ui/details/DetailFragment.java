package com.example.leshik.moviedb.ui.details;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.leshik.moviedb.R;
import com.example.leshik.moviedb.Utils;
import com.example.leshik.moviedb.data.MoviesContract;
import com.example.leshik.moviedb.data.Repository;
import com.example.leshik.moviedb.data.interfaces.MovieInteractor;
import com.example.leshik.moviedb.data.model.Movie;
import com.example.leshik.moviedb.service.CacheUpdateService;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

/**
 * Fragment class with detail info about movie
 * Information. From intent gets URI with movie
 */
public class DetailFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "DetailFragment";
    // fragment args
    public static final String FRAGMENT_MOVIE_URI = "FRAGMENT_MOVIE_URI";
    // state variables
    private Uri mUri;
    private long movieId;

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

    private SimpleCursorAdapter mVideosListAdapter;
    private SimpleCursorAdapter mReviewsListAdapter;

    private Menu mMenu;
    private ShareActionProvider mShareActionProvider;

    // variables to store data for updating share action intent
    private String mPosterName;
    private String mMovieTitle;

    // favorite flag
    private boolean isFavorite;

    MovieInteractor mRepository;
    CompositeDisposable subscription = new CompositeDisposable();

    public DetailFragment() {
        // Required empty public constructor
    }

    public static DetailFragment newInstance(Uri uri) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();

        args.putParcelable(FRAGMENT_MOVIE_URI, uri);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // restore movie uri
        if (savedInstanceState != null) {
            mUri = savedInstanceState.getParcelable(FRAGMENT_MOVIE_URI);
        }

        mRepository = new Repository(getActivity().getApplicationContext());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // put movie uri into state bundle
        outState.putParcelable(FRAGMENT_MOVIE_URI, mUri);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get bundle with args (URI)
        Bundle args = getArguments();
        if (args != null) mUri = args.getParcelable(FRAGMENT_MOVIE_URI);
        if (mUri != null) movieId = ContentUris.parseId(mUri);

        // Inflate fragment
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        mSwipeRefreshLayout.setOnRefreshListener(this);

        // set videos list adapter with empty cursor
        mVideosListAdapter = new SimpleCursorAdapter(getContext(),
                R.layout.videos_list_item,
                null, // cursor
                new String[]{MoviesContract.Videos.COLUMN_NAME_NAME},
                new int[]{R.id.videos_list_item_frame}, // dest. view is root table (for create onClick listener for whole view)
                0);
        // we need separate view binder because our view is not a simple text view
        // and we need onClick listener to start video watching via youtube
        mVideosListAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int i) {
                if (i == MoviesContract.Videos.DETAIL_PROJECTION_INDEX_NAME) {
                    // text view with video title
                    TextView titleView = ButterKnife.findById(view, R.id.videos_list_item_title);
                    titleView.setText(cursor.getString(i)); // set text from cursor data
                    // tag view with youtube video key
                    view.setTag(cursor.getString(MoviesContract.Videos.DETAIL_PROJECTION_INDEX_KEY));
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // start video watching, key of the video we extract from view's tag
                            Utils.watchYoutubeVideo(getContext(), (String) view.getTag());
                        }
                    });
                    return true;
                }
                return false;
            }
        });

        // adapter for reviews
        mReviewsListAdapter = new SimpleCursorAdapter(getContext(),
                R.layout.reviews_list_item,
                null, // cursor
                new String[]{MoviesContract.Reviews.COLUMN_NAME_AUTHOR, MoviesContract.Reviews.COLUMN_NAME_CONTENT},
                new int[]{R.id.reviews_list_item_author, R.id.reviews_list_item_content},
                0);
        // we need separate view binder because our view is not a simple text
        mReviewsListAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int i) {
                if (i == MoviesContract.Reviews.DETAIL_PROJECTION_INDEX_CONTENT) {
                    ((ExpandableTextView) view).setText(cursor.getString(i));
                    return true;
                }
                return false;
            }
        });

        isFavorite = false;

        // set onClick listener for poster image
        // click on poster image - call to full poster image view via callback in the activity
        mPosterImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPosterName != null) {
                    ((DetailFragment.Callback) getContext()).onImageClicked((int) movieId, mPosterName, mMovieTitle);
                }
            }
        });

        subscription.add(mRepository
                .getMovie(movieId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Movie>() {
                    @Override
                    public void accept(Movie movie) throws Exception {
                        updateUi(movie);
                    }
                }));

        return rootView;
    }

    @Override
    public void onDestroyView() {
        subscription.dispose();
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
            Utils.setFavoriteIcon(isFavorite, mMenu);
            // update favorites table via service on work thread
            CacheUpdateService.startActionUpdateFavorite(getActivity(), movieId, isFavorite);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // starting intent services to update cache tables
    void refreshCurrentMovie() {
    }

    // swipe refresh layout callback
    @Override
    public void onRefresh() {
        refreshCurrentMovie();
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
        void onImageClicked(int movieId, String posterName, String movieTitle);
    }

    /**
     * Method to update share action intent after loading movie data
     *
     * @param title
     * @param poster
     */
    void updateShareAction(String title, String poster) {
        // create intent
        Intent myShareIntent = Utils.getShareIntent(getContext(), title, poster);
        // set intent into provider
        if (mShareActionProvider != null) mShareActionProvider.setShareIntent(myShareIntent);
    }

    private void updateUi(Movie movie) {
        // update variables for share action provider
        mPosterName = movie.getPosterPath();
        mMovieTitle = movie.getOriginalTitle();

        if (mMenu != null)
            updateShareAction(mMovieTitle, mPosterName);
        // load poster image
        Picasso.with(getActivity())
                .load(Utils.getPosterSmallUri(mPosterName))
                .into(mPosterImage);

        // Setting all view's content
        mTitleText.setText(movie.getOriginalTitle());
        mReleasedText.setText(movie.getReleaseDate());

        // TODO: make mRuntimeText formatting more reliable
        if (movie.getRunTime() > 0) {
            mRuntimeText.setText(String.format("%d %s",
                    movie.getRunTime(),
                    getString(R.string.runtime_minutes_text)));
        } else {
            mRuntimeText.setText("-");
        }
        
        mRatingText.setText(String.format("%.1f/10", movie.getVoteAverage()));

        // Set homepage link, if present
        String homePage = movie.getHomePage();
        if (homePage != null && homePage.length() > 0) {
            mHomepageText.setText("Homepage: " + homePage);
            mHomepageText.setVisibility(View.VISIBLE);
        } else {
            mHomepageText.setVisibility(View.GONE);
        }

        mOverviewText.setText(movie.getOverview());
    }
}
