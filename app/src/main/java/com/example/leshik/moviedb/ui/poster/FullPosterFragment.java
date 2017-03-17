package com.example.leshik.moviedb.ui.poster;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.leshik.moviedb.R;
import com.example.leshik.moviedb.data.MovieRepository;
import com.example.leshik.moviedb.data.PreferenceStorage;
import com.example.leshik.moviedb.data.interfaces.PreferenceInterface;
import com.example.leshik.moviedb.data.model.Movie;
import com.example.leshik.moviedb.ui.viewmodels.MovieViewModel;
import com.example.leshik.moviedb.utils.ViewUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FullPosterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FullPosterFragment extends Fragment {
    private static final String TAG = "FullPosterFragment";
    // state and fragment's arguments markers
    private static final String ARG_MOVIE_ID = "ARG_MOVIE_ID";

    private long movieId;
    private Movie lastMovie;

    private Menu mMenu;
    private ShareActionProvider mShareActionProvider;
    private boolean isFavorite;
    private String mMovieTitle;
    private String mPosterName;

    @BindView(R.id.full_poster_image)
    protected PhotoView mPosterImage;
    @BindView(R.id.full_poster_progressbar)
    protected ContentLoadingProgressBar mProgressBar;

    private Unbinder unbinder;
    PhotoViewAttacher attacher;

    MovieViewModel mViewModel;
    CompositeDisposable subscription = new CompositeDisposable();

    PreferenceInterface prefStorage;

    public FullPosterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param movie_id - id onf the movie
     * @return A new instance of fragment FullPosterFragment.
     */
    public static FullPosterFragment newInstance(long movie_id) {
        FullPosterFragment fragment = new FullPosterFragment();
        Bundle args = new Bundle();
        // set variables to argument's bundle
        args.putLong(ARG_MOVIE_ID, movie_id);
        // create fragment
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.poster_fragment, menu);
        mMenu = menu;

        // Setup share provider
        // and update share action intent
        if (menu != null) {
            mShareActionProvider = new ShareActionProvider(getContext());
            MenuItemCompat.setActionProvider(menu.findItem(R.id.action_share), mShareActionProvider);
            updateShareAction(mMovieTitle, mPosterName);

            ViewUtils.setFavoriteIcon(isFavorite, mMenu);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // set state variables from saved state or argument's bundle
        if (savedInstanceState == null) {
            if (getArguments() != null) {
                movieId = getArguments().getLong(ARG_MOVIE_ID, -1);
            }
        } else {
            movieId = savedInstanceState.getLong(ARG_MOVIE_ID);
        }

        mViewModel = new MovieViewModel(movieId, new MovieRepository(getActivity().getApplicationContext()));
        prefStorage = PreferenceStorage.getInstance(getActivity().getApplicationContext());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(ARG_MOVIE_ID, movieId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_favorite) {
            // toggle favorite flag
            isFavorite = !isFavorite;
            // change mark on the toolbar
            ViewUtils.setFavoriteIcon(isFavorite, mMenu);
            // update favorite flag in the db
            mViewModel.invertFavorite();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_full_poster, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        attacher = new PhotoViewAttacher(mPosterImage);
        attacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                onImageClicked();
            }

            @Override
            public void onOutsidePhotoTap() {
                onImageClicked();
            }
        });

        mProgressBar.show();

        subscribeToMovie();

        return rootView;
    }

    @Override
    public void onDestroyView() {
        unsubscribeFromMovie();
        unbinder.unbind();

        super.onDestroyView();
    }

    interface OnImageClickCallback {
        void onImageClicked();
    }

    private void onImageClicked() {
        ((FullPosterActivity) getActivity()).onImageClicked();
    }

    private void updateUi(Movie movie) {
        // update variables for share action provider
        mPosterName = movie.getPosterPath();
        mMovieTitle = movie.getOriginalTitle();

        if (mMenu != null)
            updateShareAction(mMovieTitle, mPosterName);

        if (isPosterChanged(movie)) {
            Picasso.with(getActivity())
                    .load(prefStorage.getPosterFullUri(movie.getPosterPath()))
                    .into(mPosterImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            mProgressBar.hide();
                            attacher.update();
                        }

                        @Override
                        public void onError() {
                        }
                    });
        }

        // Favorite mark set
        isFavorite = false;
        if (movie.getFavoritePosition() != null && movie.getFavoritePosition() >= 0) {
            isFavorite = true;
        }
        // and update it
        ViewUtils.setFavoriteIcon(isFavorite, mMenu);

        lastMovie = movie;
    }

    private boolean isPosterChanged(Movie newMovie) {
        return lastMovie == null || !lastMovie.getPosterPath().equals(newMovie.getPosterPath());
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
}
