package com.example.leshik.moviedb.ui.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.leshik.moviedb.R;
import com.example.leshik.moviedb.data.MovieListRepository;
import com.example.leshik.moviedb.data.MovieListType;
import com.example.leshik.moviedb.data.model.MovieListViewItem;
import com.example.leshik.moviedb.ui.viewmodels.MovieListViewModel;
import com.example.leshik.moviedb.utils.FirebaseUtils;
import com.example.leshik.moviedb.utils.ViewUtils;
import com.google.firebase.analytics.FirebaseAnalytics;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.functions.Consumer;


/**
 * Main screen fragment with list of movie's posters,
 * placed in a RecycleView.
 * There are three types of this fragment for popular, top rated and favorites lists
 * <p>
 */
public class MovieListFragment extends Fragment {
    private static final String TAG = "MovieListFragment";
    public static final String ARG_FRAGMENT_TYPE = "FRAGMENT_TYPE";

    private static final MovieListType DEFAULT_FRAGMENT_TYPE = MovieListType.Favorite;

    // views in the fragment
    @BindView(R.id.movies_list)
    protected RecyclerView mRecyclerView;
    private Unbinder unbinder;

    private RecyclerView.LayoutManager mLayoutManager;

    private MovieListAdapter mAdapter;

    private MovieListViewModel viewModel;
    private Disposable subscription;

    private FirebaseAnalytics mFirebaseAnalytics;


    // TODO: 3/7/17 Change arguments to MovieListType enum
    public static MovieListFragment newInstance(int listType) {
        MovieListFragment fragment = new MovieListFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_FRAGMENT_TYPE, listType);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MovieListType fragmentType = getFragmentTypeFromArgs();
        viewModel = new MovieListViewModel(fragmentType,
                new MovieListRepository(getActivity().getApplicationContext()));

        // Inflate fragment
        View rootView = inflater.inflate(R.layout.main_fragment, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        // Create layout manager and attach it to recycle view
        mLayoutManager = new GridLayoutManager(getActivity(), ViewUtils.calculateNoOfColumns(getActivity()));
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Construct empty adapter ...
        mAdapter = new MovieListAdapter(getActivity(), fragmentType);
        mAdapter.setHasStableIds(true);
        // ... and set it to recycle view
        mRecyclerView.setAdapter(mAdapter);

        subscribeToEndlessList();

        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT,
                FirebaseUtils.createAnalyticsSelectBundle(TAG, "Create Movie List Fragment", fragmentType.toString()));

        return rootView;
    }

    private MovieListType getFragmentTypeFromArgs() {
        Bundle args = getArguments();
        if (args != null) {
            int typeFromIntent = args.getInt(ARG_FRAGMENT_TYPE);
            if (typeFromIntent == MovieListType.Popular.getIndex()) return MovieListType.Popular;
            else if (typeFromIntent == MovieListType.Toprated.getIndex())
                return MovieListType.Toprated;
            else if (typeFromIntent == MovieListType.Upcoming.getIndex())
                return MovieListType.Upcoming;
            else if (typeFromIntent == MovieListType.Favorite.getIndex())
                return MovieListType.Favorite;
            else throw new IllegalArgumentException("Incorrect fragment type argument");
        } else return DEFAULT_FRAGMENT_TYPE;
    }

    private void subscribeToEndlessList() {
        subscription = viewModel.getMovieList(getScrollObservable(mRecyclerView, 20, 0))
                .subscribe(new Consumer<MovieListViewItem>() {
                    @Override
                    public void accept(@NonNull MovieListViewItem movieListViewItem) throws Exception {
                        mAdapter.updateListItem(movieListViewItem);
                    }
                });
    }

    @Override
    public void onDestroyView() {
        unsubscribeFromMovieList();
        super.onDestroyView();
        unbinder.unbind();
    }

    private void unsubscribeFromMovieList() {
        if (subscription != null && !subscription.isDisposed()) subscription.dispose();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        void onMovieListItemSelected(long movieId, ImageView posterView);
    }

    /**
     * Source - https://habrahabr.ru/post/271875/
     *
     * @param recyclerView
     * @param limit
     * @param emptyListCount
     * @return
     */

    private Observable<Integer> getScrollObservable(final RecyclerView recyclerView, final int limit, final int emptyListCount) {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull final ObservableEmitter<Integer> subscriber) throws Exception {
                final RecyclerView.OnScrollListener sl = new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        if (!subscriber.isDisposed()) {
                            int position = getLastVisibleItemPosition(recyclerView);
                            int updatePosition = recyclerView.getAdapter().getItemCount() - 1 - (limit / 2);
                            if (position >= updatePosition) {
                                subscriber.onNext(recyclerView.getAdapter().getItemCount());
                            }
                        }
                    }
                };
                recyclerView.addOnScrollListener(sl);
                subscriber.setDisposable(Disposables.fromRunnable(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.removeOnScrollListener(sl);
                    }
                }));
                if (recyclerView.getAdapter().getItemCount() == emptyListCount) {
                    subscriber.onNext(recyclerView.getAdapter().getItemCount());
                }
            }
        });
    }

    private int getLastVisibleItemPosition(RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        return layoutManager.findLastVisibleItemPosition();

    }
}
