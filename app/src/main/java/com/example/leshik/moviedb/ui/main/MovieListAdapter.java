package com.example.leshik.moviedb.ui.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.example.leshik.moviedb.R;
import com.example.leshik.moviedb.data.MovieListType;
import com.example.leshik.moviedb.data.PreferenceStorage;
import com.example.leshik.moviedb.data.interfaces.PreferenceInterface;
import com.example.leshik.moviedb.data.model.MovieListViewItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.v7.widget.RecyclerView.NO_ID;

/**
 * Created by alex on 3/7/17.
 */

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ViewHolder> {
    private Context context;
    private List<MovieListViewItem> movieList;
    private Map<Long, MovieListViewItem> movieMap;
    private PreferenceInterface prefStorage;
    private MovieListType listType;

    public MovieListAdapter(Context context, MovieListType listType) {
        this.context = context;
        prefStorage = PreferenceStorage.getInstance(context.getApplicationContext());
        this.listType = listType;
        this.movieList = new ArrayList<>();

        if (listType.isLocalOnly()) {
            this.movieMap = new TreeMap<>();
        }
    }

    public void updateListItem(MovieListViewItem newItem) {
        if (listType.isLocalOnly()) updateMovieMap(newItem);
        else updateMovieList(newItem);
    }

    private void updateMovieMap(MovieListViewItem newItem) {
        if (newItem.listPosition < 0) { // delete item from map
            movieMap.remove(newItem.movieId);
        } else { // add item to map
            movieMap.put(newItem.movieId, newItem);
        }

        movieList = new ArrayList<>(movieMap.values());
        notifyDataSetChanged();
    }

    private void updateMovieList(MovieListViewItem newItem) {
        if (movieList.size() <= newItem.listPosition) {
            movieList.add(newItem);
            notifyItemInserted(newItem.listPosition);
        } else {
            movieList.set(newItem.listPosition, newItem);
            notifyItemChanged(newItem.listPosition);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_list_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (movieList != null) {
            MovieListViewItem movie = movieList.get(position);
            Picasso.with(context)
                    .load(prefStorage.getPosterSmallUri(movie.posterPath))
                    .into(holder.mPosterView);

            // set item click listener
            final long movieId = movie.movieId;
            final ImageView posterView = holder.mPosterView;
            holder.mPosterView.setOnClickListener(new AdapterView.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Call callback interface method to start detail activity with cursor data
                    ((MovieListFragment.Callback) context).
                            onMovieListItemSelected(movieId, posterView);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (movieList != null) return movieList.size();
        else return 0;
    }

    @Override
    public long getItemId(int position) {
        if (movieList != null) return movieList.get(position).movieId;
        else return NO_ID;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.poster_image)
        ImageView mPosterView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
